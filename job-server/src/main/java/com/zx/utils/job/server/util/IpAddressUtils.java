package com.zx.utils.job.server.util;


import javax.servlet.http.HttpServletRequest;

/***
 *Description:获取请求IP地址
 *@author: zhaoxu
 *Date:2020/10/24 10:18 
 */
public class IpAddressUtils {
    final static String UNKNOWN = "unknown";
    final static String IP_SPLIT = ",";
    final static String X_FORWARDED_FOR = "x-forwarded-for";
    final static String PROXY_CLIENT_IP = "Proxy-Client-IP";
    final static String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    final static String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    final static String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    final static String X_REAL_IP = "X-Real-IP";
    final static String LOCAL = "127.0.0.1";
    final static String LOCAL_IP = "0:0:0:0:0:0:0:1";

    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(IP_SPLIT)) {
            return ip.split(IP_SPLIT)[0];
        } else {
            return ip.trim();
        }
    }

}
