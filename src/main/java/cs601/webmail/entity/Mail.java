package cs601.webmail.entity;

import cs601.webmail.db.annotation.Column;
import cs601.webmail.db.annotation.Id;
import cs601.webmail.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yuanyuan on 10/26/14.
 */
@Table(tableName = "emails")
public class Mail implements Serializable {

    public static final int NO_FLAG =0;
    public static final int FLAG_NEW =1;
    public static final int FLAG_UNREAD=2;
    public static final int FLAG_REMOVED=4;

    public static final int FLAG_YES=1;
    public static final int FLAG_NO=0;

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

    @Column(columnName = "CONTENT", propertyName = "body")
    private String body;

    @Column(columnName = "READ", propertyName = "read")
    private boolean read = false;

    @Column(columnName = "MESSAGE_ID", propertyName = "messageId")
    private String messageId;

    @Column(columnName = "CONTENT_TYPE", propertyName = "contentType")
    private String contentType = "text/plain";

    @Column(columnName = "UID",propertyName = "uid")
    private String uid;

    @Column(columnName = "FLAG_NEW", propertyName = "flagNew")
    private int flagNew = 1;

    @Column(columnName = "FLAG_UNREAD", propertyName = "flagUnread")
    private int flagUnread = 1;

    @Column(columnName = "FLAG_FAV", propertyName = "flagFav")
    private int flagFav = 0;


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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return String.format("[Date=%s, Subject=%s, From=%s]", getDate(), getSubject(), getFrom());
    }
}
