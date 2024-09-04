package ac.asuha.processor;

import ac.asuha.Asuha;
import ac.asuha.event.processor.ProcessorViolationEvent;
import ac.asuha.packet.Packet;
import ac.asuha.player.AsuhaPlayer;

// impls should be records
public interface PlayerProcessor {

    void process(Packet.Client packet);

    AsuhaPlayer player();

    Asuha asuha();

    default void triggerViolation(String debug, Object... args) {
        asuha().eventNode().call(new ProcessorViolationEvent(this, String.format(debug, args)));
    }

}
