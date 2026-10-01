package com.aula.leitorqrcode

import android.content.pm.PackageManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun CameraQr(aoLerCodigo: (Barcode) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
        Text("Este aparelho não possui câmera.")
        return
    }
    val aoLerAtual by rememberUpdatedState(aoLerCodigo)
    val leitor = remember {
        BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            val executor = ContextCompat.getMainExecutor(context)
            setImageAnalysisAnalyzer(executor, MlKitAnalyzer(
                listOf(leitor), ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED, executor
            ) { resultado ->
                resultado.getValue(leitor)?.firstOrNull()?.let { aoLerAtual(it) }
            })
        }
    }
    DisposableEffect(controller, lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()
            leitor.close()
        }
    }
    AndroidView(modifier = modifier, factory = { ctx ->
        PreviewView(ctx).apply {
            // Mantém a imagem dentro da área da câmera.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            this.controller = controller
        }
    })
}
