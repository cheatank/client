package util

import com.github.cheatank.common.Packet
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.data.PacketData
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

/**
 * パケットを送信する
 */
suspend fun <T : PacketData> SendChannel<Frame>.sendPacket(packetType: PacketType<T>, data: T) {
    send(Frame.Binary(true, Packet.toByteArray(packetType, data)))
}

/**
 * パケットを受信する
 */
suspend fun <T : PacketData> ReceiveChannel<Frame>.receivePacket(packetType: PacketType<T>): Packet<T>? {
    return Packet.fromByteArray(receive().readBytes())?.toPacket(packetType)
}
