package cs601.webmail.entity;

import cs601.webmail.frameworks.db.annotation.Column;
import cs601.webmail.frameworks.db.annotation.Entity;
import cs601.webmail.frameworks.db.annotation.Id;
import cs601.webmail.frameworks.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yuanyuan on 10/27/14.
 */
@Entity
@Table(tableName = "ACCOUNTS")
public class Account implements Serializable {

    @Id
    @Column(columnName = "AID", propertyName = "id")
    private Long id;

    @Column(columnName = "USERID", propertyName = "userId")
    private Long userId;

    @Column(columnName = "EMAIL_ADDRESS", propertyName = "emailUsername")
    private String emailUsername;

    @Column(columnName = "EPASS", propertyName = "emailPassword")
    private String emailPassword;

    @Column(columnName = "POP_SERVER", propertyName = "popServer")
    private String popServer;

    // Default 110, TLS 995
    @Column(columnName = "POP_SERVER_PORT", propertyName = "popServerPort")
    private Integer popServerPort = 110;

    @Column(columnName = "ENABLE_SSL", propertyName = "enableSsl")
    private boolean enableSsl = false;

    @Column(columnName = "SMTP_SERVER", propertyName = "smtpServer")
    private String smtpServer;

    @Column(columnName = "SMTP_SERVER_PORT", propertyName = "smtpServerPort")
    private Integer smtpServerPort = 25;

    @Column(columnName = "ENABLE_SMTP_SSL", propertyName = "enableSmtpSsl")
    private boolean enableSmtpSsl = false;

    @Column(columnName = "DISPLAY_NAME", propertyName = "displayName")
    private String displayName;

    @Column(columnName = "MAIL_SIGNATURE", propertyName = "mailSignature")
    private String mailSignature;

    public String getMailSignature() {
        return mailSignature;
    }

    public void setMailSignature(String mailSignature) {
        this.mailSignature = mailSignature;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnableSmtpSsl() {
        return enableSmtpSsl;
    }

    public void setEnableSmtpSsl(boolean enableSmtpSsl) {
        this.enableSmtpSsl = enableSmtpSsl;
    }

    public Integer getSmtpServerPort() {
        return smtpServerPort;
    }

    public void setSmtpServerPort(Integer smtpServerPort) {
        this.smtpServerPort = smtpServerPort;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public Integer getPopServerPort() {
        return popServerPort;
    }

    public void setPopServerPort(Integer popServerPort) {
        this.popServerPort = popServerPort;
    }

    public String getPopServer() {
        return popServer;
    }

    public void setPopServer(String popServer) {
        this.popServer = popServer;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmailUsername() {
        return emailUsername;
    }

    public void setEmailUsername(String emailUsername) {
        this.emailUsername = emailUsername;
    }

    @Override
    public String toString() {
        return String.format("Account[id=%d, userId=%d, email=%s]", id, userId, emailUsername);
    }
}
