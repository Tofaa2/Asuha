package ac.asuha.check;

import ac.asuha.Asuha;
import ac.asuha.event.check.CheckPunishEvent;
import ac.asuha.event.check.CheckViolationEvent;
import ac.asuha.event.check.CheckWarnEvent;
import ac.asuha.packet.Packet;
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


    public abstract void check(Packet.Client packet);

    public void triggerWarn(String debug, Object... args) {
        asuha.eventNode().call(new CheckWarnEvent(this, format(debug, args)));
    }

    public void triggerViolation(String debug, Object... args) {
        var event = new CheckViolationEvent(this, format(debug, args));
        asuha.eventNode().call(event);
        if (event.isCancelled()) return;
        violations++;
        if (violations >= data.maxViolations()) {
            triggerPunishment();
        }
    }

    public void triggerPunishment() {
        var event = new CheckPunishEvent(this);
        asuha.eventNode().call(event);
    }

    protected String format(String msg, Object... args) {
        return String.format(msg, args);
    }

    public AsuhaPlayer player() {
        return player;
    }

    public CheckData data() {
        return data;
    }

    public int violations() {
        return violations;
    }

    public Asuha asuha() {
        return asuha;
    }
}
