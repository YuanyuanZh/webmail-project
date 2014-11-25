package cs601.webmail.frameworks.mail.pop3;

import cs601.webmail.frameworks.mail.Header;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

import cs601.webmail.frameworks.mail.SocketClient;
import cs601.webmail.util.Logger;

/**
 * ref: https://www.ietf.org/rfc/rfc1939.txt
 *
 * ref: http://mike-java.blogspot.com/2008/03/simple-pop3-client-in-java-tutorial.html
 *
 * Created by yuanyuan on 10/24/14.
 */
public class Pop3Client extends SocketClient{

    private final static Logger LOGGER = Logger.getLogger(Pop3Client.class);

    public Pop3Client(boolean sslEnabled) {
        super(sslEnabled);
    }

    public Pop3Client() {
        super(false);
    }

    private final static int DEFAULT_PORT = 110;

    private final static String OK = "+OK";
    private final static String ERR = "+ERR";

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
            LOGGER.debug("Connected to the host");

        readResponseLine();
    }
    @Override
    protected int getPort(){
        return DEFAULT_PORT;
    }
    @Override
    protected void doResponseCheck(String responseLine){
        if (responseLine != null && responseLine.startsWith("-ERR"))
        throw new RuntimeException("Server has returned an error: " + responseLine.replaceFirst("-ERR ", ""));
    }
@Override
    public boolean login(String username, String password) throws IOException {
        sendCommand("USER " + username);
        String answer=sendCommand("PASS " + password);
        if(answer.startsWith("+OK")){
        }else{
            return false;
        }
        if (debug) {
            LOGGER.debug("[DEBUG] login with " + username);
        }
        return true;
    }
@Override
    public void logout() throws IOException {
        sendCommand("QUIT");
    }


    //------------------------- POP3 details

    public int getNumberOfNewMessages() throws IOException {
        String response = sendCommand("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }

    public Pop3Message getMessageTop(int messageId, int topCount) throws IOException {

        sendCommand("TOP " + messageId + " " + topCount);

        String response;
        String headerName;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();

        Map<String, List<Header>> _headers = new HashMap<String, List<Header>>();
        Header lastHeader = null;

        // process headers
        while ((response = readResponseLine()).length() != 0) {

            if (response.startsWith(" ") || response.startsWith("\t")) {
                if (lastHeader != null) {
                    lastHeader.setValue(lastHeader.getValue() + response);
                }
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

            lastHeader = new Header(headerName, headerValue);
            List<Header> _headerValues = _headers.get(headerName);
            if (_headerValues == null) {
                _headerValues = new ArrayList<Header>();
                _headers.put(headerName, _headerValues);
            }
            _headerValues.add(lastHeader);
        }

        // process body
        StringBuilder bodyBuilder = new StringBuilder();
        while (!(response = readResponseLine()).equals(".")) {
            bodyBuilder.append(response + "\n");
        }
        return new Pop3Message(_headers);
    }

    private Pop3Message __parseTOP(String substring) {
        return null;
    }

    public Pop3MessageInfo listUniqueIdentifier(int messageId) throws IOException {
        String response = sendCommand("UIDL " + messageId);

        if (response != null && response.startsWith(OK)) {
            return __parseUID(response.substring(3));
        }

        return null;
    }

    public Pop3MessageInfo[] listUniqueIdentifiers() throws IOException {
        String response = sendCommand("UIDL");

        if (response != null && response.startsWith(OK)) {

            List<String> lines = new ArrayList<String>();

            while (!(response = readResponseLine()).equals(".")) {
                lines.add(response);
            }

            Pop3MessageInfo[] messageInfos = new Pop3MessageInfo[lines.size()];
            ListIterator<String> iterator = lines.listIterator();

            for (int i = 0, len = messageInfos.length; i < len; i++) {
                messageInfos[i] = __parseUID(iterator.next());
            }

            return messageInfos;
        }

        return null;
    }

    private static Pop3MessageInfo __parseUID(String line)
    {
        int num;
        StringTokenizer tokenizer;

        tokenizer = new StringTokenizer(line);

        if (!tokenizer.hasMoreElements()) {
            return null;
        }

        num = 0;

        try
        {
            num = Integer.parseInt(tokenizer.nextToken());

            if (!tokenizer.hasMoreElements()) {
                return null;
            }

            line = tokenizer.nextToken();
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        return new Pop3MessageInfo(num, line);
    }

    public Pop3MessageInfo listMessage(int messageId) throws IOException {
        String response = sendCommand("LIST " + messageId);

        if (response != null && response.startsWith(OK)) {
            return __parseStatus(response.substring(3));
        }

        return null;
    }

    public Pop3MessageInfo[] listMessages() throws IOException {
        String response = sendCommand("LIST");

        if (response != null && response.startsWith(OK)) {

            List<String> lines = new ArrayList<String>();

            while (!(response = readResponseLine()).equals(".")) {
                lines.add(response);
            }

            Pop3MessageInfo[] messageInfos = new Pop3MessageInfo[lines.size()];
            ListIterator<String> iterator = lines.listIterator();

            for (int i = 0, len = messageInfos.length; i < len; i++) {
                messageInfos[i] = __parseStatus(iterator.next());
            }

            return messageInfos;
        }

        return null;
    }

    private static Pop3MessageInfo __parseStatus(String line)
    {
        int num, size;
        StringTokenizer tokenizer;

        tokenizer = new StringTokenizer(line);

        if (!tokenizer.hasMoreElements()) {
            return null;
        }

        num = size = 0;

        try
        {
            num = Integer.parseInt(tokenizer.nextToken());

            if (!tokenizer.hasMoreElements()) {
                return null;
            }

            size = Integer.parseInt(tokenizer.nextToken());
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        return new Pop3MessageInfo(num, size);
    }

    public Pop3Message getMessage(int i) throws IOException {
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
        return new Pop3Message(headers, bodyBuilder.toString());
    }

    public List<Pop3Message> getMessages() throws IOException {
        int numOfMessages = getNumberOfNewMessages();
        List<Pop3Message> messageList = new ArrayList<Pop3Message>();
        for (int i = 1; i <= numOfMessages; i++) {
            messageList.add(getMessage(i));
        }
        return messageList;
    }

    public List<Pop3Message> getMessages(int retriveCount) throws IOException {
        int numOfMessages = getNumberOfNewMessages();

        if (retriveCount > numOfMessages) {
            throw new IllegalArgumentException("count overflow");
        }

        if (retriveCount < 0 && retriveCount * -1 > numOfMessages) {
            throw new IllegalArgumentException("negative count overflow");
        }

        List<Pop3Message> messageList = new ArrayList<Pop3Message>();

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

    public static Pop3Client createInstance(String host, int port, boolean sslEnabled) throws IOException {
        Pop3Client client = new Pop3Client(sslEnabled);
        client.setDebug(false);
        client.connect(host, port);
        return client;
    }
}
