package ac.asuha.event.player;

import ac.asuha.event.types.AsuhaCancellableEvent;
import ac.asuha.event.types.AsuhaPlayerEvent;
import ac.asuha.player.AsuhaPlayer;

public class PlayerClientBrandEvent implements AsuhaPlayerEvent, AsuhaCancellableEvent {

    private boolean cancelled = false;
    private final AsuhaPlayer player;
    private final String oldBrand;
    private String newBrand;

    public PlayerClientBrandEvent(AsuhaPlayer player, String oldBrand, String newBrand) {
        this.player = player;
        this.oldBrand = oldBrand;
        this.newBrand = newBrand;
    }

    @Override
    public AsuhaPlayer player() {
        return player;
    }

    public String oldBrand() {
        return oldBrand;
    }

    public String newBrand() {
        return newBrand;
    }

    public void newBrand(String brand) {
        newBrand = brand;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
