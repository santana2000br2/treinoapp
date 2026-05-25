package com.example.treinoapp

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object FotoEvolucaoImagemUtil {

    private const val PASTA = "fotos_evolucao"

    /** Caminho relativo em `filesDir` (ex.: `fotos_evolucao/s1_frente_123.jpg`). */
    fun persistirDaGaleria(
        context: Context,
        uriOrigem: Uri,
        sessaoId: Long,
        posicao: String,
    ): String? {
        return try {
            val dir = File(context.filesDir, PASTA).apply { mkdirs() }
            val arquivo = File(dir, "s${sessaoId}_${posicao}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uriOrigem)?.use { input ->
                FileOutputStream(arquivo).use { output -> input.copyTo(output) }
            } ?: return null
            if (!arquivo.exists()) return null
            "$PASTA/${arquivo.name}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun arquivoDePathRelativo(context: Context, pathRelativo: String): File =
        File(context.filesDir, pathRelativo)

    fun uriParaExibicao(context: Context, pathRelativo: String): Uri? {
        if (pathRelativo.isBlank()) return null
        val file = arquivoDePathRelativo(context, pathRelativo)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }

    fun apagarArquivo(context: Context, pathRelativo: String?) {
        if (pathRelativo.isNullOrBlank()) return
        try {
            arquivoDePathRelativo(context, pathRelativo).delete()
        } catch (_: Exception) {
        }
    }
}
