package cs601.webmail.entity;

import cs601.webmail.db.annotation.Column;
import cs601.webmail.db.annotation.Id;
import cs601.webmail.db.annotation.Table;

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
    private long userId;

    @Column(columnName = "EMAIL_ADDRESS", propertyName = "emailUsername")
    private String emailUsername;

    @Column(columnName = "EPASS", propertyName = "emailPassword")
    private String emailPassword;

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

    public long getUserId() {
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

