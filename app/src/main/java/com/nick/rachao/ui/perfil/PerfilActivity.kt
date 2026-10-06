package com.nick.rachao.ui.perfil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.nick.rachao.R
import com.nick.rachao.databinding.ActivityPerfilBinding
import com.nick.rachao.model.Jogador
import com.nick.rachao.model.Posicao
import com.nick.rachao.ui.quadras.QuadrasActivity
import com.nick.rachao.util.Extras
import com.nick.rachao.util.configurarTelaCheia

/**
 * TELA 1 (launcher): o jogador monta o perfil e vai para a lista de quadras.
 */
class PerfilActivity : AppCompatActivity() {

    // REQUISITO: SOMENTE ViewBinding (nenhum findViewById no projeto)
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarTelaCheia(binding.root)

        // REQUISITO: interações que atualizam a interface
        binding.ratingNivel.setOnRatingBarChangeListener { barra, nota, _ ->
            if (nota < 1f) barra.rating = 1f // nível mínimo é 1
            atualizarDescricaoNivel()
        }
        binding.editNome.doAfterTextChanged { binding.layoutNome.error = null }
        binding.botaoBora.setOnClickListener { irPraQuadra() }

        atualizarDescricaoNivel()
    }

    private fun nivelSelecionado(): Int = binding.ratingNivel.rating.toInt().coerceIn(1, 5)

    private fun atualizarDescricaoNivel() {
        val nivel = nivelSelecionado()
        val descricoes = resources.getStringArray(R.array.niveis_descricao)
        binding.textNivelDescricao.text =
            getString(R.string.perfil_nivel_descricao, nivel, descricoes[nivel - 1])
    }

    private fun irPraQuadra() {
        val nome = binding.editNome.text?.toString()?.trim().orEmpty()

        // Validação: nome vazio mostra erro no próprio campo
        if (nome.isEmpty()) {
            binding.layoutNome.error = getString(R.string.erro_nome_vazio)
            binding.editNome.requestFocus()
            return
        }

        // REQUISITO (opcional): apelido em branco vira null
        val apelido = binding.editApelido.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }

        val posicao = when (binding.radioPosicao.checkedRadioButtonId) {
            R.id.radioAla -> Posicao.ALA
            R.id.radioPivo -> Posicao.PIVO
            else -> Posicao.ARMADOR
        }

        val jogador = Jogador(
            id = Jogador.ID_USUARIO,
            nome = nome,
            apelido = apelido,
            posicao = posicao,
            nivel = nivelSelecionado()
        )

        binding.imageLogo.animate().rotationBy(360f).setDuration(500).start()

        // REQUISITO: navegação por Intent EXPLÍCITA, enviando dados com putExtra
        val intent = Intent(this, QuadrasActivity::class.java)
            .putExtra(Extras.JOGADOR, jogador)
        startActivity(intent)
    }
}
