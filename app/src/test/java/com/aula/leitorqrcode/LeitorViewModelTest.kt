package com.aula.leitorqrcode

import org.junit.Assert.*
import org.junit.Test

class LeitorViewModelTest {
    private fun leitura(valor: String, instante: Long = 1) =
        LeituraQr(valor, TipoConteudo.TEXTO, valor, null, instante)

    @Test fun quadrosRepetidosNaoAlteramEstado() {
        val vm = LeitorViewModel()
        vm.registrar(leitura("A"))
        val anterior = vm.estado.value
        repeat(20) { vm.registrar(leitura("A", 2)) }
        assertSame(anterior, vm.estado.value)
        assertEquals(1, vm.estado.value.historico.size)
    }

    @Test fun releituraVaiAoTopoSemDuplicar() {
        val vm = LeitorViewModel()
        vm.registrar(leitura("A"))
        vm.registrar(leitura("B"))
        vm.registrar(leitura("A", 3))
        assertEquals(listOf("A", "B"), vm.estado.value.historico.map { it.valor })
        assertEquals(3L, vm.estado.value.ultima?.instante)
    }

    @Test fun limparRemoveUltimaEHistorico() {
        val vm = LeitorViewModel()
        vm.registrar(leitura("A"))
        vm.limparHistorico()
        assertEquals(LeitorUiState(), vm.estado.value)
        vm.registrar(leitura("A"))
        assertEquals(1, vm.estado.value.historico.size)
    }
}
