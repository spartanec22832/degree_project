package com.sfedu.degree_android.core.network

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

object ApiErrorParser {

    fun humanMessage(e: HttpException): String {
        val code = e.code()
        val raw = runCatching { e.response()?.errorBody()?.string() }.getOrNull()

        if (raw.isNullOrBlank()) return "HTTP $code"

        return runCatching {
            val json = JSONObject(raw)

            // 1) Основное сообщение
            val message = json.optString("message").takeIf { it.isNotBlank() }

            // 2) Детали (часто для валидации)
            val details = json.opt("details")
            val detailsText = detailsToText(details)

            when {
                !detailsText.isNullOrBlank() && !message.isNullOrBlank() -> "$message\n$detailsText"
                !message.isNullOrBlank() -> message
                !detailsText.isNullOrBlank() -> detailsText
                else -> "HTTP $code"
            }
        }.getOrElse {
            // если JSON неожиданного формата
            raw
        }
    }

    private fun detailsToText(details: Any?): String? {
        if (details == null || details == JSONObject.NULL) return null

        return when (details) {
            is JSONObject -> {
                // ожидаем что может быть map: field -> message или что-то похожее
                val keys = details.keys().asSequence().toList()
                if (keys.isEmpty()) null
                else keys.joinToString("\n") { k ->
                    val v = details.opt(k)
                    "• $k: ${vToString(v)}"
                }
            }

            is JSONArray -> {
                if (details.length() == 0) null
                else (0 until details.length()).joinToString("\n") { idx ->
                    "• ${vToString(details.opt(idx))}"
                }
            }

            else -> vToString(details)
        }
    }

    private fun vToString(v: Any?): String {
        if (v == null || v == JSONObject.NULL) return ""
        return when (v) {
            is JSONObject -> v.toString()
            is JSONArray -> v.toString()
            else -> v.toString()
        }
    }
}
