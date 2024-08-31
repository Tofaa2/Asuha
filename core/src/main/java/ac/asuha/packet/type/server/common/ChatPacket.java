package ac.asuha.packet.type.server.common;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;
import net.kyori.adventure.text.Component;


public record ChatPacket(Component message, boolean overlay) implements Packet.Server {

    public ChatPacket(Component message) {
        this(message, false);
    }

    @Override
    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.COMPONENT, message);
        buffer.write(NetworkBuffer.BOOLEAN, overlay);
    }
}
