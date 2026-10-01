package com.aula.leitorqrcode

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aula.leitorqrcode.permissoes.ExigePermissao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelaLeitor(modifier: Modifier = Modifier, viewModel: LeitorViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LazyColumn(modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Leitor de QR Code", style = MaterialTheme.typography.headlineSmall) }
        item {
            ExigePermissao(Manifest.permission.CAMERA,
                "O leitor usa a câmera para ler QR codes. Nenhuma imagem é salva.") {
                CameraQr(aoLerCodigo = { codigo ->
                    codigo.paraLeitura()?.let { viewModel.registrar(it) }
                }, modifier = Modifier.fillMaxWidth().height(260.dp))
            }
        }
        item { UltimaLeitura(estado.ultima) { abrirLeitura(context, it) } }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Histórico (${estado.historico.size})", Modifier.weight(1f))
                TextButton(onClick = viewModel::limparHistorico) { Text("Limpar") }
            }
        }
        if (estado.historico.isEmpty()) item { Text("Nenhuma leitura no histórico.") }
        items(estado.historico, key = { it.valor }) {
            ItemHistorico(it)
            HorizontalDivider()
        }
    }
}

@Composable
private fun UltimaLeitura(leitura: LeituraQr?, aoAbrir: (LeituraQr) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (leitura == null) Text("Aponte a câmera para um QR code.") else {
                Text(leitura.tipo.rotulo, style = MaterialTheme.typography.labelLarge)
                Text(leitura.descricao)
                if (leitura.uriAcao != null) {
                    Button(onClick = { aoAbrir(leitura) }) { Text("Abrir") }
                }
            }
        }
    }
}

@Composable
private fun ItemHistorico(leitura: LeituraQr) {
    val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(leitura.instante))
    Column(Modifier.padding(vertical = 8.dp)) {
        Text("${leitura.tipo.rotulo} • $hora", style = MaterialTheme.typography.labelLarge)
        Text(leitura.descricao)
    }
}

private fun abrirLeitura(context: Context, leitura: LeituraQr) {
    val uri = leitura.uriAcao ?: return
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "Nenhum aplicativo pode abrir este conteúdo.", Toast.LENGTH_SHORT).show()
    }
}
