package scene

import Theme
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.PacketVersion
import com.github.cheatank.common.data.ConfigData
import com.github.cheatank.common.data.EmptyPacketData
import com.github.cheatank.common.data.IntData
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.text
import com.soywiz.korio.async.launchImmediately
import exception.FailReceivePacketException
import exception.MismatchPacketVersionException
import httpClient
import injects.ErrorMessage
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import util.receivePacket
import util.sendPacket

/**
 * 待機画面
 *
 * @property address サーバーアドレス
 */
class WaitingScene(private val address: String) : Scene() {
    override suspend fun Container.sceneInit() {
        text("Waiting...", 48.0, Theme.Text).centerOnStage()
        launchImmediately {
            try {
                httpClient.webSocket(address) {
                    checkPacketVersion()
                    val configData = joinLobby()
                    sceneContainer.changeTo<GameScene>(configData)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                sceneContainer.changeTo<TitleScene>(ErrorMessage(ex))
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
        outgoing.sendPacket(PacketType.GetVersion, EmptyPacketData)
        val packet = incoming.receivePacket(PacketType.SendVersion)
        val packetData = packet?.data
        when {
            packetData !is IntData -> {
                throw FailReceivePacketException()
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
        outgoing.sendPacket(PacketType.JoinQueue, EmptyPacketData)
        val packet = incoming.receivePacket(PacketType.StartGame)
        val packetData = packet?.data
        if (packetData !is ConfigData) throw FailReceivePacketException()
        return packetData
    }
}
