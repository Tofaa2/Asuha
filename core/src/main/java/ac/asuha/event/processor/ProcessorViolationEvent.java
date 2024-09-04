package ac.asuha.event.processor;

import ac.asuha.event.types.AsuhaProcessorEvent;
import ac.asuha.processor.PlayerProcessor;

public class ProcessorViolationEvent extends AsuhaProcessorEvent {

    private final String debug;

    public ProcessorViolationEvent(PlayerProcessor processor, String debug) {
        super(processor);
        this.debug = debug;
    }

    public String debug() {
        return debug;
    }
}
