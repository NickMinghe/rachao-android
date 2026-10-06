package com.nick.rachao.ui.placar

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nick.rachao.R
import com.nick.rachao.adapter.JogadorAdapter
import com.nick.rachao.databinding.ActivityPlacarBinding
import com.nick.rachao.databinding.ViewBotoesPontosBinding
import com.nick.rachao.model.Cesta
import com.nick.rachao.model.Lado
import com.nick.rachao.model.Time
import com.nick.rachao.util.Extras
import com.nick.rachao.util.configurarTelaCheia
import com.nick.rachao.util.listaParcelavel
import com.nick.rachao.util.paraCor
import com.nick.rachao.util.parcelavel
import java.util.Locale

/**
 * TELA 4: placar ao vivo, jogo até 21.
 */
class PlacarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacarBinding

    // ITEM OPCIONAL: o MESMO layout view_botoes_pontos.xml inflado uma vez para cada time
    private lateinit var botoesLaranja: ViewBotoesPontosBinding
    private lateinit var botoesAzul: ViewBotoesPontosBinding

    // Estado imutável: cada cesta gera novos objetos via copy() / nova lista
    private lateinit var timeLaranja: Time
    private lateinit var timeAzul: Time
    private var historico: List<Cesta> = emptyList()

    private val vencedor: Time?
        get() = listOf(timeLaranja, timeAzul).firstOrNull { it.pontos >= PONTOS_PARA_VENCER }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarTelaCheia(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Times recebidos por putExtra (Parcelable). Após rotação, vêm do savedInstanceState.
        val laranja = savedInstanceState?.parcelavel<Time>(ESTADO_LARANJA)
            ?: intent.parcelavel<Time>(Extras.TIME_LARANJA)
        val azul = savedInstanceState?.parcelavel<Time>(ESTADO_AZUL)
            ?: intent.parcelavel<Time>(Extras.TIME_AZUL)
        if (laranja == null || azul == null) {
            finish()
            return
        }
        timeLaranja = laranja
        timeAzul = azul
        historico = savedInstanceState?.listaParcelavel<Cesta>(ESTADO_HISTORICO) ?: emptyList()

        // Opcional: nome da quadra no subtítulo, só se veio na Intent
        intent.getStringExtra(Extras.QUADRA_NOME)?.let { binding.toolbar.subtitle = it }

        binding.textMeta.text = getString(R.string.placar_meta, PONTOS_PARA_VENCER)
        binding.textNomeLaranja.text = timeLaranja.nome
        binding.textNomeAzul.text = timeAzul.nome
        binding.textRotuloBotoesLaranja.text = timeLaranja.nome
        binding.textRotuloBotoesAzul.text = timeAzul.nome

        // Infla o componente reutilizável dentro de cada container
        botoesLaranja = ViewBotoesPontosBinding.inflate(
            layoutInflater, binding.containerBotoesLaranja, true
        )
        botoesAzul = ViewBotoesPontosBinding.inflate(
            layoutInflater, binding.containerBotoesAzul, true
        )
        configurarBotoes(botoesLaranja, Lado.LARANJA, timeLaranja.cor, R.color.preto)
        configurarBotoes(botoesAzul, Lado.AZUL, timeAzul.cor, R.color.branco)

        configurarElencos()

        binding.botaoNovaPartida.setOnClickListener { novaPartida() }
        binding.botaoVoltarQuadra.setOnClickListener { finish() }

        atualizarPlacar(animar = false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ESTADO_LARANJA, timeLaranja)
        outState.putParcelable(ESTADO_AZUL, timeAzul)
        outState.putParcelableArrayList(ESTADO_HISTORICO, ArrayList(historico))
    }

    /** Eventos do componente reutilizável: cada cópia controla o seu time. */
    private fun configurarBotoes(
        botoes: ViewBotoesPontosBinding,
        lado: Lado,
        corTime: String,
        @ColorRes corTexto: Int
    ) {
        val fundo = ColorStateList.valueOf(corTime.paraCor())
        val texto = ContextCompat.getColor(this, corTexto)
        listOf(botoes.botaoMais1 to 1, botoes.botaoMais2 to 2, botoes.botaoMais3 to 3)
            .forEach { (botao, pontos) ->
                botao.backgroundTintList = fundo
                botao.setTextColor(texto)
                botao.setOnClickListener { marcarCesta(lado, pontos) }
            }
        botoes.botaoDesfazer.strokeColor = fundo
        botoes.botaoDesfazer.setOnClickListener { desfazer(lado) }
    }

    private fun configurarElencos() {
        val adapterLaranja = JogadorAdapter()
        val adapterAzul = JogadorAdapter()
        binding.recyclerLaranja.layoutManager = LinearLayoutManager(this)
        binding.recyclerLaranja.adapter = adapterLaranja
        binding.recyclerAzul.layoutManager = LinearLayoutManager(this)
        binding.recyclerAzul.adapter = adapterAzul
        adapterLaranja.submitList(timeLaranja.jogadores)
        adapterAzul.submitList(timeAzul.jogadores)
        binding.textNivelLaranja.text = getString(R.string.placar_nivel_medio, timeLaranja.nivelMedio)
        binding.textNivelAzul.text = getString(R.string.placar_nivel_medio, timeAzul.nivelMedio)
    }

    private fun marcarCesta(lado: Lado, pontos: Int) {
        if (vencedor != null) return
        // copy() em vez de alterar o objeto; histórico = nova lista
        when (lado) {
            Lado.LARANJA -> timeLaranja = timeLaranja.copy(pontos = timeLaranja.pontos + pontos)
            Lado.AZUL -> timeAzul = timeAzul.copy(pontos = timeAzul.pontos + pontos)
        }
        historico = historico + Cesta(lado, pontos)
        atualizarPlacar(animar = true, ladoAnimado = lado)
    }

    /** Desfaz a última cesta daquele time. */
    private fun desfazer(lado: Lado) {
        val indice = historico.indexOfLast { it.lado == lado }
        if (indice == -1) return
        val cesta = historico[indice]
        historico = historico.filterIndexed { i, _ -> i != indice }
        when (lado) {
            Lado.LARANJA -> timeLaranja = timeLaranja.copy(pontos = timeLaranja.pontos - cesta.pontos)
            Lado.AZUL -> timeAzul = timeAzul.copy(pontos = timeAzul.pontos - cesta.pontos)
        }
        atualizarPlacar(animar = true, ladoAnimado = lado)
    }

    private fun novaPartida() {
        timeLaranja = timeLaranja.copy(pontos = 0)
        timeAzul = timeAzul.copy(pontos = 0)
        historico = emptyList()
        atualizarPlacar(animar = false)
    }

    /** REQUISITO: interações que atualizam a interface (placar, banner, botões, histórico). */
    private fun atualizarPlacar(animar: Boolean, ladoAnimado: Lado? = null) {
        binding.textPontosLaranja.text = formatarPontos(timeLaranja.pontos)
        binding.textPontosAzul.text = formatarPontos(timeAzul.pontos)
        if (animar) {
            when (ladoAnimado) {
                Lado.LARANJA -> pulsar(binding.textPontosLaranja)
                Lado.AZUL -> pulsar(binding.textPontosAzul)
                null -> Unit
            }
        }

        // Banner de vitória: aparece só quando há vencedor (?.let + isVisible)
        val ganhador = vencedor
        binding.bannerVencedor.isVisible = ganhador != null
        ganhador?.let { binding.textVencedor.text = getString(R.string.vencedor, it.nome) }

        // Fim de jogo desabilita os botões de pontos
        val emJogo = ganhador == null
        habilitarBotoes(botoesLaranja, emJogo, historico.any { it.lado == Lado.LARANJA })
        habilitarBotoes(botoesAzul, emJogo, historico.any { it.lado == Lado.AZUL })

        atualizarHistorico()
    }

    private fun habilitarBotoes(botoes: ViewBotoesPontosBinding, emJogo: Boolean, temCesta: Boolean) {
        botoes.botaoMais1.isEnabled = emJogo
        botoes.botaoMais2.isEnabled = emJogo
        botoes.botaoMais3.isEnabled = emJogo
        botoes.botaoDesfazer.isEnabled = emJogo && temCesta
        listOf(botoes.botaoMais1, botoes.botaoMais2, botoes.botaoMais3)
            .forEach { it.alpha = if (emJogo) 1f else 0.35f }
    }

    /** Últimas cestas, mais recente primeiro, cada linha na cor do time. */
    private fun atualizarHistorico() {
        if (historico.isEmpty()) {
            binding.textHistorico.setText(R.string.historico_vazio)
            return
        }
        binding.textHistorico.text = buildSpannedString {
            historico.takeLast(MAX_HISTORICO).asReversed().forEachIndexed { i, cesta ->
                if (i > 0) append("\n")
                val time = if (cesta.lado == Lado.LARANJA) timeLaranja else timeAzul
                color(time.cor.paraCor()) {
                    append(getString(R.string.historico_item, cesta.pontos, time.nome))
                }
            }
        }
    }

    private fun formatarPontos(pontos: Int): String = String.format(Locale.ROOT, "%02d", pontos)

    private fun pulsar(view: TextView) {
        view.animate().cancel()
        view.scaleX = 1.18f
        view.scaleY = 1.18f
        view.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
    }

    private companion object {
        const val PONTOS_PARA_VENCER = 21
        const val MAX_HISTORICO = 8
        const val ESTADO_LARANJA = "estado_laranja"
        const val ESTADO_AZUL = "estado_azul"
        const val ESTADO_HISTORICO = "estado_historico"
    }
}
