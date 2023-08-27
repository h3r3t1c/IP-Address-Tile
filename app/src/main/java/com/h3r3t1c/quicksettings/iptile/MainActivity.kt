package com.h3r3t1c.quicksettings.iptile

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.h3r3t1c.quicksettings.iptile.service.LocalIPTileService
import com.h3r3t1c.quicksettings.iptile.ui.theme.Blue500
import com.h3r3t1c.quicksettings.iptile.ui.theme.IPTileTheme
import com.h3r3t1c.quicksettings.iptile.util.Keys
import java.util.concurrent.Executor


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val statusBarManager: StatusBarManager = getSystemService(StatusBarManager::class.java)
            val resultSuccessExecutor = Executor {
                runOnUiThread {
                    Log.d("zzz", "Added?")
                }
            }
            statusBarManager.requestAddTileService(
                ComponentName(this, LocalIPTileService::class.java),
                getString(R.string.app_name),
                Icon.createWithResource(this, R.drawable.ic_lan_network),
                resultSuccessExecutor
            ){ resultCodeFailure ->
                Log.d("zzz", resultCodeFailure.toString())
            }
        }

        setContent {
            IPTileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityUI()
                }
            }
        }
    }
}
@Composable
fun MainActivityUI(){
    val context = LocalContext.current;
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        item{
            Text(
                text = stringResource(R.string.settings),
                fontWeight = FontWeight.Bold,
                color = Blue500,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
        item{SwitchOption(Keys.showDialogOnLockscreen(context), R.string.show_dialog_on_lockscreen, Keys.PREF_SHOW_DIALOG_LOCKSCREEN, true)}
        item{SwitchOption(Keys.hideIPOnLockscreen(context), R.string.hide_ip_when_locked, Keys.PREF_HIDE_IP_ON_LOCK)}
        item{Text(
            text = stringResource(R.string.about),
            fontWeight = FontWeight.Bold,
            color = Blue500,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )}
        item{Text(text = "Created by Thomas Otero")}
        item{Text(text = "th3h3r3t1c@gmail.com",
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp))}
        item{LinkOption(R.string.linkedin_profile, icon = R.drawable.ic_linkedin,"https://www.linkedin.com/in/thomas-otero-5b8aa429/")}
        item{LinkOption(R.string.play_store_apps, icon = R.drawable.ic_google_play,"https://play.google.com/store/apps/developer?id=Thomas+Otero")}
        item{LinkOption(R.string.github, icon = R.drawable.ic_github,"https://github.com/h3r3t1c")}
        item{LinkOption("Version "+BuildConfig.VERSION_NAME, icon = R.drawable.ic_about,null)}
    }
}
@Composable
fun LinkOption(title:Int, icon:Int, url:String?,hideSpace:Boolean = false ){
    LinkOption(title = stringResource(id = title), icon, url)
}
@Composable
fun LinkOption(title:String, icon:Int, url:String?, hideSpace:Boolean = false){
    val context = LocalContext.current
    if(!hideSpace) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .height(1.dp)
        )
    }
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                if (url != null) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    context.startActivity(i)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = if(isSystemInDarkTheme()) Color.White else Color.Black
        )
        Text(text = title,Modifier.padding(start = 8.dp))
    }
}
@Composable
fun SwitchOption(b:Boolean, title:Int, pref:String, hideSpace:Boolean = false){
    val context = LocalContext.current;
    var boxChecked by remember {
        mutableStateOf(b)
    }
    Box (
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .toggleable(
                boxChecked,
                true,
                Role.Switch
            ) { b ->
                boxChecked = b
                Keys.updateBoolean(context, pref, b)
            },

        ) {
        Text(
            text = stringResource(title),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 70.dp)
        )
        Switch(
            checked = boxChecked,
            onCheckedChange = null,
            enabled = true,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Blue500,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
                checkedBorderColor = Color.LightGray,
                uncheckedBorderColor = Color.LightGray
            ),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        if(!hideSpace) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .height(1.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IPTileTheme {
        MainActivityUI()
    }
}