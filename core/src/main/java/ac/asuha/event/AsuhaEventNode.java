package ac.asuha.event;

import ac.asuha.event.types.AsuhaEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface AsuhaEventNode<T extends AsuhaEvent> {

    static <E extends AsuhaEvent> AsuhaEventNode<E> create(String name, int priority, Predicate<E> filter) {
        return new EventNodeImpl<>(priority,name, filter);
    }

    static <E extends AsuhaEvent> AsuhaEventNode<E> create(String name, int priority) {
        return new EventNodeImpl<>(priority,name);
    }

    static <E extends AsuhaEvent> AsuhaEventNode<E> create(String name, Predicate<E> filter) {
        return new EventNodeImpl<>(0, name, filter);
    }

    static <E extends AsuhaEvent> AsuhaEventNode<E> create(String name) {
        return new EventNodeImpl<>(0, name);
    }

    int priority();

    @NotNull String name();

    <E extends T> void addListener(Class<E> eventClass, Consumer<E> listener);

    <E extends T> void removeListener(Class<E> eventClass, Consumer<E> listener);

    <E extends T> void call(E event);

    <E extends T> void callCancellable(E event, @Nullable Consumer<E> ifNotCancelled);

    @Nullable AsuhaEventNode<T> parent();

    @Unmodifiable @NotNull Collection<AsuhaEventNode<T>> children();

    void addChild(@NotNull AsuhaEventNode<T> child);

    void removeChild(@NotNull AsuhaEventNode<T> child);




}
