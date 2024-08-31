package ac.asuha;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.packet.registry.ClientPacketRegistry;
import ac.asuha.packet.registry.ServerPacketRegistry;
import ac.asuha.packet.type.client.play.UseItemPacket;
import ac.asuha.packet.type.server.common.ChatPacket;
import net.kyori.adventure.text.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;


public final class Asuha {

    private final AsuhaConfig config;

    private final Deque<Packet.FromAsuha> queuedPackets;

    private final ServerPacketRegistry serverPackets;
    private final ClientPacketRegistry clientPackets;

    public Asuha(AsuhaConfig config) {
        this.config = config;
        this.queuedPackets = new ArrayDeque<>();

        this.serverPackets = new ServerPacketRegistry();
        this.clientPackets = new ClientPacketRegistry();
    }


    public void consume(Packet.ToAsuha packet) {
        var buffer = packet.body();
        //NetworkBuffer buffer = NetworkBuffer.wrap(packet.body(), 0, 0);
        int id = packet.packetId();
        Packet.Client p = clientPackets.makeAndRead(id, buffer);
        if (p == null) {
            return;
        }

        if (p instanceof UseItemPacket e) {
            System.out.println("UseItemPacket");
            sendPacket(new ChatPacket(Component.text("Header"), false), packet.playerId());
        }
    }

    public Packet.FromAsuha poll() {
        if (queuedPackets.isEmpty()) {
            return null;
        }
        return queuedPackets.pop();
    }

    public void sendPacket(Packet.Server packet, UUID player) {
        int id = serverPackets.getId(packet.getClass());
        NetworkBuffer buffer = NetworkBuffer.resizableBuffer();
        packet.write(buffer);

        queuedPackets.addLast(new Packet.FromAsuha(player, id, buffer));
    }

}
