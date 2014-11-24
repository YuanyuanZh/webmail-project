package cs601.webmail.frameworks.mail;

import cs601.webmail.frameworks.mail.pop3.ClientListener;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 11/16/14.
 */
public abstract class SocketClient {

    private Socket socket;

    protected BufferedReader reader;
    protected BufferedWriter writer;

    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean debug = false;
    private boolean sslEnabled = false;

    private final static int DEFAULT_PORT = 110;

    private final static String OK = "+OK";
    private final static String ERR = "+ERR";

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

    protected String sendCommand(String command) throws IOException {
        if (debug) {
            System.out.println("DEBUG [out]: " + command);
        }
        writer.write(command + "\r\n");
        writer.flush();
        return readResponseLine();
    }

    protected String readResponseLine() throws IOException{
        String response = reader.readLine();
        if (debug) {
            System.out.println("DEBUG [in] : " + response);
        }

        fireEvent(ClientListener.Event.LineReceived, response);

        if (response != null && response.startsWith("-ERR"))
            throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        return response;
    }

    public abstract void login(String username, String password) throws IOException ;

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

    protected synchronized void fireEvent(ClientListener.Event event, Object eventData) {
        if (listeners != null && listeners.size() > 0) {
            for (int i = 0, l = listeners.size(); i < l; i++) {
                ClientListener listener = listeners.get(i);
                if (event == ClientListener.Event.LineReceived) {
                    listener.onLineReceived(eventData.toString());
                }
            }
        }
    }

}
