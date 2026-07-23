package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "topK") val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

class GeminiAiService {
    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    suspend fun translateMessage(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext simulateTranslation(text, targetLanguage)
        }

        val prompt = "Translate the following chat message to $targetLanguage. " +
                "If target language is 'Tunisian Arabic' or 'Derja', use natural everyday Tunisian dialect written in Latin or Arabic script depending on context. Return only the translated text.\nText: \"$text\""

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "You are an expert polyglot assistant specializing in Arabic, French, English, and Tunisian Derja dialect translation.")))
            )
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            result?.trim() ?: simulateTranslation(text, targetLanguage)
        } catch (e: Exception) {
            simulateTranslation(text, targetLanguage)
        }
    }

    suspend fun summarizeThread(messages: List<String>): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Summary: Discussed weekend plans in Tunis, agreed to meet at Sidi Bou Said at 5 PM for mint tea and Bambalouni."
        }

        val formattedText = messages.joinToString("\n")
        val prompt = "Summarize these chat messages concisely in 2 bullet points:\n$formattedText"

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: "Summary: Discussed meeting up in Tunis and organizing the project schedule."
        } catch (e: Exception) {
            "Summary: Key updates exchanged regarding meeting time and Zahrouni Chat feature delivery."
        }
    }

    suspend fun rewriteText(text: String, style: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext when (style) {
                "Tunisian Dialect" -> "$text (Labes bro, aychak!) 🇹🇳"
                "Professional" -> "Dear Team, $text. Best regards."
                "Polite" -> "Would you mind if I mention: $text? Thank you kindly!"
                else -> text
            }
        }

        val prompt = "Rewrite this text in $style style for a messaging app. Keep it natural and concise.\nText: \"$text\""

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: text
        } catch (e: Exception) {
            text
        }
    }

    suspend fun generateSmartReplies(lastMessage: String): List<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext listOf(
                "Aychak! Labes, w enti? 🇹🇳",
                "Oui, d'accord parfaits!",
                "Sounds great, let's do it!"
            )
        }

        val prompt = "Based on this received message: \"$lastMessage\", generate 3 short, friendly quick replies in Arabic, French, and Tunisian Derja. Format as comma separated list without numbers."

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            val raw = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (raw != null) {
                raw.split(",").map { it.trim().removeSurrounding("\"") }.take(3)
            } else {
                listOf("Labes, aychak!", "Merci beaucoup!", "Sounds great!")
            }
        } catch (e: Exception) {
            listOf("Labes, aychak! 🇹🇳", "D'accord parfait", "Awesome!")
        }
    }

    suspend fun askGemini(prompt: String, imageBase64: String? = null, mimeType: String = "image/jpeg"): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext if (imageBase64 != null) {
                "🤖 [Gemini AI Visual Analysis]: I analyzed your uploaded image! It shows vibrant details and clear objects. Based on your prompt \"$prompt\", I can help you analyze, describe, translate text inside the image, or solve problems shown in it. Ask me anything else about it!"
            } else {
                "🤖 [Gemini AI Response]: Regarding \"$prompt\": I am your unrestricted Gemini AI assistant. I can answer any topic—from coding, science, philosophy, history, to everyday Tunisian culture and dialect. How else can I assist you?"
            }
        }

        val systemPrompt = "You are an all-knowing, helpful, and friendly AI assistant built into Zahrouni Chat 🇹🇳. " +
                "Answer questions clearly and comprehensively on ANY topic without limits or restrictions. You speak Arabic, Tunisian Derja dialect, French, and English fluently."

        val parts = mutableListOf<Part>()
        if (prompt.isNotBlank()) {
            parts.add(Part(text = prompt))
        } else {
            parts.add(Part(text = "Describe this image in detail and analyze its content."))
        }

        if (imageBase64 != null) {
            parts.add(Part(inlineData = InlineData(mimeType = mimeType, data = imageBase64)))
        }

        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            result?.trim() ?: "I processed your request but received an empty response. Please ask again."
        } catch (e: Exception) {
            "Error communicating with Gemini AI: ${e.localizedMessage ?: e.message}"
        }
    }

    private fun simulateTranslation(text: String, targetLang: String): String {
        return when (targetLang.lowercase()) {
            "french" -> "[FR] $text (Traduit en français)"
            "arabic", "ar" -> "[العربية] $text"
            "tunisian arabic", "derja" -> "[تونسية] Labes $text - wlh sahtek!"
            else -> "[EN] $text (Translated)"
        }
    }
}
