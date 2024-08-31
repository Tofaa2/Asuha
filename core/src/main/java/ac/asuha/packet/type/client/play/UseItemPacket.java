package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.protocol.Hand;

public class UseItemPacket implements Packet.Client{

    private Hand hand;
    private int sequence;
    private float yaw, pitch;


    @Override
    public void read(NetworkBuffer buffer) {
        hand = buffer.read(NetworkBuffer.HAND);
        sequence = buffer.read(NetworkBuffer.VAR_INT);
        yaw = buffer.read(NetworkBuffer.FLOAT);
        pitch = buffer.read(NetworkBuffer.FLOAT);
    }
}
