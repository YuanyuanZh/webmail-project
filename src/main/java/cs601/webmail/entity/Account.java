package cs601.webmail.entity;

import cs601.webmail.frameworks.db.annotation.Column;
import cs601.webmail.frameworks.db.annotation.Id;
import cs601.webmail.frameworks.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by yuanyuan on 10/26/14.
 */
@Table(tableName = "ACCOUNTS")
public class Account implements Serializable {

    @Id
    @Column(columnName = "AID", propertyName = "id")
    private long id;

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
    private int popServerPort;

    @Column(columnName = "ENABLE_SSL", propertyName = "enableSsl")
    private boolean enableSsl;

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public int getPopServerPort() {
        return popServerPort;
    }

    public void setPopServerPort(int popServerPort) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

