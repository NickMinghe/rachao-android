package com.nick.rachao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nick.rachao.R
import com.nick.rachao.databinding.ItemQuadraBinding
import com.nick.rachao.model.Quadra
import com.nick.rachao.util.rotulo

/**
 * Adapter dos cards de quadra (item_quadra.xml) da tela 2.
 * O clique no card é repassado para quem usa o adapter (o Fragment abre a tela 3).
 */
class QuadraAdapter(
    private val aoClicar: (Quadra) -> Unit
) : ListAdapter<Quadra, QuadraAdapter.QuadraViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuadraViewHolder {
        val binding = ItemQuadraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuadraViewHolder(binding, aoClicar)
    }

    override fun onBindViewHolder(holder: QuadraViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class QuadraViewHolder(
        private val binding: ItemQuadraBinding,
        private val aoClicar: (Quadra) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quadra: Quadra) {
            val contexto = binding.root.context
            binding.imageFoto.setImageResource(quadra.fotoRes)
            binding.textNome.text = quadra.nome
            binding.textBairro.text = quadra.bairro
            binding.textHorario.text = quadra.horario
            binding.textTipo.setText(quadra.tipo.rotulo())
            binding.textRolando.isVisible = quadra.rolandoAgora
            binding.textJogadores.text = contexto.getString(
                R.string.jogadores_contagem,
                quadra.confirmados.size,
                quadra.vagas
            )
            binding.progressOcupacao.max = quadra.vagas
            binding.progressOcupacao.progress = quadra.confirmados.size
            binding.root.setOnClickListener { aoClicar(quadra) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Quadra>() {
            override fun areItemsTheSame(antiga: Quadra, nova: Quadra) = antiga.id == nova.id
            override fun areContentsTheSame(antiga: Quadra, nova: Quadra) = antiga == nova
        }
    }
}
