package com.nick.rachao.ui.quadras

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.nick.rachao.R
import com.nick.rachao.databinding.ActivityQuadrasBinding
import com.nick.rachao.model.Jogador
import com.nick.rachao.util.Extras
import com.nick.rachao.util.configurarTelaCheia
import com.nick.rachao.util.parcelavel

/**
 * TELA 2: apenas hospeda o QuadrasFragment, repassando o jogador via arguments.
 */
class QuadrasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuadrasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuadrasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarTelaCheia(binding.root)

        // Toolbar com botão voltar
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Dado recebido da tela 1 por putExtra
        val jogador = intent.parcelavel<Jogador>(Extras.JOGADOR) ?: run {
            finish()
            return
        }

        // Só adiciona o fragment na primeira criação (na rotação o FragmentManager restaura)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.containerFragment, QuadrasFragment.novaInstancia(jogador))
            }
        }
    }
}
