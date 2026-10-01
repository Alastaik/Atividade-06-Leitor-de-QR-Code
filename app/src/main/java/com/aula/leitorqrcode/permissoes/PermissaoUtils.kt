package com.aula.leitorqrcode.permissoes

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.temPermissao(permissao: String): Boolean =
    ContextCompat.checkSelfPermission(this, permissao) == PackageManager.PERMISSION_GRANTED

fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.activity()
    else -> null
}

fun Context.abrirConfiguracoes() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName")))
}
