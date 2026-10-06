package com.nick.rachao.ui.detalhe

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nick.rachao.R
import com.nick.rachao.adapter.JogadorAdapter
import com.nick.rachao.data.RachaoMock
import com.nick.rachao.databinding.ActivityQuadraDetalheBinding
import com.nick.rachao.model.Jogador
import com.nick.rachao.model.Quadra
import com.nick.rachao.ui.placar.PlacarActivity
import com.nick.rachao.util.Extras
import com.nick.rachao.util.Sorteio
import com.nick.rachao.util.configurarTelaCheia
import com.nick.rachao.util.corHex
import com.nick.rachao.util.parcelavel
import com.nick.rachao.util.rotulo

/**
 * TELA 3: detalhes da quadra, entrar/sair do racha e sortear os times.
 */
class QuadraDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuadraDetalheBinding
    private lateinit var jogador: Jogador

    // Estado da tela. A Quadra é imutável: cada mudança troca a referência por um copy().
    private lateinit var quadra: Quadra

    private val confirmadosAdapter = JogadorAdapter()

    private val usuarioNoRacha: Boolean
        get() = quadra.confirmados.any { it.id == Jogador.ID_USUARIO }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuadraDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarTelaCheia(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Dados recebidos por putExtra: id da quadra + jogador
        val quadraRecebida = RachaoMock.buscarQuadra(intent.getIntExtra(Extras.QUADRA_ID, -1))
        val jogadorRecebido = intent.parcelavel<Jogador>(Extras.JOGADOR)
        if (quadraRecebida == null || jogadorRecebido == null) {
            Toast.makeText(this, R.string.erro_quadra_nao_encontrada, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        jogador = jogadorRecebido
        quadra = quadraRecebida

        // Restaura (após rotação) se o usuário já tinha entrado no racha
        if (savedInstanceState?.getBoolean(ESTADO_NO_RACHA) == true) {
            quadra = quadra.copy(confirmados = quadra.confirmados + jogador)
        }

        binding.recyclerConfirmados.layoutManager = LinearLayoutManager(this)
        binding.recyclerConfirmados.adapter = confirmadosAdapter

        preencherInformacoes()
        atualizarConfirmados()

        binding.botaoEntrar.setOnClickListener { alternarParticipacao() }
        binding.botaoSortear.setOnClickListener { sortearTimes() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ESTADO_NO_RACHA, usuarioNoRacha)
    }

    private fun preencherInformacoes() {
        binding.imageFoto.setImageResource(quadra.fotoRes)
        binding.textNome.text = quadra.nome
        binding.textBairro.text = quadra.bairro
        binding.textHorario.text = quadra.horario
        binding.textTipo.setText(quadra.tipo.rotulo())
        binding.textRolando.isVisible = quadra.rolandoAgora

        // REQUISITO (opcionais): Boolean? com 3 estados -> Sim / Não / Não informado
        val iluminacao = when (quadra.temIluminacao) {
            true -> getString(R.string.iluminacao_sim)
            false -> getString(R.string.iluminacao_nao)
            null -> getString(R.string.iluminacao_nao_informado)
        }
        binding.textIluminacao.text = getString(R.string.iluminacao_formato, iluminacao)

        // REQUISITO (opcionais): bloco "Regras da casa" só se existir -> isVisible + ?.let
        binding.blocoRegras.isVisible = quadra.regrasDaCasa != null
        quadra.regrasDaCasa?.let { regras -> binding.textRegras.text = regras }
    }

    /** Entra ou sai do racha criando uma NOVA Quadra com copy() — o mock nunca é mutado. */
    private fun alternarParticipacao() {
        val mensagem: Int
        if (usuarioNoRacha) {
            quadra = quadra.copy(
                confirmados = quadra.confirmados.filterNot { it.id == Jogador.ID_USUARIO }
            )
            mensagem = R.string.msg_saiu
        } else {
            if (quadra.lotada) return
            quadra = quadra.copy(confirmados = quadra.confirmados + jogador)
            mensagem = R.string.msg_entrou
        }
        atualizarConfirmados()
        Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.botaoEntrar)
            .show()
    }

    /** REQUISITO: interação que atualiza a interface (lista, contador de vagas, botões). */
    private fun atualizarConfirmados() {
        val total = quadra.confirmados.size
        confirmadosAdapter.submitList(quadra.confirmados)

        binding.textContagem.text = getString(R.string.jogadores_contagem, total, quadra.vagas)
        binding.textVagas.text = if (quadra.lotada) {
            getString(R.string.vagas_lotado)
        } else {
            resources.getQuantityString(
                R.plurals.vagas_restantes, quadra.vagasRestantes, quadra.vagasRestantes
            )
        }
        binding.progressOcupacao.max = quadra.vagas
        binding.progressOcupacao.setProgressCompat(total, true)

        when {
            usuarioNoRacha -> estilizarBotaoEntrar(
                R.string.botao_sair, R.color.superficie_alta, R.color.texto_primario, true
            )
            quadra.lotada -> estilizarBotaoEntrar(
                R.string.botao_lotado, R.color.superficie_alta, R.color.texto_apagado, false
            )
            else -> estilizarBotaoEntrar(
                R.string.botao_entrar, R.color.laranja_bola, R.color.preto, true
            )
        }

        // "Sortear times" só aparece com pelo menos 6 confirmados
        val podeSortear = total >= MINIMO_PARA_SORTEAR
        binding.botaoSortear.isVisible = podeSortear
        binding.textDicaSorteio.isVisible = !podeSortear
        val faltam = MINIMO_PARA_SORTEAR - total
        if (faltam > 0) {
            binding.textDicaSorteio.text =
                resources.getQuantityString(R.plurals.faltam_para_sortear, faltam, faltam)
        }
    }

    private fun estilizarBotaoEntrar(
        texto: Int,
        @ColorRes fundo: Int,
        @ColorRes corTexto: Int,
        habilitado: Boolean
    ) {
        binding.botaoEntrar.setText(texto)
        binding.botaoEntrar.isEnabled = habilitado
        binding.botaoEntrar.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, fundo))
        binding.botaoEntrar.setTextColor(ContextCompat.getColor(this, corTexto))
    }

    private fun sortearTimes() {
        val (laranja, azul) = Sorteio.sortear(
            jogadores = quadra.confirmados,
            nomeTimeA = getString(R.string.time_laranja),
            corTimeA = corHex(R.color.laranja_bola),
            nomeTimeB = getString(R.string.time_azul),
            corTimeB = corHex(R.color.azul_quadra)
        )
        // REQUISITO: Intent explícita passando os times (Parcelable via @Parcelize)
        val intent = Intent(this, PlacarActivity::class.java)
            .putExtra(Extras.TIME_LARANJA, laranja)
            .putExtra(Extras.TIME_AZUL, azul)
            .putExtra(Extras.QUADRA_NOME, quadra.nome)
        startActivity(intent)
    }

    private companion object {
        const val MINIMO_PARA_SORTEAR = 6
        const val ESTADO_NO_RACHA = "estado_no_racha"
    }
}
