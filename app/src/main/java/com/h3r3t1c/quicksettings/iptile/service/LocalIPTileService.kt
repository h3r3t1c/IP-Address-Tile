package com.h3r3t1c.quicksettings.iptile.service


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.EventLogTags.Description
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.h3r3t1c.quicksettings.iptile.R
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale


class LocalIPTileService : TileService() {

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }
    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile

        tile.icon = Icon.createWithResource(this, R.drawable.ic_lan_network);

        if(isNetworkAvailable()){
            tile.state = Tile.STATE_ACTIVE
            val ip = getLocalIPAddress(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = ip
                tile.label = getString(R.string.local_ip_address)
            }
            else{
                tile.label = ip
            }
        }
        else{
            tile.state = Tile.STATE_UNAVAILABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = getString(R.string.not_connected)
                tile.label = getString(R.string.local_ip_address)
            }
            else{
                tile.label = getString(R.string.no_network)
            }
        }

        tile.updateTile()
    }

    /**
     * Will try to return the IP address of wlan0
     *
     */
    private fun getLocalIPAddress(useIPv4:Boolean = true): String? {
        var ip = "Not Connected"
        var isWifi = false;
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                isWifi = intf.name != null && intf.name.equals("wlan0",true)
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        //Log.d("zzz",sAddr+" - "+intf.name)
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4 ){
                                ip = sAddr
                                if(isWifi)
                                    return sAddr
                            }
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                ip = if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
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
        return ip
    }
    private fun isNetworkAvailable(): Boolean {
        val conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        conMgr.activeNetworkInfo ?: return false
        return true;
    }
    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    @SuppressLint("MissingInflatedId")
    override fun onClick() {
        super.onClick()
        if(!isLocked){
            val localIP = getLocalIPAddress()
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_ip_tile, null);
            val d = Dialog(this, android.R.style.Theme_DeviceDefault_Dialog);
            val networkIpView = view.findViewById<TextView>(R.id.textNetworkAddress)
            view.findViewById<Button>(R.id.button).setOnClickListener {
                d.dismiss()
            }
            view.findViewById<View>(R.id.op1).setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.local_ip_address),localIP))

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                    Toast.makeText(this, "IP Address Copied", Toast.LENGTH_LONG).show()
            }
            view.findViewById<TextView>(R.id.textLocalAddress).text = localIP

            view.findViewById<View>(R.id.op2).visibility = View.GONE

            d.setTitle("Network Address")
            d.setContentView(view)
            showDialog(d)
        }
    }
    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}