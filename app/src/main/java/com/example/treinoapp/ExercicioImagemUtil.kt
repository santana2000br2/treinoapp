package com.example.treinoapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer

object ExercicioImagemUtil {

    private val slugsAlternativosPorNome = mapOf(
        "Desenvolvimento com Barra" to listOf("desenvolvimento_barra"),
        "Supino Reto com Barra" to listOf("supino_reto_barra"),
        "Agachamento Livre" to listOf("agachamento_livre"),
        "Puxada Frente na Polia" to listOf("puxada_frente_polia"),
    )

    fun nomeParaSlug(nome: String): String {
        val semAcentos = Normalizer.normalize(nome, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
        return semAcentos
            .lowercase()
            .replace("°", "")
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }

    fun slugsCandidatos(nome: String, arquivoImagem: String? = null): List<String> {
        val candidatos = linkedSetOf<String>()
        arquivoImagem?.trim()?.takeIf { it.isNotEmpty() }?.let { candidatos.add(it) }
        candidatos.add(nomeParaSlug(nome))
        slugsAlternativosPorNome[nome].orEmpty().forEach { candidatos.add(it) }
        return candidatos.toList()
    }

    fun parseUri(valor: String?): Uri? {
        if (valor.isNullOrBlank()) return null
        return try {
            Uri.parse(valor)
        } catch (_: Exception) {
            null
        }
    }

    fun vincularImagemSeExistir(
        context: Context,
        nome: String,
        arquivoImagem: String? = null,
    ): String? {
        for (slug in slugsCandidatos(nome, arquivoImagem)) {
            val resId = context.resources.getIdentifier(slug, "drawable", context.packageName)
            if (resId != 0) {
                return copiarDrawableParaArquivo(context, resId, "$slug.png")
            }
        }
        return null
    }

    fun uriImagemValida(context: Context, uri: String?): Boolean {
        if (uri.isNullOrBlank()) return false
        return try {
            val parsed = Uri.parse(uri)
            when (parsed.scheme) {
                "file" -> {
                    val path = parsed.path ?: return false
                    File(path).exists()
                }
                "content" -> {
                    context.contentResolver.openInputStream(parsed)?.use { true } ?: false
                }
                else -> false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun copiarDrawableParaArquivo(
        context: Context,
        resourceId: Int,
        nomeArquivo: String,
    ): String? {
        return try {
            val bitmap = decodeSampledResource(context, resourceId) ?: return null
            val file = File(context.filesDir, nomeArquivo)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 92, outputStream)
            }
            if (!file.exists()) return null
            uriDoArquivo(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeSampledResource(context: Context, resourceId: Int, maxPx: Int = 1600): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(context.resources, resourceId, bounds)
        val sample = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxPx)
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inJustDecodeBounds = false
        }
        return BitmapFactory.decodeResource(context.resources, resourceId, opts)
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxPx: Int): Int {
        var sample = 1
        while (width / sample > maxPx || height / sample > maxPx) {
            sample *= 2
        }
        return sample.coerceAtLeast(1)
    }

    private fun uriDoArquivo(context: Context, file: File): String {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        ).toString()
    }
}
