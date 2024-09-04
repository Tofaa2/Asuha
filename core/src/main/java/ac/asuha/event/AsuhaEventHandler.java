package ac.asuha.event;

import ac.asuha.event.types.AsuhaEvent;
import org.jetbrains.annotations.NotNull;

public interface AsuhaEventHandler {

    @NotNull AsuhaEventNode<AsuhaEvent> eventNode();

}
