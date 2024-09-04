package ac.asuha.event.types;

public interface  AsuhaCancellableEvent extends AsuhaEvent{

    boolean isCancelled();

    void setCancelled(boolean cancel);

}
