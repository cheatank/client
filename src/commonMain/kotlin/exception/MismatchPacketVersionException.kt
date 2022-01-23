package exception

/**
 * パケットのバージョンが一致しない
 */
class MismatchPacketVersionException(message: String = "Packet version do not match.") : IllegalStateException(message)
