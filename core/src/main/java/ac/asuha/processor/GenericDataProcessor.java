package ac.asuha.processor;

import ac.asuha.Asuha;
import ac.asuha.event.player.PlayerClientBrandEvent;
import ac.asuha.packet.Packet;
import ac.asuha.packet.type.common.PluginMessagePacket;
import ac.asuha.player.AsuhaPlayer;

import java.nio.charset.StandardCharsets;

/**
 * Responsible for processing generic data such as client brand, player sprinting and sneaking state, ping, and such things.
 */
public record GenericDataProcessor(Asuha asuha, AsuhaPlayer player) implements PlayerProcessor {

    @Override
    public void process(Packet.Client packet) {
        if (packet instanceof PluginMessagePacket pluginMessagePacket) {
            handleClientBrand(pluginMessagePacket);
        }
    }

    private void handleClientBrand(PluginMessagePacket p) {
        var data = p.data();
        var id = p.identifier();

        if ("minecraft:brand".equalsIgnoreCase(id)) { // 1.12< use MC|Brand but old versions arent a focus rn
            int len = data.length;
            if (len == 0) {
                triggerViolation("length=0");
                return;
            }
            byte[] shrunk = new byte[len - 1];

            String oldBrand = player.clientBrand();

            System.arraycopy(data, 1, shrunk, 0, len - 1);
            String newBrand = new String(shrunk, StandardCharsets.UTF_8)
                    .replace(" (Velocity)", ""); // Thanks velocity

            asuha.eventNode().callCancellable(new PlayerClientBrandEvent(player, oldBrand, newBrand), (event) -> {
                event.player().clientBrand(newBrand);
            });
        }
    }
}
