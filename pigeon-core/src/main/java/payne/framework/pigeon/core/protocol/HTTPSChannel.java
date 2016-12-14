package payne.framework.pigeon.core.protocol;

import payne.framework.pigeon.core.annotation.Accept;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

/**
 * Created by yangchangpei on 16/12/14.
 */
public class HTTPSChannel extends HTTPChannel {

    public void initialize(String host, int port, Accept.Mode mode, String file, int timeout, String format) throws IOException {
        this.protocol = "HTTPS";
        this.host = host;
        this.port = port;
        this.mode = mode;
        this.file = file;
        this.parameter = "";
        this.address = new InetSocketAddress(host, port);
        this.connection = (HttpURLConnection) new URL(protocol, host, port, file).openConnection();
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setUseCaches(false);
        this.connection.setRequestMethod(mode.name());
        this.connection.setConnectTimeout(timeout);
        this.connected = true;
        this.closed = false;
    }

}
