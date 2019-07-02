package com.lg.sixsenses.willi.util;

import android.util.Log;

import com.lg.sixsenses.willi.repository.DataManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Util {

    public static final String TAG = Util.class.getName().toString();

    public static boolean setResolution (DataManager.Resolution resolution) {
        DataManager.getInstance().setResolution(resolution);
        if(resolution == DataManager.Resolution.LOW)
        {
            DataManager.getInstance().setCamWidth(144);
            DataManager.getInstance().setCamHeight(176);
            DataManager.getInstance().setComRate(15);
            Log.d(TAG, "Resolution : LOW");
        }
        else if(resolution == DataManager.Resolution.MID)
        {
            DataManager.getInstance().setCamWidth(144);
            DataManager.getInstance().setCamHeight(176);
            DataManager.getInstance().setComRate(20);
            Log.d(TAG, "Resolution : MID");
        }
        else if(resolution == DataManager.Resolution.HIGH)
        {
            DataManager.getInstance().setCamWidth(240);
            DataManager.getInstance().setCamHeight(320);
            DataManager.getInstance().setComRate(25);
            Log.d(TAG, "Resolution : HIGH");
        }
        return true;
    }
    public static boolean setResolution(String resolution) {
        DataManager.Resolution selectedResolution = DataManager.Resolution.LOW;
        if(resolution.equals("LOW")) {
            selectedResolution = DataManager.Resolution.LOW;
        } else if(resolution.equals("MID")) {
            selectedResolution = DataManager.Resolution.MID;
        } else if(resolution.equals("HIGH")) {
            selectedResolution = DataManager.Resolution.HIGH;
        }

        return setResolution(selectedResolution);
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (true) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }




}
