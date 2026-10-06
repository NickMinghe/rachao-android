package com.nick.rachao.util

import com.nick.rachao.model.Jogador
import com.nick.rachao.model.Time

/**
 * Sorteio de 2 times equilibrados por nível.
 * Estratégia "draft em cobra" (A, B, B, A, A, B, B, A...) sobre os jogadores ordenados por nível:
 * os tamanhos ficam iguais (±1) e a soma dos níveis fica próxima.
 * O shuffle antes da ordenação estável faz jogadores de mesmo nível trocarem de time a cada sorteio.
 */
object Sorteio {

    fun sortear(
        jogadores: List<Jogador>,
        nomeTimeA: String,
        corTimeA: String,
        nomeTimeB: String,
        corTimeB: String
    ): Pair<Time, Time> {
        val ordenados = jogadores.shuffled().sortedByDescending { it.nivel }
        val (timeA, timeB) = ordenados
            .withIndex()
            .partition { (indice, _) -> indice % 4 == 0 || indice % 4 == 3 }
        return Time(nomeTimeA, corTimeA, timeA.map { it.value }) to
            Time(nomeTimeB, corTimeB, timeB.map { it.value })
    }
}
