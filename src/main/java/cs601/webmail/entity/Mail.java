package cs601.webmail.entity;

import cs601.webmail.frameworks.db.annotation.Column;
import cs601.webmail.frameworks.db.annotation.Entity;
import cs601.webmail.frameworks.db.annotation.Id;
import cs601.webmail.frameworks.db.annotation.Table;

import java.io.Serializable;
import cs601.webmail.util.Strings;

/**
 * Created by yuanyuan on 10/25/14.
 */
@Entity
@Table(tableName = "emails")
public class Mail implements Serializable{

    public static final int NO_FLAG         = 0;

    public static final int FLAG_NEW        = 1;
    public static final int FLAG_UNREAD     = 2;

    public static final int FLAG_TRASH      = 4;
    public static final int FLAG_DELETE     = 8;

    public static final int FLAG_YES = 1;
    public static final int FLAG_NO = 0;

    public static enum VirtualFolder {

        inbox("inbox"),

        // alias of fav
        starred("inbox", "fav"),

        fav("inbox", "fav"),

        trash("inbox", "trash"),

        sent("sent");

        private String sys_folder;
        private String v_folder;
        private boolean is_virtual = true;

        VirtualFolder(String folder) {
            this(folder, folder);
            this.is_virtual = false;
        }

        VirtualFolder(String sys_folder, String virtual_folder) {
            this.sys_folder = sys_folder;
            this.v_folder = virtual_folder;
        }

        public String getSystemFolder() {
            return sys_folder;
        }

        public String getVirtualFolder() {
            return v_folder;
        }

        public boolean isVirtualFolder() {
            return is_virtual;
        }

        public static VirtualFolder parseFolder(String fn) {
            if (!Strings.haveLength(fn)) {
                return null;
            }

            for (VirtualFolder f : VirtualFolder.values()) {
                if (f.toString().equalsIgnoreCase(fn)) {
                    return f;
                }
            }

            return null;
        }
    }

    @Id
    @Column(columnName = "MSGID", propertyName = "id")
    private Long id;

    @Column(columnName = "ACCOUNTID", propertyName = "accountId")
    private Long accountId;

    @Column(columnName = "USERSID", propertyName = "userId")
    private Long userId;

    @Column(columnName = "SUBJECT", propertyName = "subject")
    private String subject;

    @Column(columnName = "MFROM", propertyName = "from")
    private String from;

    @Column(columnName = "MTO", propertyName = "to")
    private String to;

    @Column(columnName = "DATE", propertyName = "date")
    private String date;

    @Column(columnName = "CONTENT", propertyName = "content")
    private String content;

    @Column(columnName = "READ", propertyName = "read")
    private boolean read = false;

    @Column(columnName = "MESSAGE_ID", propertyName = "messageId")
    private String messageId;

    @Column(columnName = "CONTENT_TYPE", propertyName = "contentType")
    private String contentType = "text/plain";

    // Unique ID assigned for each mail when it has received by mail server
    @Column(columnName = "UID", propertyName = "uid")
    private String uid;

    @Deprecated
    @Column(columnName = "FLAGS", propertyName = "flags")
    private int flags = 0;

    @Column(columnName = "FLAG_NEW", propertyName = "flagNew")
    private int flagNew = FLAG_YES;

    @Column(columnName = "FLAG_UNREAD", propertyName = "flagUnread")
    private int flagUnread = FLAG_YES;

    @Column(columnName = "FLAG_FAV", propertyName = "flagFav")
    private int flagFav = FLAG_NO;

    // flag for mail if has been removed
    // flagDel = 0 // it's ok, to represent this normally.
    // flagDel = 1 // moved to trash
    // flagDel = 2 // deleted in client, also not display for browser.
    @Column(columnName = "FLAG_DEL", propertyName = "flagDel")
    private int flagDel = 0;

    // this belongs to which email address
    @Column(columnName = "OWNER_ADDRESS", propertyName = "ownerAddress")
    private String ownerAddress;

    @Column(columnName = "FOLDER", propertyName = "folder")
    private String folder;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public int getFlagDel() {
        return flagDel;
    }

    public void setFlagDel(int flagDel) {
        this.flagDel = flagDel;
    }

    public int getFlagFav() {
        return flagFav;
    }

    public void setFlagFav(int flagFav) {
        this.flagFav = flagFav;
    }

    public static int getNoFlag() {
        return NO_FLAG;
    }

    public int getFlagUnread() {
        return flagUnread;
    }

    public void setFlagUnread(int flagUnread) {
        this.flagUnread = flagUnread;
    }

    public int getFlagNew() {
        return flagNew;
    }

    public void setFlagNew(int flagNew) {
        this.flagNew = flagNew;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isNew() {
        return (flags & FLAG_NEW) == FLAG_NEW;
    }

    public boolean isUnread() {
        return (flags & FLAG_UNREAD) == FLAG_UNREAD;
    }

    // user flagNew or flagUnread directly
    @Deprecated
    public int markFlag(int flag) {
        return flags = flags | flag;
    }

    @Override
    public String toString() {
        return String.format("[Date=%s, Subject=%s, From=%s]", getDate(), getSubject(), getFrom());
    }
}
