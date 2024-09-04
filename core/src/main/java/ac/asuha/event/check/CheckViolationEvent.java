package ac.asuha.event.check;

import ac.asuha.check.Check;
import ac.asuha.event.types.AsuhaCancellableEvent;

public class CheckViolationEvent extends MessagedCheckEvent implements AsuhaCancellableEvent {

    private boolean cancelled = false;

    public CheckViolationEvent(Check check, String message) {
        super(check, message);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
