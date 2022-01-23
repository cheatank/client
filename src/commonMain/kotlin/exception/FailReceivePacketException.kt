package exception

/**
 * パケットの取得に失敗した
 */
class FailReceivePacketException(message: String = "Failed to receive the version packet.") : IllegalStateException(message)
