package service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class smtpMail {
    private String mailServer;
    private String from;
    private String to;
    private String content;
    private String enter = "\r\n";
    private int port = 25;
    private Socket client;
    private BufferedReader in;
    private DataOutputStream os;
    public String getContent() {  return content;  }
    public void setContent(String content) {  this.content = content;  }
    public String getMailServer() {  return mailServer;  }
    public void setMailServer(String mailServer) {  this.mailServer = mailServer;  }
    public String getFrom() {  return from;}
    public void setFrom(String from) {  this.from = from;  }
    public String getTo() {  return to;  }
    public void setTo(String to) {  this.to = to;  }

    //initial connection
    private boolean init(){
        boolean boo=true;
        if(mailServer==null ||"".equals(mailServer)){
            return false;
        }
        try{
            client=new Socket(mailServer,port);
            in=new BufferedReader(new InputStreamReader(client.getInputStream()));
            os=new DataOutputStream(client.getOutputStream());
            String isConnect=response();
            if(isConnect.startsWith("220")){

            }else {
                boo=false;
                System.out.println("failed to establish connection"+isConnect);
            }
        }catch (UnknownHostException e){
            System.out.println("failed to establish connection");
            e.printStackTrace();
            boo=false;
        }catch (IOException e){
            System.out.println("failed to read stream");
            e.printStackTrace();
            boo=false;
        }
        return boo;
    }

    //send smtp command and return server response

    private String sendCommand(String msg)throws IOException{
        String answer=null;
        os.writeBytes(msg);
        os.flush();
        answer =response();
        return answer;

    }

    private String response()throws IOException{
        String answer=null;
        answer=in.readLine();
        return answer;
    }

    //close
    private void close()throws IOException{
        os.close();
        in.close();
        client.close();

    }

    //send email

    private boolean sendMail()throws IOException{

        if(client==null){
            if(init()){
            }
            else{
                return false;
            }
        }

        if(from==null || from.isEmpty()||to==null||to.isEmpty()){
            return false;
        }
        //handshake
        String result=sendCommand("HELO "+mailServer+enter);

        if(isStartWith(result,"250")){
            System.out.println("handshake successfully");

        }else{
            System.out.println("filed to handshake"+result);
            return false;
        }

        //verify sender information

        /*String auth = sendCommand("AUTH LOGIN"+enter);
        if(isStartWith(auth,"334")){
            System.out.println("verify successfully");

        }else {
            System.out.println("filed to verify");
            return false;

        }
        String user=sendCommand(new String(Base64.encode("zhangyuanyuan_056@163.com".getBytes()))+enter);
        System.out.println(user);
        if(isStartWith(user,"334")){
        }else{
            System.out.println("filed to username");
            return false;
        }
        String pass = sendCommand(new String(Base64.encode("198706211202".getBytes()))+enter);
        if(isStartWith(pass,"235")){
        }else{
            System.out.println("filed to pass");
            return false;  }*/

        //send command
        String f=sendCommand("Mail From:<"+from+">"+enter);
        if(isStartWith(f,"250")){

        }else{
            System.out.println("filed to command");
            return false;
        }

        String toStr=sendCommand("RCPT TO:<"+to+">"+enter);
        if(isStartWith(toStr,"250")){

        }else{   return false;  }

        String data=sendCommand("DATA"+enter);
        if(isStartWith(data,"354")){

        }else {
            System.out.println("filed to data");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("From:<"+from+">"+enter);
        sb.append("To:<"+to+">"+enter);
        sb.append("Subject:test"+enter);
        sb.append("Content-Type:text/plain;charset=\"GB2312\""+enter);
        sb.append(enter);
        sb.append(content);
        sb.append(enter+"."+enter);

        String reply = sendCommand(sb.toString());
        if(isStartWith(reply,"250")){
        }else{   return false;  }
        //quit
        String quit = sendCommand("QUIT"+enter);
        if(isStartWith(quit,"221")){
        }else{   return false;  }
        close();
        return true;
    }

    private boolean isStartWith(String res,String with){
        return res.startsWith(with);  }

    public static void main(String[] args)throws IOException{
        smtpMail mail = new smtpMail();
        mail.setMailServer("smtp.usfca.edu");
        mail.setFrom("yzhang171@usfca.edu");
        mail.setTo("yuanyuantest2014@gmail.com");
        mail.setContent("hello,this mail for saving email to db");
        boolean boo = mail.sendMail();
        if(boo)
            System.out.println("send email successfully！");
        else{   System.out.println("send email failed");
        }

    }

}

