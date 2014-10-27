package cs601.webmail;

import cs601.webmail.util.MimeUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ref: https://www.ietf.org/rfc/rfc1939.txt
 *
 * ref: http://mike-java.blogspot.com/2008/03/simple-pop3-client-in-java-tutorial.html
 *
 */
public class Pop3Client {

    private final static Logger LOGGER = Logger.getLogger(Pop3Client.class);

    public Pop3Client(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public Pop3Client() {
    }

    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean debug = true;
    private boolean sslEnabled = false;

    private final static int DEFAULT_PORT = 110;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void connect(String host, int port) throws IOException {

        if (sslEnabled) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = sslsocketfactory.createSocket(host, port);
        } else {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
        }

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        if (debug)
            System.out.println("Connected to the host");

        readResponseLine();
    }

    public void connect(String host) throws IOException {
        connect(host, DEFAULT_PORT);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void disconnect() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("Not connected to a host");
        socket.close();
        reader = null;
        writer = null;
        if (debug)
            System.out.println("Disconnected from the host");
    }

    protected String readResponseLine() throws IOException{
        String response = reader.readLine();
        if (debug) {
            System.out.println("DEBUG [in] : " + response);
        }
        if (response != null && response.startsWith("-ERR"))
            throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        return response;
    }

    protected String sendCommand(String command) throws IOException {
        if (debug) {
            System.out.println("DEBUG [out]: " + command);
        }
        writer.write(command + "\n");
        writer.flush();
        return readResponseLine();
    }

    public void login(String username, String password) throws IOException {
        sendCommand("USER " + username);
        sendCommand("PASS " + password);
    }

    public void logout() throws IOException {
        sendCommand("QUIT");
    }

    public void close() throws IOException {
        logout();
        disconnect();
    }

    //------------------------- POP3 details

    public int getNumberOfNewMessages() throws IOException {
        String response = sendCommand("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }

    protected Message getMessage(int i) throws IOException {
        String response = sendCommand("RETR " + i);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        String headerName = null;
        // process headers
        while ((response = readResponseLine()).length() != 0) {
            if (response.startsWith("\t")) {
                continue; //no process of multiline headers
            }
            int colonPosition = response.indexOf(":");

            // no colon
            if (colonPosition == -1) {
                LOGGER.debug("ignore header line: " + response);
                continue;
            }

            headerName = response.substring(0, colonPosition);
            String headerValue;
            if (response.length() > colonPosition + 2) {
                headerValue = response.substring(colonPosition + 2);
            } else {
                headerValue = "";
            }
            List<String> headerValues = headers.get(headerName);
            if (headerValues == null) {
                headerValues = new ArrayList<String>();
                headers.put(headerName, headerValues);
            }
            headerValues.add(headerValue);
        }
        // process body
        StringBuilder bodyBuilder = new StringBuilder();
        while (!(response = readResponseLine()).equals(".")) {
            bodyBuilder.append(response + "\n");
        }
        return new Message(headers, bodyBuilder.toString());

    }

    public List<Message> getMessages() throws IOException {
        int numOfMessages = getNumberOfNewMessages();
        List<Message> messageList = new ArrayList<Message>();
        for (int i = 1; i <= numOfMessages; i++) {
            messageList.add(getMessage(i));
        }
        return messageList;
    }

    public List<Message> getMessages(int retriveCount) throws IOException {
        int numOfMessages = getNumberOfNewMessages();

        if (retriveCount > numOfMessages) {
            throw new IllegalArgumentException("count overflow");
        }

        if (retriveCount < 0 && retriveCount * -1 > numOfMessages) {
            throw new IllegalArgumentException("negative count overflow");
        }

        List<Message> messageList = new ArrayList<Message>();

        // get old first
        if (retriveCount > 0) {
            for (int i = 1; i <= retriveCount; i++) {
                messageList.add(getMessage(i));
            }
        }
        //get new first
        else if (retriveCount < 0) {
            for (int i = numOfMessages + retriveCount; i <= numOfMessages; i++) {
                messageList.add(getMessage(i));
            }
        }
        return messageList;
    }

    public static Pop3Client createInstance() throws IOException {
        MailServerCredential credential = MailServerCredential.getDefault();

        Pop3Client client = new Pop3Client(credential.isSslEnabled());

        client.setDebug(false);
        client.connect(credential.getPopServer(), Integer.parseInt(credential.getPopPort()));
        client.login(credential.getEmail(), credential.getPassword());

        return client;
    }

    public static void main(String[] args) throws IOException {

//        System.out.println(MimeUtils.decodeText("=?GB2312?B?tbG1sc34yKvBpsCp1cWw2bv1OsL6MTAw1rG89TIwIb32MTDM7A==?="));
//        System.out.println(MimeUtils.decodeText("=?gb18030?B?eGlvbmd6ZF8xMTE4QDE2My5jb20=?="));

        Pop3Client client = new Pop3Client(true);
        client.setDebug(true);
        client.connect("pop.gmail.com", 995);
        client.login("yolandazhang2010@gmail.com", "Zyy@638708");

        System.out.println("Number of new emails: " + client.getNumberOfNewMessages());

        List<Message> messages = client.getMessages(1);
        for (int index = 0; index < messages.size(); index++) {
            System.out.println("--- Message num. " + index + " ---");
            System.out.println(messages.get(index).getHeaders());
            System.out.println(messages.get(index).getBody());
        }

        client.logout();
        client.disconnect();
    }

}
