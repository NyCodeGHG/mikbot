package dev.schlaubi.mikmusic.innerttube

import dev.kord.common.Locale
import dev.schlaubi.mikbot.plugin.api.util.convertToISO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val youtubeMusic = Url("https://music.youtube.com")
private val youtube = Url("https://www.youtube.com")


private val webContext = InnerTubeContext(InnerTubeContext.Client("WEB", "2.20220502.01.00"))

private fun musicContext(locale: Locale): InnerTubeContext {
    val isoLocale = locale.convertToISO()
    return InnerTubeContext(InnerTubeContext.Client("WEB_REMIX",
        "1.20220502.01.00",
        isoLocale.language,
        isoLocale.country!!))
}


object InnerTubeClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }

            json(json)
        }
    }

    suspend fun requestMusicAutoComplete(
        input: String,
        locale: Locale,
    ): InnerTubeBox<SearchSuggestionsSectionRendererContent> =
        makeRequest(youtubeMusic,
            "music",
            "get_search_suggestions",
            body = MusicSearchRequest(musicContext(locale), input)) {
            val localeString = if (locale.country != null) {
                "${locale.language}-${locale.country},${locale.language}"
            } else {
                locale.language
            }
            header(HttpHeaders.AcceptLanguage, localeString)
        }

    suspend fun requestVideoSearch(query: String): InnerTubeSingleBox<TwoColumnSearchResultsRendererContent> =
        makeRequest(
            youtube, "search", body = SearchRequest(webContext, query)
        )

    private suspend inline fun <reified B, reified R> makeRequest(
        domain: Url,
        vararg endpoint: String,
        body: B? = null,
        block: HttpRequestBuilder.() -> Unit = {}
    ): R =
        client.post(domain) {
            url {
                path("youtubei", "v1")
                appendPathSegments(endpoint.asList())
                parameter("prettyPrint", false)
            }

            header(HttpHeaders.Referrer, domain)

            if (body != null) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            block()
        }.body()
}
