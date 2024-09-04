package ac.asuha.event.check;

import ac.asuha.check.Check;

public class CheckWarnEvent extends MessagedCheckEvent {

    public CheckWarnEvent(Check check, String message) {
        super(check, message);
    }
}
