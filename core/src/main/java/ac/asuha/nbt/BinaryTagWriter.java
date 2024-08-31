package ac.asuha.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import org.jetbrains.annotations.NotNull;

public class BinaryTagWriter {
    private final DataOutput output;

    public BinaryTagWriter(@NotNull DataOutput output) {
        this.output = output;
    }

    public void writeNameless(@NotNull BinaryTag tag) throws IOException {
        //noinspection unchecked
        BinaryTagType<BinaryTag> type = (BinaryTagType<BinaryTag>) tag.type();
        output.writeByte(type.id());
        type.write(tag, output);
    }

    public void readNamed(@NotNull String name, @NotNull BinaryTag tag) throws IOException {
        //noinspection unchecked
        BinaryTagType<BinaryTag> type = (BinaryTagType<BinaryTag>) tag.type();
        output.writeByte(type.id());
        output.writeUTF(name);
        type.write(tag, output);
    }
    static {
        BinaryTagTypes.COMPOUND.id();
    }
}
