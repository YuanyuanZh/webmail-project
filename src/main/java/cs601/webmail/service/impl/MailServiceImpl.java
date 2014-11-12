package cs601.webmail.service.impl;

import cs601.webmail.Constants;
import cs601.webmail.application.Configuration;
import cs601.webmail.dao.MailDao;
import cs601.webmail.dao.impl.MailDaojdbcImpl;
import cs601.webmail.entity.Account;
import cs601.webmail.entity.Mail;
import cs601.webmail.frameworks.mail.Pop3Client;
import cs601.webmail.frameworks.mail.Pop3Extractor;
import cs601.webmail.frameworks.mail.Pop3Message;
import cs601.webmail.frameworks.mail.Pop3MessageInfo;
import cs601.webmail.service.AccountService;
import cs601.webmail.service.MailService;
import cs601.webmail.service.ServiceException;
import cs601.webmail.frameworks.db.Page;
import cs601.webmail.frameworks.db.PageRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;



import java.io.IOException;
import java.util.*;

/**
 * Created by yuanyuan on 10/25/14.
 */
public class MailServiceImpl implements MailService {
    private static final Logger LOGGER = Logger.getLogger(MailServiceImpl.class);

    public MailServiceImpl() {
        mailDao=new MailDaojdbcImpl();
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

    @Override
    public void delete(Mail mail) {
        if (mail != null) {
            mailDao.removeByUID(mail.getUid());
        }
    }

    @Override
    public Mail findById(long id) { return mailDao.findById(id); }


    @Override
    public Page<Mail> findByAccountAndPage(Account account, PageRequest pageRequest) {
        return mailDao.findByPage(pageRequest, account.getId(), account.getUserId());
    }

    @Override
    public List<Mail> findByFolder(String folder) {
        return null;
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
        String popServer= Configuration.getDefault().get("pop");
        int popPort=Configuration.getDefault().getInteger("pop.port");
        boolean sslEnable=Configuration.getDefault().getBoolean("pop.ssl");
        String username=Configuration.getDefault().get("email");
        String password=Configuration.getDefault().get("password");

        Pop3Client client = Pop3Client.createInstance(popServer,popPort,sslEnable);

        List<String> currentUIDs = mailDao.findAllMailUIDs();
        client.login(username, password);

        Map<String, Long> localUIDMap = _parseLocalUIDMap(currentUIDs);
        Collection<String> localUIDs = localUIDMap.keySet();

        Pop3MessageInfo[] remoteMsgInfos = client.listUniqueIdentifiers();
        Collection<String> remoteUIDs = _getRemoteUIDs(remoteMsgInfos);

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

            // save remote
            Pop3Message message = client.getMessage(msgInfo.number);

            Mail mail = _parseMail(message);
            mail.setUid(rmtUID);

            mail.setAccountId(account.getId());
            mail.setUserId(account.getUserId());

            mail.setFlagNew(Mail.FLAG_YES);
            mail.setFlagUnread(Mail.FLAG_YES);

            LOGGER.debug("save mail to DB: " + mail);
            mailDao.save(mail);

            updatedCount++;
        }

        // To remove local mail which was deleted from remote server
        Collection<String> localDeleteUIDs = CollectionUtils.subtract(localUIDs,
                CollectionUtils.intersection(localUIDs, remoteUIDs));

        if (localDeleteUIDs.size() > 0) {
            for (String uid : localDeleteUIDs) {
                mailDao.removeByUID(uid);

                if (Constants.DEBUG_MODE) {
                    LOGGER.debug("MAIL removed, UID=" + uid);
                }
            }
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

}
