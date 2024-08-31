package ac.asuha.packet;

import ac.asuha.buffer.NetworkBuffer;

import java.util.UUID;

/**
 * Generic packet super-interface
 */
public sealed interface Packet {

    record ToAsuha(
            UUID playerId,
            int packetId,
            NetworkBuffer body
    ) {}

    record FromAsuha(
            UUID playerId,
            int packetId,
            NetworkBuffer body
    ) {}

    /**
     * Packets that are sent by the server
     */
    non-sealed interface Server extends Packet{

        void write(NetworkBuffer buffer);

    }

    /**
     * Packets that are sent by the client
     */
    non-sealed interface Client extends Packet{

        void read(NetworkBuffer buffer);

    }


}
