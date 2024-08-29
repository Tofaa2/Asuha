package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.packet.Packet;

public class PickItemPacket implements Packet.Client {

    private int slot;

    public int slot() {
        return slot;
    }

    @Override
    public void read(PacketBuffer buffer) {
        slot = buffer.read(PacketBuffer.Type.VAR_INT);
    }
}
