package ac.asuha.event.check;

import ac.asuha.check.Check;
import ac.asuha.event.types.AsuhaCheckEvent;

public class CheckPunishEvent extends AsuhaCheckEvent {


    public CheckPunishEvent(Check check) {
        super(check);
    }

}
