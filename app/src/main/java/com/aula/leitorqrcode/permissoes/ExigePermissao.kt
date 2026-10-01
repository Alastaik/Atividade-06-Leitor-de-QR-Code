package com.aula.leitorqrcode.permissoes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun ExigePermissao(permissao: String, justificativa: String, conteudo: @Composable () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preferencias = remember { context.getSharedPreferences("permissoes", 0) }
    var concedida by remember { mutableStateOf(context.temPermissao(permissao)) }
    var bloqueada by remember { mutableStateOf(false) }

    fun atualizar() {
        concedida = context.temPermissao(permissao)
        val activity = context.activity()
        bloqueada = !concedida && preferencias.getBoolean(permissao, false) &&
            activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissao)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        preferencias.edit().putBoolean(permissao, true).apply()
        atualizar()
    }
    // Reconfere a permissão ao voltar das configurações.
    DisposableEffect(lifecycleOwner, permissao) {
        val observer = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) atualizar()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        atualizar()
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    if (concedida) conteudo() else Column {
        Text(justificativa)
        Button(onClick = {
            if (bloqueada) context.abrirConfiguracoes() else launcher.launch(permissao)
        }) {
            Text(if (bloqueada) "Abrir configurações" else "Conceder permissão")
        }
    }
}
