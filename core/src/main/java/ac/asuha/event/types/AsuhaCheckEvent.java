package ac.asuha.event.types;

import ac.asuha.Asuha;
import ac.asuha.check.Check;
import ac.asuha.player.AsuhaPlayer;

public abstract class AsuhaCheckEvent implements AsuhaPlayerEvent {

    private final Check check;

    public AsuhaCheckEvent(final Check check) {
        this.check = check;
    }

    public Check check() {
        return check;
    }

    @Override
    public AsuhaPlayer player() {
        return check.player();
    }

    public Asuha asuha() {
        return check.asuha();
    }

}
