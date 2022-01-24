package scene

import Theme
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.PacketVersion
import com.github.cheatank.common.data.ConfigData
import com.github.cheatank.common.data.EmptyPacketData
import com.github.cheatank.common.data.IntData
import com.github.cheatank.common.data.ShortData
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korio.async.launchImmediately
import exception.FailReceivePacketException
import exception.MismatchPacketVersionException
import injects.ErrorMessage
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import util.readRawPacket
import util.receivePacket
import util.sendPacket

/**
 * ゲーム画面
 */
class GameScene(private val address: String) : Scene() {
    override suspend fun Container.sceneInit() {
        val isWait = ObservableProperty(true)
        val time = ObservableProperty<Short>(-1)
        text("Waiting...", 48.0, Theme.Text) {
            centerOnStage()
            isWait.observe {
                textSize = if (it) 48.0 else 0.0
            }
        }
        text("${time.value}", 0.0, Theme.Text) {
            centerXOnStage()
            time.observe {
                if (it < 0) {
                    textSize = 0.0
                } else {
                    textSize = 36.0
                    text = "$it"
                }
            }
        }
        launchImmediately {
            val httpClient = HttpClient {
                install(WebSockets)
            }
            var exception: Exception? = null
            try {
                httpClient.webSocket(address) {
                    checkPacketVersion()
                    val configData = joinLobby()
                    isWait.value = false
                    time.value = configData.timeLimit
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Binary -> {
                                val packet = frame.readRawPacket() ?: continue
                                when (packet.id) {
                                    PacketType.Countdown.id -> {
                                        val data = packet.toPacket(PacketType.Countdown)?.data as? ShortData ?: continue
                                        time.value = data.short
                                    }
                                }
                            }
                            is Frame.Close -> {
                                close()
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                exception = ex
            } finally {
                httpClient.close()
                if (exception != null) {
                    sceneContainer.changeTo<TitleScene>(ErrorMessage(exception))
                } else {
                    sceneContainer.changeTo<TitleScene>()
                }
            }
        }
    }

    /**
     * クライアントとサーバーのパケットバージョンが同じであるか調べる
     *
     * @exception [MismatchPacketVersionException] パケットバージョンが一致しない
     * @exception [FailReceivePacketException] パケットの受け取りに失敗した
     */
    private suspend fun DefaultWebSocketSession.checkPacketVersion() {
        sendPacket(PacketType.GetVersion, EmptyPacketData)
        val packet = receivePacket(PacketType.SendVersion)
        val packetData = packet?.data
        when {
            packetData !is IntData -> {
                throw FailReceivePacketException.failReceivePacketException(PacketType.SendVersion, packet)
            }
            packetData.int != PacketVersion -> {
                throw MismatchPacketVersionException()
            }
        }
    }

    /**
     * ロビーに参加する
     */
    private suspend fun DefaultWebSocketSession.joinLobby(): ConfigData {
        sendPacket(PacketType.JoinQueue, EmptyPacketData)
        val packet = receivePacket(PacketType.StartGame)
        val packetData = packet?.data
        if (packetData !is ConfigData) throw FailReceivePacketException.failReceivePacketException(
            PacketType.StartGame,
            packet
        )
        return packetData
    }
}
