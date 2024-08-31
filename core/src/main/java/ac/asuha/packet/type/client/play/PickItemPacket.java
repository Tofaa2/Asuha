package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;

public class PickItemPacket implements Packet.Client {

    private int slot;

    public int slot() {
        return slot;
    }

    @Override
    public void read(NetworkBuffer buffer) {
        slot = buffer.read(NetworkBuffer.VAR_INT);
    }
}
