package com.nick.rachao.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.graphics.toColorInt
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.nick.rachao.R
import com.nick.rachao.model.Posicao
import com.nick.rachao.model.TipoQuadra

/** Texto (strings.xml) de cada posição. */
@StringRes
fun Posicao.rotulo(): Int = when (this) {
    Posicao.ARMADOR -> R.string.posicao_armador
    Posicao.ALA -> R.string.posicao_ala
    Posicao.PIVO -> R.string.posicao_pivo
}

/** Texto (strings.xml) de cada tipo de quadra. */
@StringRes
fun TipoQuadra.rotulo(): Int = when (this) {
    TipoQuadra.COBERTA -> R.string.tipo_coberta
    TipoQuadra.AO_AR_LIVRE -> R.string.tipo_ar_livre
}

/** Converte uma cor de colors.xml em "#AARRGGBB" (o modelo Time guarda a cor como String). */
fun Context.corHex(@ColorRes cor: Int): String =
    String.format("#%08X", ContextCompat.getColor(this, cor))

/** Lê a cor "#AARRGGBB" do Time de volta para Int. */
fun String.paraCor(): Int = toColorInt()

/** Leitura compatível de Parcelable vindo de uma Intent (putExtra). */
inline fun <reified T : Parcelable> Intent.parcelavel(chave: String): T? =
    IntentCompat.getParcelableExtra(this, chave, T::class.java)

/** Leitura compatível de Parcelable vindo de um Bundle (arguments / savedInstanceState). */
inline fun <reified T : Parcelable> Bundle.parcelavel(chave: String): T? =
    BundleCompat.getParcelable(this, chave, T::class.java)

inline fun <reified T : Parcelable> Bundle.listaParcelavel(chave: String): List<T>? =
    BundleCompat.getParcelableArrayList(this, chave, T::class.java)

/**
 * Tela cheia (edge-to-edge) com barras de sistema escuras e padding automático
 * para o conteúdo não ficar atrás da status bar / navigation bar / teclado.
 */
fun ComponentActivity.configurarTelaCheia(raiz: View) {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
    )
    ViewCompat.setOnApplyWindowInsetsListener(raiz) { view, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        view.updatePadding(
            left = barras.left,
            top = barras.top,
            right = barras.right,
            bottom = barras.bottom
        )
        insets
    }
}
