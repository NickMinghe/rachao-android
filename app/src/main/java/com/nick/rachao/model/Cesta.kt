package com.nick.rachao.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Qual time fez a cesta. */
enum class Lado { LARANJA, AZUL }

/** Uma cesta do histórico do placar (imutável). */
@Parcelize
data class Cesta(val lado: Lado, val pontos: Int) : Parcelable
