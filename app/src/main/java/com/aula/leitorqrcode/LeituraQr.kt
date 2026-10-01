package com.aula.leitorqrcode

import com.google.mlkit.vision.barcode.common.Barcode

enum class TipoConteudo(val rotulo: String) {
    LINK("Link"), WIFI("Rede Wi-Fi"), TELEFONE("Telefone"),
    EMAIL("E-mail"), LOCALIZACAO("Localização"), TEXTO("Texto")
}

data class LeituraQr(
    val valor: String,
    val tipo: TipoConteudo,
    val descricao: String,
    val uriAcao: String?,
    val instante: Long = System.currentTimeMillis()
)

fun Barcode.paraLeitura(): LeituraQr? {
    val bruto = rawValue ?: return null
    return when (valueType) {
        Barcode.TYPE_URL -> {
            val link = url?.url ?: bruto
            LeituraQr(bruto, TipoConteudo.LINK, link, link)
        }
        Barcode.TYPE_WIFI -> {
            // A senha não aparece na tela.
            LeituraQr(bruto, TipoConteudo.WIFI, "Rede: ${wifi?.ssid.orEmpty()}", null)
        }
        Barcode.TYPE_PHONE -> {
            val numero = phone?.number ?: return null
            LeituraQr(bruto, TipoConteudo.TELEFONE, numero, "tel:$numero")
        }
        Barcode.TYPE_EMAIL -> {
            val endereco = email?.address ?: return null
            LeituraQr(bruto, TipoConteudo.EMAIL, endereco, "mailto:$endereco")
        }
        Barcode.TYPE_GEO -> {
            val ponto = geoPoint ?: return null
            val coordenadas = "${ponto.lat},${ponto.lng}"
            LeituraQr(bruto, TipoConteudo.LOCALIZACAO,
                "Latitude ${ponto.lat}, longitude ${ponto.lng}",
                "geo:$coordenadas?q=$coordenadas")
        }
        else -> LeituraQr(bruto, TipoConteudo.TEXTO, bruto, null)
    }
}
