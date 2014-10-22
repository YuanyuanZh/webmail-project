package object;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class Account {

    private Integer  AID ;
    private Integer  USERID ;
    private String  EMAIL_ADDRESS ;
    private String  EPASS ;


    //AID
    public void setAid(Integer aid) {
        this.AID = aid;
    }

    public Integer getAid() {
        return AID;
    }

    //USERID
    public void setUserid(Integer userid) {
        this.USERID = userid;
    }

    public Integer getUserid() {
        return USERID;
    }

    //EMAIL_ADDRESS
    public void setEmail_Address(String email_address) {
        this.EMAIL_ADDRESS = email_address;
    }

    public String getEmail_Address() {
        return EMAIL_ADDRESS;
    }

    //EPASS
    public void setEpass(String epass) {
        this.EPASS = epass;
    }

    public String getEpass() {
        return EPASS;
    }



}