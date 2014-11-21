package cs601.webmail.entity;
import cs601.webmail.frameworks.db.annotation.Column;
import cs601.webmail.frameworks.db.annotation.Entity;
import cs601.webmail.frameworks.db.annotation.Id;
import cs601.webmail.frameworks.db.annotation.Table;
import java.io.Serializable;

/**
 * Created by yuanyuan on 11/19/14.
 */
@Entity
@Table(tableName = "contacts")
public class Contact implements Serializable {

    public static final int FLAG_YES        = 1;
    public static final int FLAG_NO         = 0;

    public static final int FLAG_DISABLED   = 4;
    public static final int FLAG_DELETE     = 8;


    @Id
    private Long id;

    // FK
    @Column(columnName = "user_id", propertyName = "userId")
    private Long userId;

    @Column(columnName = "full_name", propertyName = "fullName")
    private String fullName;

    @Column
    private String email;

    @Column
    private String address;

    @Column
    private String zipcode;

    @Column
    private String phone;

    @Column
    private String avatar;

    @Column(columnName = "flag_fav", propertyName = "flagFav")
    private int flagFav = FLAG_NO;

    @Column(columnName = "flag_del", propertyName = "flagDel")
    private int flagDel = FLAG_NO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFlagFav() {
        return flagFav;
    }

    public void setFlagFav(int flagFav) {
        this.flagFav = flagFav;
    }

    public int getFlagDel() {
        return flagDel;
    }

    public void setFlagDel(int flagDel) {
        this.flagDel = flagDel;
    }
}
