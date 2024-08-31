package ac.asuha.nbt;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public interface NbtComponentSerializer extends ComponentSerializer<Component, Component, BinaryTag> {
    static @NotNull NbtComponentSerializer nbt() {
        return NbtComponentSerializerImpl.INSTANCE;
    }

    @NotNull Style deserializeStyle(@NotNull BinaryTag var1);

    @NotNull CompoundBinaryTag serializeStyle(@NotNull Style var1);
}
