package ac.asuha.event.types;

import ac.asuha.processor.PlayerProcessor;

public abstract class AsuhaProcessorEvent implements AsuhaEvent {

    private final PlayerProcessor processor;


    public AsuhaProcessorEvent(PlayerProcessor processor) {
        this.processor = processor;
    }

    public PlayerProcessor processor() {
        return processor;
    }
}
