import com.soywiz.korge.Korge
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets

/**
 * HTTP 接続を行うクライアント
 */
val httpClient = HttpClient {
    install(WebSockets)
}

/**
 * メインクラス
 */
suspend fun main() = Korge(Korge.Config(module = SceneModule))
