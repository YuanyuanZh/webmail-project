package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.pop3.ClientListener;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import cs601.webmail.util.Logger;

/**
 * Created by yuanyuan on 11/16/14.
 */
public abstract class SocketClient {

    private static final Logger LOGGER = Logger.getLogger(SocketClient.class);
    public static final String CLRF="\r\n";

    protected Socket socket;

    protected BufferedReader reader;
    protected BufferedWriter writer;

    protected InputStream inputStream;
    protected OutputStream outputStream;

    protected boolean debug = false;
    protected boolean sslEnabled = false;

    protected SocketClient(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public boolean isDebug() {
        return debug;
    }

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

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        reader = new BufferedReader(new InputStreamReader(inputStream));
        writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        if (debug)
            LOGGER.debug("Connected to the host");

        readResponseLine();
    }

    public void connect(String host) throws IOException {
        connect(host,getPort() );
    }
    protected abstract int getPort();

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
            LOGGER.debug("Disconnected from the host");
    }

    protected String sendCommand(String command) throws IOException {
        if (debug) {
            if (command.startsWith("PASS ")) {
                LOGGER.debug("[out]: PASS ******");
            }else {
                LOGGER.debug("DEBUG [out]: " + command);
            }
        }
        writerLine(command,true);
        return readResponseLine();
    }

    protected void writeLine(String line) throws IOException{
        writerLine(line,false);
    }
    protected void writerLine(String line, boolean flush)throws IOException{
        writer.write(line);
        writer.write(CLRF);
        if(flush)
            writer.flush();
        fireEvent(ClientListener.EventType.LineWrite, line);
    }

    protected String readResponseLine() throws IOException{
        String response = reader.readLine();
        if (debug) {
            LOGGER.debug("DEBUG [in] : " + response);
        }

        fireEvent(ClientListener.EventType.LineRead, response);

        doResponseCheck(response);

        return response;
    }

    protected void doResponseCheck(String responseLine){
        //sub-class maybe need to impl this
    }

    public abstract boolean login(String username, String password) throws IOException ;

    public abstract void logout() throws IOException;

    public void close() throws IOException {
        logout();
        disconnect();
    }


    // ----------------------------- Listeners

    private List<ClientListener> listeners = new ArrayList<ClientListener>();

    public synchronized void addListener(ClientListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ClientListener listener) {
        listeners.remove(listener);
    }

    protected synchronized void fireEvent(ClientListener.EventType eventType, Object eventData) {
        if (listeners != null && listeners.size() > 0) {
            for (int i = 0, l = listeners.size(); i < l; i++) {
                ClientListener listener = listeners.get(i);
                if(listener.isAccepted(eventType)){
                    listener.onEvent(eventType,eventData);
                }
            }
        }
    }

}
