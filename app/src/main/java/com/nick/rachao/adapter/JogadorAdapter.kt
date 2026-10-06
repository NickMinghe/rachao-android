package com.nick.rachao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nick.rachao.R
import com.nick.rachao.databinding.ItemJogadorBinding
import com.nick.rachao.model.Jogador
import com.nick.rachao.util.rotulo

/**
 * Adapter reutilizado na lista de confirmados (tela 3) e nos elencos do placar (tela 4).
 * REQUISITO: ViewBinding (ItemJogadorBinding) — nada de findViewById.
 * ListAdapter combina com dados imutáveis: a cada mudança enviamos uma NOVA lista (submitList).
 */
class JogadorAdapter : ListAdapter<Jogador, JogadorAdapter.JogadorViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogadorViewHolder {
        val binding = ItemJogadorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JogadorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JogadorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class JogadorViewHolder(private val binding: ItemJogadorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(jogador: Jogador) {
            val contexto = binding.root.context
            binding.textAvatar.text = jogador.nome.firstOrNull()?.uppercase() ?: "?"
            binding.textNome.text = jogador.nome

            // REQUISITO (opcionais): apelido só aparece se existir -> isVisible + ?.let
            binding.textApelido.isVisible = jogador.apelido != null
            jogador.apelido?.let { apelido ->
                binding.textApelido.text = contexto.getString(R.string.apelido_formato, apelido)
            }

            binding.textDetalhe.text = contexto.getString(
                R.string.jogador_detalhe,
                contexto.getString(jogador.posicao.rotulo()),
                jogador.nivel
            )
            binding.textVoce.isVisible = jogador.id == Jogador.ID_USUARIO
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Jogador>() {
            override fun areItemsTheSame(antigo: Jogador, novo: Jogador) = antigo.id == novo.id
            override fun areContentsTheSame(antigo: Jogador, novo: Jogador) = antigo == novo
        }
    }
}
