package ac.asuha.event.check;

import ac.asuha.check.Check;
import ac.asuha.event.types.AsuhaCheckEvent;

class MessagedCheckEvent extends AsuhaCheckEvent {

    private final String message;

    public MessagedCheckEvent(Check check, String message) {
        super(check);
        this.message = message;
    }

    public String message() {
        return message;
    }

}
