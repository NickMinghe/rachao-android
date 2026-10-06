package com.nick.rachao.ui.quadras

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nick.rachao.R
import com.nick.rachao.adapter.QuadraAdapter
import com.nick.rachao.data.RachaoMock
import com.nick.rachao.databinding.FragmentQuadrasBinding
import com.nick.rachao.model.Jogador
import com.nick.rachao.model.Quadra
import com.nick.rachao.model.TipoQuadra
import com.nick.rachao.ui.detalhe.QuadraDetalheActivity
import com.nick.rachao.util.Extras
import com.nick.rachao.util.parcelavel

/**
 * ITEM OPCIONAL: tela funcional dentro de um Fragment, usando o ciclo de vida
 * (onCreateView / onViewCreated / onDestroyView) e ViewBinding.
 */
class QuadrasFragment : Fragment() {

    // Padrão de ViewBinding em Fragment: _binding nulo fora do ciclo de vida da View
    private var _binding: FragmentQuadrasBinding? = null
    private val binding get() = _binding!!

    private lateinit var jogador: Jogador
    private lateinit var adapter: QuadraAdapter

    // CICLO DE VIDA 1: infla o layout via ViewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuadrasBinding.inflate(inflater, container, false)
        return binding.root
    }

    // CICLO DE VIDA 2: a View existe -> configura lista, filtros e textos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Jogador recebido via arguments
        jogador = requireArguments().parcelavel<Jogador>(Extras.JOGADOR)
            ?: error("QuadrasFragment precisa de um jogador nos arguments")

        // REQUISITO (opcionais): apelido ?: nome
        binding.textSaudacao.text = getString(R.string.saudacao, jogador.apelido ?: jogador.nome)

        adapter = QuadraAdapter { quadra -> abrirDetalhe(quadra) }
        binding.recyclerQuadras.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerQuadras.adapter = adapter

        // REQUISITO: interação que atualiza a interface na hora (chips de filtro)
        binding.chipGroupFiltro.setOnCheckedStateChangeListener { grupo, _ ->
            aplicarFiltro(grupo.checkedChipId)
        }
        aplicarFiltro(binding.chipGroupFiltro.checkedChipId)
    }

    private fun aplicarFiltro(chipId: Int) {
        val tipo: TipoQuadra? = when (chipId) {
            R.id.chipCobertas -> TipoQuadra.COBERTA
            R.id.chipArLivre -> TipoQuadra.AO_AR_LIVRE
            else -> null // "Todas"
        }
        // filter cria uma NOVA lista; o mock nunca é alterado
        val lista = RachaoMock.quadras.filter { tipo == null || it.tipo == tipo }
        adapter.submitList(lista)

        binding.textVazio.isVisible = lista.isEmpty()

        val rolando = lista.count { it.rolandoAgora }
        binding.textRolandoAgora.text = if (rolando == 0) {
            getString(R.string.nenhum_racha_rolando)
        } else {
            resources.getQuantityString(R.plurals.rachas_rolando, rolando, rolando)
        }
    }

    private fun abrirDetalhe(quadra: Quadra) {
        // REQUISITO: Intent explícita + putExtra (id da quadra e dados do jogador)
        val intent = Intent(requireContext(), QuadraDetalheActivity::class.java)
            .putExtra(Extras.QUADRA_ID, quadra.id)
            .putExtra(Extras.JOGADOR, jogador)
        startActivity(intent)
    }

    // CICLO DE VIDA 3: a View foi destruída -> libera o binding (evita vazamento de memória)
    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerQuadras.adapter = null
        _binding = null
    }

    companion object {
        fun novaInstancia(jogador: Jogador): QuadrasFragment = QuadrasFragment().apply {
            arguments = bundleOf(Extras.JOGADOR to jogador)
        }
    }
}
