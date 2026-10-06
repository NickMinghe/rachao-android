package com.nick.rachao.model

import androidx.annotation.DrawableRes

/**
 * REQUISITO: modelo imutável (só val) com opcionais:
 *  - temIluminacao: Boolean? -> null = "Não informado"
 *  - regrasDaCasa: String?   -> null = bloco escondido (isVisible)
 * Para entrar/sair do racha usamos copy(confirmados = ...) — nunca mutamos o objeto.
 */
data class Quadra(
    val id: Int,
    val nome: String,
    val bairro: String,
    val tipo: TipoQuadra,
    val horario: String,
    val temIluminacao: Boolean?,
    val regrasDaCasa: String?,
    val rolandoAgora: Boolean,
    val vagas: Int,
    val confirmados: List<Jogador>,
    @param:DrawableRes val fotoRes: Int
) {
    val vagasRestantes: Int get() = (vagas - confirmados.size).coerceAtLeast(0)
    val lotada: Boolean get() = confirmados.size >= vagas
}
