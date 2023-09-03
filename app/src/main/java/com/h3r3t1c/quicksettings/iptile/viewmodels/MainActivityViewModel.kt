package com.h3r3t1c.quicksettings.iptile.viewmodels

import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.h3r3t1c.quicksettings.iptile.util.AddressHelper
import com.h3r3t1c.quicksettings.iptile.util.Keys

class MainActivityViewModel : ViewModel(){

    private var selectedInterface = mutableStateOf(AddressHelper.defaultInterface)
    private var showAddTileButton = mutableStateOf(true)

    fun initVars(c: Context){
        val tmp = Keys.getSelectedInterface(c).toString()
        selectedInterface.value = AddressHelper.interfaceNameToReadableName(tmp)+" ($tmp)"
        showAddTileButton.value = !Keys.isTileAdded(c) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
    fun updateSelectedInterface(s:String){
        selectedInterface.value = s
    }
    fun showAddTileButton():Boolean{
        return showAddTileButton.value
    }
    fun getSelectedInterface():String{
        return selectedInterface.value
    }
    fun updateShowAddTileButton(b:Boolean){
        showAddTileButton.value = b
    }
}