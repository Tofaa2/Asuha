package ac.asuha.packet;

import java.util.UUID;

public record QueuedPacket(
        UUID playerId,
        int packetId,
        byte[] body
) {
}
