package com.aula.leitorqrcode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LeitorUiState(
    val ultima: LeituraQr? = null,
    val historico: List<LeituraQr> = emptyList()
)

class LeitorViewModel : ViewModel() {
    private val _estado = MutableStateFlow(LeitorUiState())
    val estado: StateFlow<LeitorUiState> = _estado.asStateFlow()

    fun registrar(leitura: LeituraQr) {
        _estado.update { atual ->
            // Ignora quadros seguidos com o mesmo código.
            if (leitura.valor == atual.ultima?.valor) return@update atual
            val restantes = atual.historico.filterNot { it.valor == leitura.valor }
            atual.copy(ultima = leitura, historico = listOf(leitura) + restantes)
        }
    }

    fun limparHistorico() {
        _estado.value = LeitorUiState()
    }
}
