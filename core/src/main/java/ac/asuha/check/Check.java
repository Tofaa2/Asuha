package ac.asuha.check;

import ac.asuha.Asuha;
import ac.asuha.player.AsuhaPlayer;

public abstract class Check {

    protected final AsuhaPlayer player;
    protected final Asuha asuha;
    protected final CheckData data;
    private int violations;

    protected Check(Asuha asuha, AsuhaPlayer player, CheckData data) {
        this.asuha = asuha;
        this.player = player;
        this.data = data;
    }

    public void triggerViolation(String debug, Object... args) {
        violations++;
        if (violations >= data.maxViolations()) {
            triggerPunishment();
        }
    }

}
