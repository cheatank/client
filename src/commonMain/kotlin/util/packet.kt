package util

import com.github.cheatank.common.Packet
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.data.PacketData
import io.ktor.http.cio.websocket.DefaultWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes

/**
 * パケットを送信する
 */
suspend fun <T : PacketData> DefaultWebSocketSession.sendPacket(packetType: PacketType<T>, data: T) {
    send(Frame.Binary(true, Packet.toByteArray(packetType, data)))
}

/**
 * パケットを受信する
 */
suspend fun <T : PacketData> DefaultWebSocketSession.receivePacket(packetType: PacketType<T>): Packet<T>? {
    return Packet.fromByteArray(incoming.receive().readBytes())?.toPacket(packetType)
}
