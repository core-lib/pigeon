package payne.framework.pigeon.core.protocol;

import payne.framework.pigeon.core.annotation.Accept;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by yangchangpei on 16/12/14.
 */
public class HTTPSChannel extends HTTPChannel implements HostnameVerifier, X509TrustManager {
    private HttpsURLConnection httpsURLConnection;

    public void initialize(String host, int port, Accept.Mode mode, String file, int timeout, String format) throws IOException {
        this.protocol = "https";
        this.host = host;
        this.port = port;
        this.mode = mode;
        this.file = file;
        this.parameter = "";
        this.address = new InetSocketAddress(host, port);

        this.httpsURLConnection = (HttpsURLConnection) new URL(protocol, host, port, file).openConnection();
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{this}, null);
            this.httpsURLConnection.setHostnameVerifier(this);
            this.httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            throw new IOException(e);
        }

        this.connection = this.httpsURLConnection;
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setUseCaches(false);
        this.connection.setRequestMethod(mode.name());
        this.connection.setConnectTimeout(timeout);
        this.connected = true;
        this.closed = false;
    }

    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
