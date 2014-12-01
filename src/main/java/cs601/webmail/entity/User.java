package cs601.webmail.entity;

import cs601.webmail.frameworks.db.annotation.Column;
import cs601.webmail.frameworks.db.annotation.Entity;
import cs601.webmail.frameworks.db.annotation.Id;
import cs601.webmail.frameworks.db.annotation.Table;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuan on 10/27/14.
 */
@Entity
@Table(tableName = "users")
public class User implements Serializable {

    @Id
    @Column(columnName = "UID", propertyName = "id")
    private Long id;

    @Column(columnName = "LOGID", propertyName = "loginId")
    private String loginId;

    @Column(columnName = "PASS", propertyName = "password")
    private String password;

    @Column(columnName = "FIRSTNAME", propertyName = "firstName")
    private String firstName;

    @Column(columnName = "LASTNAME", propertyName = "lastName")
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    //------------------- static

    private static Map propAndColMap;

    @Deprecated
    public static Map generatePropertiesColumnsMap() {
        if (propAndColMap == null) {
            propAndColMap = new HashMap();
        }
        return propAndColMap;
    }
}
