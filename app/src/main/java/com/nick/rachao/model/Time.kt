package com.nick.rachao.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * REQUISITO: data class imutável. Somar pontos = time.copy(pontos = time.pontos + n).
 * @param cor cor em hexadecimal (#AARRGGBB), gerada a partir de colors.xml.
 */
@Parcelize
data class Time(
    val nome: String,
    val cor: String,
    val jogadores: List<Jogador>,
    val pontos: Int = 0
) : Parcelable {
    val nivelMedio: Double
        get() = if (jogadores.isEmpty()) 0.0 else jogadores.map { it.nivel }.average()
}
