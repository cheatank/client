package exception

import com.github.cheatank.common.Packet
import com.github.cheatank.common.PacketType
import com.github.cheatank.common.data.PacketData

/**
 * パケットの取得に失敗した
 */
class FailReceivePacketException(
    message: String = "Failed to receive the version packet."
) : IllegalStateException(message) {
    companion object {
        fun <T : PacketData, U : PacketData> failReceivePacketException(expected: PacketType<T>, actual: Packet<U>?): FailReceivePacketException {
            return FailReceivePacketException(
                "Failed to receive the version packet: (expected: ${expected::class.simpleName}, actual: ${actual?.let { it.type::class.simpleName }})"
            )
        }
    }
}
