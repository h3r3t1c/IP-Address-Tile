package com.h3r3t1c.quicksettings.iptile.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Keys {

    const val PREF_HIDE_IP_ON_LOCK = "hide_ip_lock"
    const val PREF_SHOW_DIALOG_LOCKSCREEN = "show_dialog_lockscreen"
    const val PREF_INTERFACE = "interface"
    const val PREF_SHOW_INTERFACE_NAME = "show_iface_name"

    private fun getPrefs(c: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(c)
    }
    fun updateString(c:Context, key:String, s:String){
        getPrefs(c).edit().putString(key, s).commit()
    }
    fun updateBoolean(c:Context, key:String, b:Boolean){
        getPrefs(c).edit().putBoolean(key, b).commit()
    }
    fun showDialogOnLockscreen(c:Context):Boolean{
        return getPrefs(c).getBoolean(PREF_SHOW_DIALOG_LOCKSCREEN, true)
    }
    fun hideIPOnLockscreen(c:Context):Boolean{
        return getPrefs(c).getBoolean(PREF_HIDE_IP_ON_LOCK, false)
    }
    fun showInterfaceName(c:Context):Boolean{
        return getPrefs(c).getBoolean(PREF_SHOW_INTERFACE_NAME, false)
    }
    fun getSelectedInterface(c:Context):String?{
        return getPrefs(c).getString(PREF_INTERFACE, AddressHelper.defaultInterface)
    }
}