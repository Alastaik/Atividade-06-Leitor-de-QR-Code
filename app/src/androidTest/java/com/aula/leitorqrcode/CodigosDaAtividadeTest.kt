package com.aula.leitorqrcode

import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CodigosDaAtividadeTest {
    @Test fun interpretaOsSeteQrCodesEIgnoraEan13() {
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val scanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
        val tipos = listOf(TipoConteudo.LINK, TipoConteudo.TEXTO, TipoConteudo.WIFI,
            TipoConteudo.TELEFONE, TipoConteudo.EMAIL, TipoConteudo.LOCALIZACAO,
            TipoConteudo.TEXTO)
        val vm = LeitorViewModel()
        try {
            for (numero in 1..8) {
                val bitmap = assets.open("codigo-$numero.png").use { BitmapFactory.decodeStream(it) }
                val codigos = Tasks.await(scanner.process(InputImage.fromBitmap(bitmap, 0)),
                    15, TimeUnit.SECONDS)
                if (numero == 8) {
                    assertTrue("EAN-13 deve ser ignorado", codigos.isEmpty())
                    continue
                }
                assertEquals("Código $numero", 1, codigos.size)
                val leitura = requireNotNull(codigos.single().paraLeitura())
                assertEquals(tipos[numero - 1], leitura.tipo)
                assertEquals(numero in listOf(1, 4, 5, 6), leitura.uriAcao != null)
                if (numero == 3) assertEquals("Rede: Laboratorio-ADS", leitura.descricao)
                if (numero == 4) assertTrue(leitura.uriAcao!!.startsWith("tel:"))
                if (numero == 5) assertTrue(leitura.uriAcao!!.startsWith("mailto:"))
                if (numero == 6) assertTrue(leitura.uriAcao!!.startsWith("geo:"))
                repeat(5) { vm.registrar(leitura) }
            }
            assertEquals(7, vm.estado.value.historico.size)
        } finally {
            scanner.close()
        }
    }
}
