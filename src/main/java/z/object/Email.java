package z.object;

/**
 * Created by yuanyuan on 10/22/14.
 */
public class Email {

    private String  MSGID ;
    private String  SUBJECT ;
    private String  MFROM ;
    private String  MTO ;
    private String  CONTENT ;
    private String  DATE ;
    private Integer  USERSID ;
    private Integer  ACCOUNTID ;

    //MSGID
    public void setMsgid(String msgid) {
        this.MSGID = msgid;
    }

    public String getMsgid() {
        return MSGID;
    }

    //SUBJECT
    public void setSubject(String subject) {
        this.SUBJECT = subject;
    }

    public String getSubject() {
        return SUBJECT;
    }

    //MFROM
    public void setMfrom(String mfrom) {
        this.MFROM = mfrom;
    }

    public String getMfrom() {
        return MFROM;
    }

    //MTO
    public void setMto(String mto) {
        this.MTO = mto;
    }

    public String getMto() {
        return MTO;
    }

    //CONTENT
    public void setContent(String content) {
        this.CONTENT = content;
    }

    public String getContent() {
        return CONTENT;
    }

    //DATE
    public void setDate(String date) {
        this.DATE = date;
    }

    public String getDate() {
        return DATE;
    }

    //USERSID
    public void setUsersid(Integer usersid) {
        this.USERSID = usersid;
    }

    public Integer getUsersid() {
        return USERSID;
    }

    //ACCOUNTID
    public void setAccountid(Integer accountid) {
        this.ACCOUNTID = accountid;
    }

    public Integer getAccountid() {
        return ACCOUNTID;
    }



}