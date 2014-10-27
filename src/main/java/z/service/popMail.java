package z.service;

/**
 * Created by yuanyuan on 10/22/14.
 */

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;


public class popMail {
    private String popServer;
    private int port = 995;
    private Socket client;
    private BufferedReader in;
    private DataOutputStream os;
    private String enter = "\r\n";
    private String From;
    private String To;
    private String Subject;
    private String MsgID;
    private String EDate;
    private StringBuilder content=new StringBuilder();


    public void setMailServer(String mailServer) {  this.popServer = mailServer;  }
    public String getMsgID(){return MsgID;}
    public String getFrom(){return From;}
    public String getTo(){return To;}
    public String getSubject(){return Subject;}
    public String getEDate(){return EDate;}
    //public String getBody(){return body;}
    public String getContent(){return content.toString();}


    private void connect(){

        if(popServer==null ||"".equals(popServer)){
            return;
        }

        try{
            //client =new Socket(popServer,port);

            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            client = (SSLSocket) sslsocketfactory.createSocket(popServer,port);

            in=new BufferedReader(new InputStreamReader(client.getInputStream()));
            os=new DataOutputStream(client.getOutputStream());
            String isConnect=response();
            System.out.println(isConnect);
            if(isConnect.startsWith("+OK")){
                System.out.println("establish connection successfully");

            }else {

                System.out.println("failed to establish connection"+isConnect);
            }
        }catch (UnknownHostException e){
            System.out.println("failed to establish connection");
            e.printStackTrace();
        }catch (IOException e){
            System.out.println("failed to read stream");
            e.printStackTrace();
        }

    }

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

    private void close()throws IOException{
        os.close();
        in.close();
        client.close();

    }

    private void login(String username, String password)throws IOException{

        String user=sendCommand("USER "+username+enter);
        System.out.println("USER "+username+enter);
        System.out.println(user);

        String pass=sendCommand("PASS "+password+enter);
        System.out.println("PASS "+(Base64.encode(password.getBytes()))+enter);
        System.out.println(pass);

    }

    private int getMailNumber()throws IOException{
        String auth=sendCommand("STAT "+enter);
        System.out.println("STAT"+enter);
        System.out.println(auth);
        String[] values = auth.split(" ");
        int count =Integer.parseInt(values[1]);

        return count;
    }


    private void getMailContent(int i)throws IOException {
        String reply =sendCommand("RETR " + i+enter);
        System.out.println("RETR" + i);
        System.out.println(reply);
        while (true) {
            String content = in.readLine();
            System.out.println(content);
            if (content.startsWith("From")) {
                String[] temp = content.trim().split(":");
                From = temp[1];
                //System.out.println(from);
            }
            if (content.startsWith("To")){
                String[] temp=content.trim().split(":");
                To=temp[1];
                //System.out.println(to);
            }
            if(content.startsWith("Subject")) {
                String[] temp = content.trim().split(":");
                Subject=temp[1];
                //System.out.println(subject);
            }
            if(content.startsWith("Date")) {
                String[] temp = content.trim().split(":");
                EDate=temp[1];
                //System.out.println(subject);
            }
            if(content.startsWith("Message-Id")) {
                String[] temp = content.trim().split(":");
                MsgID=temp[1];
                //System.out.println(msgID);
            }
            if (content.equals("")){
                break;
            }
        }
        while(true){
            String body=in.readLine();
            if (body.toLowerCase().equals(".")) {
                break;
            }
            content.append(body);
        }
        System.out.println("From: "+From);
        System.out.println("To: "+To);
        System.out.println("Subject: "+Subject);
        System.out.println("MessageID: "+MsgID);
        System.out.println("Date: "+EDate);
        System.out.println("Body:"+content.toString());
    }

    private boolean deleteMail(int i)throws IOException{
        String reply =sendCommand("DELE "+i+enter);
        System.out.println("DELE "+i+enter);
        System.out.println(reply);
        if(reply.startsWith("+OK")){

        }else{
            return false;
        }
        return true;
    }
    private void cancelDelete()throws IOException{
        String reply=sendCommand("RSET"+enter);
        System.out.println("RSET"+enter);
        System.out.println(reply);
    }

    private void logout()throws IOException{
        sendCommand("QUIT"+enter);
        System.out.println("QUIT"+enter);
        close();
    }

    public static void main(String args[])throws IOException,SQLException{
        popMail mailReceive=new popMail();
//        mailReceive.setMailServer("pop.gmail.com");
        mailReceive.setMailServer("pop.163.com");
        mailReceive.connect();
        mailReceive.login("yuanyuantest2014@gmail.com","zyy638708");
        System.out.println("login successfully");
        int mailNumbers = mailReceive.getMailNumber();
        System.out.println("received:"+ mailNumbers+"emails");
        for(int i=1;i<=mailNumbers;i++)
        {
            mailReceive.getMailContent(i);

            System.out.println(String.format("%s , Subject: %s", mailReceive.getMsgID() , mailReceive.getSubject()));

//        sqliteJDBC q=new sqliteJDBC();
//        q.connectToDB();
//        q.mailInsertStatement(mailReceive.getMsgID(),mailReceive.getSubject(),mailReceive.getFrom(),mailReceive.getTo(),
//                    mailReceive.getContent(),mailReceive.getEDate(),1,1);

        }
//        boolean flag=mailReceive.deleteMail(1);
//        if(flag){
//            System.out.println("message deleted");
//        }else System.out.println("message delete failed");
//        mailReceive.cancelDelete();
        mailReceive.logout();



    }

}


