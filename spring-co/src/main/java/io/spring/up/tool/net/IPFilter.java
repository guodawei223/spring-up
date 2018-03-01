package io.spring.up.tool.net;

public interface IPFilter {
    String IPv6KeyWord = ":";

    boolean accept(String ipAddress);
}
