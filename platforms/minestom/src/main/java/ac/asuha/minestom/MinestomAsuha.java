package ac.asuha.minestom;

import ac.asuha.Asuha;
import ac.asuha.AsuhaConfig;
import ac.asuha.packet.Packet;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.PacketVanilla;
import net.minestom.server.network.packet.server.ServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minestom.server.MinecraftServer.getPacketParser;

public class MinestomAsuha {

    private static final ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(MinestomAsuha.class);

    private final Asuha anticheat;

    public MinestomAsuha(AsuhaConfig config, EventNode<Event> node) {
        this.anticheat = new Asuha(config);
        node.addListener(PlayerPacketEvent.class, event -> {
            UUID player = event.getPlayer().getUuid();
            var info = getPacketParser().play().packetInfo(event.getPacket().getClass());
            int id = info.id();
            NetworkBuffer buffer = NetworkBuffer.resizableBuffer();
            info.serializer().write(buffer, event.getPacket());


            var asuhaBuffer = ac.asuha.buffer.NetworkBuffer.resizableBuffer();
            asuhaBuffer.write(ac.asuha.buffer.NetworkBuffer.RAW_BYTES, buffer.read(NetworkBuffer.RAW_BYTES));
            anticheat.consume(new Packet.ToAsuha(
                    player,
                    id,
                    asuhaBuffer
            ));
        });

        EXEC.scheduleAtFixedRate(() -> {

            var packet = anticheat.poll();
            if (packet == null) {
                return;
            }

            var player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(packet.playerId());
            if (player == null) {
                LOGGER.info("Asuha attempted to send a packet to a client that is not playing at the moment.");
                return;
            }
            NetworkBuffer minestomBuffer = NetworkBuffer.resizableBuffer();
            minestomBuffer.write(NetworkBuffer.RAW_BYTES, packet.body().read(ac.asuha.buffer.NetworkBuffer.RAW_BYTES));
            minestomBuffer.readIndex(0);
            var info = PacketVanilla.SERVER_PACKET_PARSER.play().packetInfo(packet.packetId());
            if (info == null) {
                LOGGER.error("Asuha attempted to send a packet with a non-valid platform id to the client");
                return;
            }
            ServerPacket p;
            try {
                p = info.serializer().read(minestomBuffer);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (p == null) {
                LOGGER.info("Asuha attempted to send a packet to a client but its null");
                return;
            }
            player.sendPacket(p);
        }, 0, 5, TimeUnit.MILLISECONDS);
        MinecraftServer.getSchedulerManager().buildShutdownTask(EXEC::shutdownNow);
    }

}
