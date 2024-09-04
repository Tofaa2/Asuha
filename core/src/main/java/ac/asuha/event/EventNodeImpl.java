package ac.asuha.event;

import ac.asuha.event.types.AsuhaCancellableEvent;
import ac.asuha.event.types.AsuhaEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class EventNodeImpl<T extends AsuhaEvent> implements AsuhaEventNode<T> {

    private int priority;
    private String name;
    private EventNodeImpl<T> parent;

    private final List<AsuhaEventNode<T>> children = new ArrayList<>();
    private final Predicate<T> filter;
    private final Map<Class<? extends T>, Set<Consumer<? extends T>>> handlers = new HashMap<>();

    EventNodeImpl(int priority, String name, Predicate<T> filter) {
        this.priority = priority;
        this.name = name;
        this.filter = filter;
    }

    EventNodeImpl(int priority, String name) {
        this(priority, name, null);
    }


    @Override
    public int priority() {
        return priority;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public <E extends T> void addListener(Class<E> eventClass, Consumer<E> listener) {
        if (!handlers.containsKey(eventClass)) {
            handlers.put(eventClass, new HashSet<>());
        }
        handlers.get(eventClass).add(listener);
    }

    @Override
    public <E extends T> void removeListener(Class<E> eventClass, Consumer<E> listener) {
        if (handlers.containsKey(eventClass)) {
            handlers.get(eventClass).remove(listener);
        }
    }

    @Override
    public <E extends T> void call(E event) {
        if (handlers.containsKey(event.getClass())) {
            var consumers = handlers.get(event.getClass());
            for (var consumer : consumers) {
                if (filter.test(event)) {
                    ((Consumer<T>) consumer).accept(event);
                }
            }
        }
        children.forEach(child -> child.call(event));
    }

    @Override
    public <E extends T> void callCancellable(E event, @Nullable Consumer<E> ifNotCancelled) {
        call(event);
        if (ifNotCancelled == null)  return;
        if (event instanceof AsuhaCancellableEvent cancellable && !cancellable.isCancelled()) {
            ifNotCancelled.accept(event);
        }
    }

    @Override
    public @Nullable AsuhaEventNode<T> parent() {
        return parent;
    }

    @Override
    public @Unmodifiable @NotNull Collection<AsuhaEventNode<T>> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addChild(@NotNull AsuhaEventNode<T> child) {
        children.add(child);
        ((EventNodeImpl) child).parent = this;
    }

    @Override
    public void removeChild(@NotNull AsuhaEventNode<T> child) {
        children.remove(child);
        ((EventNodeImpl) child).parent = null;
    }
}
