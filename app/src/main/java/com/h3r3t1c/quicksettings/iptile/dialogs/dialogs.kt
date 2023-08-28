package com.h3r3t1c.quicksettings.iptile.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.h3r3t1c.quicksettings.iptile.MainActivityUI

import com.h3r3t1c.quicksettings.iptile.R
import com.h3r3t1c.quicksettings.iptile.ui.theme.IPTileTheme
import com.h3r3t1c.quicksettings.iptile.util.AddressHelper
import java.net.NetworkInterface
import java.util.Collections

@Composable
fun InterfacePickerDialog(onSelect:(s:String)->Unit){
    AlertDialog(
        onDismissRequest = { onSelect("") },
        confirmButton = {
            Text(text = LocalContext.current.getString(R.string.close),
                    modifier = Modifier.clickable { onSelect("") }
                )
        },
        title = {
            Text(text = stringResource(R.string.select_interface))
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ){
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    //if(intf.name.)
                    item{
                        InterfacePickerOption(name = intf.name, onSelect = onSelect)
                    }
                }
            }
        }
    )
}

@Composable
fun InterfacePickerOption(name:String, onSelect:(s:String)->Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onSelect(name)
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = getIconForType(name),
            contentDescription = null,
            tint = Color(LocalContext.current.getColor(R.color.text_color)),
            //modifier = Modifier.size(32.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(text = AddressHelper.interfaceNameToReadableName(name), fontSize = 18.sp)
            Text(text = name, fontSize = 14.sp)
        }
    }
}
fun getIconForType(s:String):ImageVector{
    return if(s.startsWith("wlan")) Icons.Default.Wifi
        else if(s.startsWith("eth")) Icons.Default.SettingsEthernet
        else if(s == "lo") Icons.Default.Smartphone
        else if(s.contains("rmnet", false)) Icons.Default.SignalCellular4Bar
        else Icons.Default.Lan
}
@Preview(showBackground = true)
@Composable
fun DialogTester() {
    IPTileTheme {
        //InterfacePickerOption("wlan0")
        InterfacePickerDialog(){}
    }
}