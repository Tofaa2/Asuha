package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.protocol.Hand;

public final class SwingArmPacket implements Packet.Client {

    private Hand hand;

    public Hand hand() {
        return hand;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.hand = buffer.readEnum(Hand.class);
    }
}
