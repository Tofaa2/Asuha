package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.protocol.Hand;

public final class SwingArmPacket implements Packet.Client {

    private Hand hand;

    public Hand hand() {
        return hand;
    }

    @Override
    public void read(NetworkBuffer buffer) {
        this.hand = buffer.read(NetworkBuffer.HAND);
    }
}
