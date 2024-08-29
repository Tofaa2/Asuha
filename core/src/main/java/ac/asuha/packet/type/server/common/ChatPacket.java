package ac.asuha.packet.type.server.common;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;


public record ChatPacket(Component message, boolean overlay) implements Packet.Server {

    public ChatPacket(Component message) {
        this(message, false);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.write(PacketBuffer.Type.STRING, GsonComponentSerializer.gson().serialize(message));
        buffer.write(PacketBuffer.Type.BOOL, overlay);
    }
}
