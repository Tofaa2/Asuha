package ac.asuha.minestom.test;

import ac.asuha.AsuhaConfig;
import ac.asuha.minestom.MinestomAsuha;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public final class Minestom {

    public static void main(String[] args) {
        var minestom = MinecraftServer.init();

        var config = new AsuhaConfig();
        var anticheat = new MinestomAsuha(config, MinecraftServer.getGlobalEventHandler());


        InstanceContainer world = MinecraftServer.getInstanceManager().createInstanceContainer();
        world.setChunkSupplier(LightingChunk::new);
        world.setGenerator((unit) -> {
            unit.modifier().fillHeight(0, 64, Block.STONE);
        });

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(world);
            event.getPlayer().setRespawnPoint(new Pos(0, 65, 0));
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event ->{
            ItemStack sample = ItemStack.of(Material.BIRCH_CHEST_BOAT);
            var inv = event.getPlayer().getInventory();
            for (int i = 0; i < 9; i++) {
                inv.setItemStack(i, sample);
            }
        });

        minestom.start("localhost", 25565);
    }

}
