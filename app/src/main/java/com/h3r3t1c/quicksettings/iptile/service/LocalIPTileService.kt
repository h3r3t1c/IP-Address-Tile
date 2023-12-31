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
import com.h3r3t1c.quicksettings.iptile.util.AddressHelper
import com.h3r3t1c.quicksettings.iptile.util.Keys
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale


class LocalIPTileService : TileService() {

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
        Keys.updateBoolean(this, Keys.KEY_TILE_ADDED, true)
    }
    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        val interfaceName = Keys.getSelectedInterface(this);

        tile.icon = Icon.createWithResource(this, R.drawable.ic_lan_network);

        if(isNetworkAvailable()){
            tile.state = Tile.STATE_ACTIVE
            val ip = if(isLocked && Keys.hideIPOnLockscreen(this)) "" else AddressHelper.getSelectedInterfaceIP(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = ip
                tile.label = if(Keys.showInterfaceName(this)) interfaceName.toString() else getString(R.string.local_ip_address)
            }
            else{
                tile.label = ip
            }
        }
        else{
            tile.state = Tile.STATE_UNAVAILABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = getString(R.string.not_connected)
                tile.label = if(Keys.showInterfaceName(this)) interfaceName.toString() else getString(R.string.local_ip_address)
            }
            else{
                tile.label = getString(R.string.no_network)
            }
        }

        tile.updateTile()
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

    override fun onClick() {
        super.onClick()

        if(!isNetworkAvailable()){
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG).show()
        }
        else if(isLocked){
            if(Keys.hideIPOnLockscreen(this)) Toast.makeText(this.applicationContext, "Unlock to see IP address!", Toast.LENGTH_LONG).show()
        }
        else if(!isLocked){
            val localIP = AddressHelper.getSelectedInterfaceIP(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_ip_tile, null);
            val d = Dialog(this, android.R.style.Theme_DeviceDefault_Dialog);

            view.findViewById<Button>(R.id.button).setOnClickListener {
                d.dismiss()
            }
            view.findViewById<View>(R.id.op1).setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.local_ip_address),localIP))

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                    Toast.makeText(this, getString(R.string.ip_address_copied), Toast.LENGTH_LONG).show()
            }
            view.findViewById<TextView>(R.id.textLocalAddress).text = localIP

            view.findViewById<View>(R.id.op2).visibility = View.GONE

            d.setTitle(R.string.network_address)
            d.setContentView(view)
            showDialog(d)
        }
    }
    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
        Keys.updateBoolean(this, Keys.KEY_TILE_ADDED, false)
    }
}