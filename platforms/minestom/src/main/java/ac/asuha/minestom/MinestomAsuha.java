package ac.asuha.minestom;

import ac.asuha.Asuha;
import ac.asuha.AsuhaConfig;
import ac.asuha.packet.QueuedPacket;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.PacketVanilla;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minestom.server.MinecraftServer.getPacketParser;

public class MinestomAsuha {

    private static final ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(1);


    private final Asuha anticheat;

    public MinestomAsuha(AsuhaConfig config, EventNode<Event> node) {
        this.anticheat = new Asuha(config);
        node.addListener(PlayerPacketEvent.class, event -> {
            UUID player = event.getPlayer().getUuid();
            var info = getPacketParser().play().packetInfo(event.getPacket().getClass());
            int id = info.id();
            NetworkBuffer buffer = NetworkBuffer.resizableBuffer();
            info.serializer().write(buffer, event.getPacket());
            anticheat.consume(new QueuedPacket(player, id, buffer.read(NetworkBuffer.RAW_BYTES)));
        });
        EXEC.scheduleAtFixedRate(() -> {
            var packet = anticheat.poll();
            if (packet == null) {
                return;
            }
            MinecraftServer.LOGGER.info("Looking!");

            var player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(packet.playerId());
            if (player == null) {
                MinecraftServer.LOGGER.info("Asuha attempted to send a packet to a client that is not playing at the moment.");
                return;
            }

            NetworkBuffer buffer = NetworkBuffer.wrap(packet.body(), packet.body().length, packet.body().length);
            var info = PacketVanilla.SERVER_PACKET_PARSER.play().packetInfo(packet.packetId());
            var p = info.serializer().read(buffer);
            if (p == null) {
                MinecraftServer.LOGGER.info("Asuha attempted to send a packet to a client but its null");
                return;
            }
            MinecraftServer.LOGGER.info("Asuha sent packet to client");
        }, 0, 50, TimeUnit.MILLISECONDS);
        MinecraftServer.getSchedulerManager().buildShutdownTask(EXEC::shutdownNow);
    }

}
