package util

import com.github.cheatank.common.Packet
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.RawPacket
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
 * 生のパケットとして読み取る
 */
fun ByteArray.readRawPacket(): RawPacket? {
    return Packet.fromByteArray(this)
}

/**
 * 生のパケットとして読み取る
 */
fun Frame.readRawPacket(): RawPacket? {
    return data.readRawPacket()
}

/**
 * パケットとして読み取る
 */
fun <T : PacketData> ByteArray.readPacket(packetType: PacketType<T>): Packet<T>? {
    return readRawPacket()?.toPacket(packetType)
}

/**
 * パケットを受信する
 */
suspend fun <T : PacketData> DefaultWebSocketSession.receivePacket(packetType: PacketType<T>): Packet<T>? {
    return incoming.receive().readBytes().readPacket(packetType)
}
