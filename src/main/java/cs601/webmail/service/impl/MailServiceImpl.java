package cs601.webmail.service.impl;

import cs601.webmail.Constants;
import cs601.webmail.dao.MailDao;
import cs601.webmail.dao.impl.MailDaoImpl;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.db.page.Page;
import cs601.webmail.frameworks.db.page.PageRequest;
import cs601.webmail.frameworks.mail.pop3.*;
import cs601.webmail.service.MailService;
import cs601.webmail.service.ServiceException;
import cs601.webmail.util.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);

    public MailServiceImpl() {
        mailDao = new MailDaoImpl();
    }

    private MailDao mailDao;

    @Override
    public void save(Mail mail) {
        if (mailDao == null) {
            throw new IllegalStateException("MailDao missed");
        }
        mailDao.save(mail);
    }

    @Override
    public void save(List<Mail> mails) {
        if (mails != null && mails.size() > 0) {
            for (Mail m : mails) {
                save(m);
            }
        }
    }

    public void trash(Mail mail) {
        if (mail != null) {
            mail.setFlagDel(Mail.FLAG_TRASH);
            mailDao.save(mail);
        }
    }

    @Override
    public void delete(Mail mail) {
        if (mail != null) {

            mail.setFlagDel(Mail.FLAG_DELETE);

            mailDao.save(mail);
        }
    }

    @Override
    public Mail findById(long id) {
        return mailDao.findById(id);
    }

    @Override
    public List<Mail> searchByTerm(Account account,String term,String keyword){

        if (!Strings.haveLength(term)) {
            throw new IllegalArgumentException("'term' incorrect");
        }
        if (!Strings.haveLength(keyword)) {
            throw new IllegalArgumentException("'keyword' incorrect");
        }

        StringBuilder sb=new StringBuilder();

        //search scope: Normal, Trash
        sb.append(String.format("OWNER_ADDRESS = '%s' and ACCOUNTID = %d" +
                        " and USERSID = %d and FLAG_DEL <= %d",
                account.getEmailUsername(),
                account.getId(),
                account.getUserId(),
                Mail.FLAG_TRASH));

        if ("subject".equalsIgnoreCase(term)) {
            sb.append(" and SUBJECT like '%").append(keyword).append("%'");
        }
        else if ("from".equalsIgnoreCase(term)) {
            sb.append(" and MFROM like '%").append(keyword).append("%'");
        }
        else {
            throw new ServiceException("Not supported search term:" + term);
        }

        // only search INBOX
        sb.append(" and FOLDER like '").append(
                Mail.VirtualFolder.inbox.getSystemFolder()).append("'");

        PageRequest pr = new PageRequest();
        pr.page = 1;
        pr.pageSize = Integer.MAX_VALUE;
        Page<Mail> page = mailDao.findPageByConditions(pr, sb.toString());

        return page != null ? page.getPageList() : new ArrayList<Mail>();
    }

    @Deprecated
    @Override
    public Page<Mail> findByAccountAndPage(Account account, PageRequest pageRequest) {
        return mailDao.findByPage(pageRequest, account.getId(), account.getUserId());
    }

    @Override
    @Deprecated
    public Page<Mail> findPage(String folder, Account account, PageRequest pageRequest) {

        Mail.VirtualFolder vf = Mail.VirtualFolder.parseFolder(folder);

        if (folder == null || vf == null) {
            throw new IllegalArgumentException();
        }
        return findPage (vf, account, pageRequest);
    }
    @Override
    public Page<Mail> findPage(Mail.VirtualFolder folder, Account account, PageRequest pageRequest) {

        String condition=null;
        if(folder==Mail.VirtualFolder.fav){
            // return all fav flag mail but FLAG_DEL = FLAG_DELETE
            condition = String.format("OWNER_ADDRESS = '%s' and ACCOUNTID = %d" +
                    " and USERSID = %d and FLAG_FAV > 0 and FLAG_DEL <> %d",
                    account.getEmailUsername(),
                    account.getId(),
                    account.getUserId(),
                    Mail.FLAG_DELETE);
        }
        else if (folder==Mail.VirtualFolder.trash) {

            condition = String.format("OWNER_ADDRESS = '%s' and ACCOUNTID = %d" +
                            " and USERSID = %d and FLAG_DEL = %d",
                    account.getEmailUsername(),
                    account.getId(),
                    account.getUserId(),
                    Mail.FLAG_TRASH);
        }
        // 'inbox' as default.
        // of course, excluded those who's FLAG_DEL not equals FLAG_NO
        else {
            condition = String.format("OWNER_ADDRESS = '%s' and ACCOUNTID = %d and USERSID = %d and FLAG_DEL = %d",
                    account.getEmailUsername(),
                    account.getId(),
                    account.getUserId(),
                    Mail.FLAG_NO);
        }
        condition += " and FOLDER like '" + folder.getSystemFolder() + "'";
        return mailDao.findPageByConditions(pageRequest, condition);
    }

    /**
     * 1. read local UID list
     * 2. pull remote UID list
     * 3. get intersection, skip it
     * 4. save remote
     * 5. delete local
     *
     * @return Updated mails' count if have
     */
    public int syncMails(Account account) {
        try {
            return doSyncMails(account);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    private int doSyncMails(Account account) throws IOException {

        String popServer = account.getPopServer();
        int popPort = account.getPopServerPort();
        boolean sslEnable = account.isEnableSsl();

        String username = account.getEmailUsername();

        String password = EncryptUtils.decryptFromHex(account.getEmailPassword(), Constants.DEFAULT_AES_CIPHER_KEY);

        Pop3Client client = Pop3Client.createInstance(popServer, popPort, sslEnable);

        client.login(username, password);

        List<String> currentUIDs = mailDao.findMailUIDs(account.getId());

        Map<String, Long> localUIDMap = _parseLocalUIDMap(currentUIDs);
        //Collection<String> localUIDs = localUIDMap.keySet();

        Pop3MessageInfo[] remoteMsgInfos = client.listUniqueIdentifiers();
        //Collection<String> remoteUIDs = _getRemoteUIDs(remoteMsgInfos);

        if (remoteMsgInfos == null || remoteMsgInfos.length == 0) {
            return 0;
        }

        int updatedCount = 0;

        for (int i = 0, len = remoteMsgInfos.length; i < len; i++) {
            Pop3MessageInfo msgInfo = remoteMsgInfos[i];
            String rmtUID = msgInfo.identifier;

            // skip existed mail
            if (localUIDMap.containsKey(rmtUID)) {
                LOGGER.debug("UID existed " + rmtUID);
                continue;
            }

            // attach a listener to get whole mail datagram content.
            MailContentListener listener = new MailContentListener();
            client.addListener(listener);

            // fetch it
            Pop3Message message = client.getMessage(msgInfo.number);

            // ok, remove it.
            client.removeListener(listener);

            Mail mail = _parseMail(message);
            mail.setUid(rmtUID);

            mail.setAccountId(account.getId());
            mail.setUserId(account.getUserId());

            mail.setFlagNew(Mail.FLAG_YES);
            mail.setFlagUnread(Mail.FLAG_YES);
            mail.setOwnerAddress(account.getEmailUsername());
            mail.setFolder(Mail.VirtualFolder.inbox.getSystemFolder());

            LOGGER.debug("save mail to DB: " + mail);
            mailDao.save(mail);

            // save raw content to {WorkDir}/raw/SHA-1(mailAddress)/uid.dat
            // for example: /Users/foobar/webmail/raw/5897fe8711774cde9fae5af5bd39b1a4a42d6828/cDebdfdafd0122.dat
            String rawPath = ResourceUtils.resolveMailFolderPath(username, Mail.VirtualFolder.inbox);
            File rawFile = new File(rawPath + File.separator + DigestUtils.digestToSHA(rmtUID) + ".mx.txt");
            FileUtils.writeByteArrayToFile(rawFile, listener.getByteContent());

            updatedCount++;
        }

        if (client != null) {
            client.close();
        }

        return updatedCount;
    }

    private Collection<String> _getRemoteUIDs(Pop3MessageInfo[] remoteMsgInfos) {
        Set<String> set = new HashSet<String>();
        if (remoteMsgInfos != null && remoteMsgInfos.length > 0) {
            for (Pop3MessageInfo info : remoteMsgInfos) {
                set.add(info.identifier);
            }
        }
        return set;
    }

    private Mail _parseMail(Pop3Message message) {
        return new Pop3Extractor().extractMail(message);
    }

    // ID UID
    private Map<String, Long> _parseLocalUIDMap(List<String> currentUIDs) {
        Map<String, Long> ret = new HashMap<String, Long>();
        if (currentUIDs == null || currentUIDs.size() == 0) {
            return ret;
        }

        Iterator<String> itr = currentUIDs.iterator();

        while (itr.hasNext()) {
            String line = itr.next();
            String uid = line.split(" ")[1];
            Long id = Long.parseLong(line.split(" ")[0]);

            ret.put(uid, id);
        }

        return ret;
    }

    static class MailContentListener implements ClientListener {

        private StringBuilder buf;

        private static final String CR = "\r\n";

        MailContentListener() {
            this.buf = new StringBuilder();
        }

        @Override
        public void onEvent(EventType eventType,Object eventData){
            if(eventData!=null){
                buf.append(eventData.toString()).append(CR);
            }
        }
        @Override
        public boolean isAccepted(EventType eventType){
            return eventType==EventType.LineRead;
        }

        public byte[] getByteContent() {
            if (buf.length() == 0)
                return null;

            return buf.toString().getBytes();
        }
    }
}


