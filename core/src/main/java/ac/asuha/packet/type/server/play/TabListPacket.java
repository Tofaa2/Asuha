package ac.asuha.packet.type.server.play;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;
import net.kyori.adventure.text.Component;

public record TabListPacket(Component header, Component footer) implements Packet.Server {
    @Override
    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.COMPONENT, header);
        buffer.write(NetworkBuffer.COMPONENT, footer);
    }
}
