package ac.asuha.packet.registry;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.packet.type.client.play.PositionPacket;
import ac.asuha.packet.type.client.play.SwingArmPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class ClientPacketRegistry {

    private final Map<Integer, Supplier<Packet.Client>> registry = new ConcurrentHashMap<>();

    public ClientPacketRegistry() {
        register(0x36, SwingArmPacket::new);
        register(0x1A, PositionPacket::new);
        register(0x1B, PositionPacket.WithRotation::new);
        register(0x1C, PositionPacket.SetRotation::new);
        register(0x1D, PositionPacket.SetOnGround::new);
    }

    public void register(int id, Supplier<Packet.Client> supplier) {
        registry.put(id, supplier);
    }

    public Packet.Client make(int id) {
        if (registry.containsKey(id)) {
            return registry.get(id).get();
        }
        return null;
    }

    public Packet.Client makeAndRead(int id, PacketBuffer buffer) {
        var packet = make(id);
        if (packet != null) {
            packet.read(buffer);
        }
        return packet;
    }


}
