package ac.asuha.nbt;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import ac.asuha.utils.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class BinaryTagUtil {
    private static final BinaryTagType<?>[] TYPES;

    public static @NotNull BinaryTagType<?> nbtTypeFromId(byte id) {
        Check.argCondition(id < 0 || id >= TYPES.length, "Invalid NBT type id: " + id);
        return TYPES[id];
    }

    public static @NotNull Object nbtValueFromTag(@NotNull BinaryTag tag) {
        if (tag instanceof ByteBinaryTag byteTag) {
            return byteTag.value();
        } else if (tag instanceof ShortBinaryTag shortTag) {
            return shortTag.value();
        } else if (tag instanceof IntBinaryTag intTag) {
            return intTag.value();
        } else if (tag instanceof LongBinaryTag longTag) {
            return longTag.value();
        } else if (tag instanceof FloatBinaryTag floatTag) {
            return floatTag.value();
        } else if (tag instanceof DoubleBinaryTag doubleTag) {
            return doubleTag.value();
        } else if (tag instanceof ByteArrayBinaryTag byteArrayTag) {
            return byteArrayTag.value();
        } else if (tag instanceof StringBinaryTag stringTag) {
            return stringTag.value();
        } else if (tag instanceof IntArrayBinaryTag intArrayTag) {
            return intArrayTag.value();
        } else if (tag instanceof LongArrayBinaryTag longArrayTag) {
            return longArrayTag.value();
        } else {
            throw new UnsupportedOperationException("Unsupported NBT type: " + String.valueOf(tag.getClass()));
        }
    }

    private BinaryTagUtil() {
    }

    static {
        TYPES = new BinaryTagType[]{BinaryTagTypes.END, BinaryTagTypes.BYTE, BinaryTagTypes.SHORT, BinaryTagTypes.INT, BinaryTagTypes.LONG, BinaryTagTypes.FLOAT, BinaryTagTypes.DOUBLE, BinaryTagTypes.BYTE_ARRAY, BinaryTagTypes.STRING, BinaryTagTypes.LIST, BinaryTagTypes.COMPOUND, BinaryTagTypes.INT_ARRAY, BinaryTagTypes.LONG_ARRAY};
    }
}

