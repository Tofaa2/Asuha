package ac.asuha;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.packet.Packet;
import ac.asuha.packet.QueuedPacket;
import ac.asuha.packet.registry.ClientPacketRegistry;
import ac.asuha.packet.registry.ServerPacketRegistry;
import ac.asuha.packet.type.client.play.PositionPacket;
import ac.asuha.packet.type.server.common.ChatPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;


public final class Asuha {

    private final AsuhaConfig config;

    private final Deque<QueuedPacket> queuedPackets;

    private final ServerPacketRegistry serverPackets;
    private final ClientPacketRegistry clientPackets;

    public Asuha(AsuhaConfig config) {
        this.config = config;
        this.queuedPackets = new ArrayDeque<>();

        this.serverPackets = new ServerPacketRegistry();
        this.clientPackets = new ClientPacketRegistry();
    }


    public void consume(QueuedPacket packet) {
        PacketBuffer buffer = PacketBuffer.create(packet.body());
        int id = packet.packetId();
        Packet.Client p = clientPackets.makeAndRead(id, buffer);
        if (p == null) {
            return;
        }

        if (p instanceof PositionPacket e) {
            sendPacket(
                    new ChatPacket(Component.text("Position packet", NamedTextColor.RED)),
                    packet.playerId()
            );
        }
    }

    public QueuedPacket poll() {
        if (queuedPackets.isEmpty()) {
            return null;
        }
        return queuedPackets.removeFirst();
    }

    public void sendPacket(Packet.Server packet, UUID player) {
        int id = serverPackets.getId(packet.getClass());
        PacketBuffer buffer = PacketBuffer.allocate(1024);
        packet.write(buffer);
        queuedPackets.addLast(new QueuedPacket(player, id, buffer.getRawBytes()));
    }


}
