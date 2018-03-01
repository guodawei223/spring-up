package io.spring.up.tool;

import io.spring.up.tool.fn.Fn;
import io.spring.up.tool.net.IPHost;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

class Net {

    static boolean isReach(final String host, final int port) {
        final Boolean reach = Fn.getJvm(() -> {
            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            return Boolean.TRUE;
        }, host, port);
        return null == reach ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * @return
     */
    static String getIPv4() {
        return IPHost.getInstance().getExtranetIPv4Address();
    }

    static String netHostname() {
        return Fn.getJvm(() -> (InetAddress.getLocalHost()).getHostName(), true);
    }

    /**
     * @return
     */
    static String getIPv6() {
        return IPHost.getInstance().getExtranetIPv6Address();
    }

    /**
     * @return
     */
    static String getIP() {
        return IPHost.getInstance().getExtranetIPAddress();
    }
}
