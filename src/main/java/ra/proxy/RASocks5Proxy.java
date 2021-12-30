package ra.proxy;

import socks.CProxy;
import socks.Socks5Proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RASocks5Proxy extends Socks5Proxy {

    private String target;

    public RASocks5Proxy(CProxy p, String proxyHost, int proxyPort) throws UnknownHostException {
        super(p, proxyHost, proxyPort);
    }

    public RASocks5Proxy(String proxyHost, int proxyPort) throws UnknownHostException {
        super(proxyHost, proxyPort);
    }

    public RASocks5Proxy(CProxy p, InetAddress proxyIP, int proxyPort) {
        super(p, proxyIP, proxyPort);
    }

    public RASocks5Proxy(InetAddress proxyIP, int proxyPort) {
        super(proxyIP, proxyPort);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


}
