package com.nick.rachao.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * REQUISITO: modelo imutável com data class (só val).
 * REQUISITO: valor opcional -> apelido: String? (tratado com ?.let / ?: / isVisible nas telas).
 * @Parcelize permite enviar o jogador por Intent com putExtra.
 */
@Parcelize
data class Jogador(
    val id: Int,
    val nome: String,
    val apelido: String?,
    val posicao: Posicao,
    val nivel: Int
) : Parcelable {

    /** Nome exibido na quadra: apelido se existir, senão o nome (operador Elvis ?:). */
    val nomeDeQuadra: String get() = apelido ?: nome

    companion object {
        /** Id reservado para o usuário do app (os mocks usam ids a partir de 1). */
        const val ID_USUARIO = 0
    }
}
