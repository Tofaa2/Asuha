package ac.asuha.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import org.jetbrains.annotations.NotNull;

public class BinaryTagReader {
    private final DataInput input;

    public BinaryTagReader(@NotNull DataInput input) {
        this.input = input;
    }

    public @NotNull BinaryTag readNameless() throws IOException {
        BinaryTagType<? extends BinaryTag> type = BinaryTagUtil.nbtTypeFromId(this.input.readByte());
        return type.read(this.input);
    }

    @NotNull
    public Map.@NotNull Entry<String, BinaryTag> readNamed() throws IOException {
        BinaryTagType<? extends BinaryTag> type = BinaryTagUtil.nbtTypeFromId(this.input.readByte());
        String name = this.input.readUTF();
        return Map.entry(name, type.read(this.input));
    }

    static {
        BinaryTagTypes.COMPOUND.id();
    }
}
