package com.h3r3t1c.quicksettings.iptile.util

import android.content.Context
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

object AddressHelper {

    const val defaultInterface = "wlan0"

    fun getSelectedInterfaceIP(c: Context, useIPv4:Boolean = true):String?{

        val selectedInterface = Keys.getSelectedInterface(c);
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                var isFound = intf.name != null && intf.name.equals(selectedInterface,true)
                if(!isFound) continue
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress

                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if(isIPv4)
                             return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                    0,
                                    delim
                                ).uppercase(
                                    Locale.getDefault()
                                )
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Not Connected"
    }
    fun interfaceNameToReadableName(s:String):String{
        return if(s.startsWith("wlan", true)) "Wi-Fi"
            else if(s.startsWith("eth", true)) "Ethernet"
            else if(s.startsWith("lo", true)) "localhost"
            else if(s.contains("rmnet", true)) "Mobile Network"
            else s
    }
}