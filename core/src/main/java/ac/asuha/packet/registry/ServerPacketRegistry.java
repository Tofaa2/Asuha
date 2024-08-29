package ac.asuha.packet.registry;

import ac.asuha.packet.Packet;
import ac.asuha.packet.type.server.common.ChatPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerPacketRegistry {

    private final Map<Class<? extends Packet.Server>, Integer> idRegistry = new ConcurrentHashMap<>();

    public ServerPacketRegistry() {
        register(0x6C, ChatPacket.class);
    }

    public <T extends Packet.Server> void register(int id, Class<T>clazz ) {
        idRegistry.put(clazz, id);
    }

    public int getId(Class<? extends Packet.Server> clazz) {
        return idRegistry.get(clazz);
    }


}
