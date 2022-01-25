package scene

import Theme
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.PacketVersion
import com.github.cheatank.common.data.EmptyPacketData
import com.github.cheatank.common.data.GameData
import com.github.cheatank.common.data.IntData
import com.github.cheatank.common.data.LocationData
import com.github.cheatank.common.data.SelfLocationData
import com.github.cheatank.common.data.ShortData
import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.RoundRect
import com.soywiz.korge.view.alignTopToBottomOf
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.position
import com.soywiz.korge.view.roundRect
import com.soywiz.korge.view.text
import com.soywiz.korge.view.tween.hide
import com.soywiz.korge.view.tween.show
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.ObservableProperty
import com.soywiz.korio.async.launchImmediately
import exception.FailReceivePacketException
import exception.MismatchPacketVersionException
import injects.CheatMode
import injects.Message
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.delay
import util.readRawPacket
import util.receivePacket
import util.sendPacket

/**
 * ゲーム画面
 */
class GameScene(private val address: String, private val cheat: CheatMode) : Scene() {
    override suspend fun Container.sceneInit() {
        val selfX = ObservableProperty(0)
        val selfY = ObservableProperty(0)
        val isWait = ObservableProperty(true)
        val time = ObservableProperty<Short>(-1)
        val players = mutableMapOf<Short, RoundRect>()
        val waitTitle = text("Waiting...", 48.0, Theme.Text) {
            alignment = TextAlignment.MIDDLE_CENTER
            centerOnStage()
        }
        val timer = text("", 36.0, Theme.Text) {
            centerXOnStage()
            time.observe {
                text = if (it < 0) "" else "$it"
            }
        }
        roundRect(1200.0, 600.0, 0.0, fill = Theme.BackGround, stroke = Theme.Text, strokeThickness = 1.0) {
            alignTopToBottomOf(timer, 10.0)
            centerXOnStage()
            alpha = 0.0
            isWait.observe {
                launchImmediately {
                    if (it) {
                        hide(0.0.seconds)
                    } else {
                        show(0.0.seconds)
                    }
                }
            }
        }
        if (cheat.isEnable) {
            onClick {
                val pos = it.currentPosStage
                selfX.value = pos.x.toInt()
                selfY.value = pos.y.toInt()
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
                    val gameData = joinLobby()
                    isWait.value = false
                    val selfId = gameData.id
                    time.value = gameData.timeLimit
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Binary -> {
                                val packet = frame.readRawPacket() ?: continue
                                when (packet.id) {
                                    PacketType.Countdown.id -> {
                                        val data = packet.toPacket(PacketType.Countdown)?.data as? ShortData ?: continue
                                        time.value = data.short
                                    }
                                    PacketType.UpdateLocation.id -> {
                                        val (id, x, y) = packet.toPacket(PacketType.UpdateLocation)?.data as? LocationData ?: continue
                                        if (id == selfId) {
                                            selfX.value = x
                                            selfY.value = y
                                        }
                                        val player = players.getOrPut(id) {
                                            if (id == selfId) {
                                                roundRect(50, 100, 0, fill = Theme.ErrorText) {
                                                    keys {
                                                        down(Key.W) {
                                                            selfY.value -= 5
                                                        }
                                                        down(Key.A) {
                                                            selfX.value -= 5
                                                        }
                                                        down(Key.S) {
                                                            selfY.value += 5
                                                        }
                                                        down(Key.D) {
                                                            selfX.value += 5
                                                        }
                                                    }
                                                    selfX.observe {
                                                        this.x = it.toDouble()
                                                    }
                                                    selfY.observe {
                                                        this.y = it.toDouble()
                                                    }
                                                    launchImmediately {
                                                        while (isWait.value.not()) {
                                                            delay(50)
                                                            sendPacket(PacketType.UpdateSelfLocation, SelfLocationData(selfX.value, selfY.value, 0))
                                                        }
                                                    }
                                                }
                                            } else {
                                                roundRect(50, 100, 0, fill = Theme.Text)
                                            }
                                        }
                                        player.position(x, y)
                                    }
                                    PacketType.EndGame.id -> {
                                        val data = packet.toPacket(PacketType.EndGame)?.data as? ShortData ?: continue
                                        waitTitle.text = when (data.short) {
                                            (-1).toShort() -> "Draw"
                                            selfId -> "You Win"
                                            else -> "You Lose"
                                        }
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
                time.value = -1
                players.values.forEach { it.alpha = 0.0 }
                isWait.value = true
                launchImmediately {
                    if (isWait.value.not()) {
                        delay(3000)
                    }
                    if (exception != null) {
                        sceneContainer.changeTo<TitleScene>(Message(exception))
                    } else {
                        sceneContainer.changeTo<TitleScene>()
                    }
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
    private suspend fun DefaultWebSocketSession.joinLobby(): GameData {
        sendPacket(PacketType.JoinQueue, EmptyPacketData)
        val packet = receivePacket(PacketType.StartGame)
        val packetData = packet?.data
        if (packetData !is GameData) throw FailReceivePacketException.failReceivePacketException(
            PacketType.StartGame,
            packet
        )
        return packetData
    }
}
