package object;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class User {
    private Integer  UID ;
    private String  PASS ;
    private String  FIRSTNAME ;
    private String  LASTNAME ;
    private String  LOGID ;

    //UID
    public void setUid(Integer uid) {
        this.UID = uid;
    }

    public Integer getUid() {
        return UID;
    }

    //PASS
    public void setPass(String pass) {
        this.PASS = pass;
    }

    public String getPass() {
        return PASS;
    }

    //FIRSTNAME
    public void setFirstname(String firstname) {
        this.FIRSTNAME = firstname;
    }

    public String getFirstname() {
        return FIRSTNAME;
    }

    //LASTNAME
    public void setLastname(String lastname) {
        this.LASTNAME = lastname;
    }

    public String getLastname() {
        return LASTNAME;
    }

    //LOGID
    public void setLogid(String logid) {
        this.LOGID = logid;
    }

    public String getLogid() {
        return LOGID;
    }

}
