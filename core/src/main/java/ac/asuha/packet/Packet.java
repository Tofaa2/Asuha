package ac.asuha.packet;

import ac.asuha.buffer.PacketBuffer;

/**
 * Generic packet super-interface
 */
public sealed interface Packet {


    /**
     * Packets that are sent by the server
     */
    non-sealed interface Server extends Packet{

        void write(PacketBuffer buffer);

    }

    /**
     * Packets that are sent by the client
     */
    non-sealed interface Client extends Packet{

        void read(PacketBuffer buffer);

    }


}
