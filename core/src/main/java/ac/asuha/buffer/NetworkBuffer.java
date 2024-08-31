package ac.asuha.buffer;

import ac.asuha.coordinate.Point;
import ac.asuha.coordinate.Pos;
import ac.asuha.protocol.Hand;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;

public sealed interface NetworkBuffer permits NetworkBufferImpl {
    Type<Boolean> BOOLEAN = new NetworkBufferTypeImpl.BooleanType();
    Type<Byte> BYTE = new NetworkBufferTypeImpl.ByteType();
    Type<Short> SHORT = new NetworkBufferTypeImpl.ShortType();
    Type<Integer> UNSIGNED_SHORT = new NetworkBufferTypeImpl.UnsignedShortType();
    Type<Integer> INT = new NetworkBufferTypeImpl.IntType();
    Type<Long> LONG = new NetworkBufferTypeImpl.LongType();
    Type<Float> FLOAT = new NetworkBufferTypeImpl.FloatType();
    Type<Double> DOUBLE = new NetworkBufferTypeImpl.DoubleType();
    Type<Integer> VAR_INT = new NetworkBufferTypeImpl.VarIntType();
    Type<Integer> VAR_INT_3 = new NetworkBufferTypeImpl.VarInt3Type();
    Type<Long> VAR_LONG = new NetworkBufferTypeImpl.VarLongType();
    Type<byte[]> RAW_BYTES = new NetworkBufferTypeImpl.RawBytesType(-1);
    Type<String> STRING = new NetworkBufferTypeImpl.StringType();
    Type<String> STRING_TERMINATED = new NetworkBufferTypeImpl.StringTerminatedType();
    Type<BinaryTag> NBT = new NetworkBufferTypeImpl.NbtType();
    @SuppressWarnings({"unchecked", "rawtypes"})
    Type<CompoundBinaryTag> NBT_COMPOUND = (Type) new NetworkBufferTypeImpl.NbtType();
    Type<Point> BLOCK_POSITION = new NetworkBufferTypeImpl.BlockPositionType();
    Type<Component> JSON_COMPONENT = new NetworkBufferTypeImpl.JsonComponentType();
    Type<UUID> UUID = new NetworkBufferTypeImpl.UUIDType();
    Type<Pos> POS = new NetworkBufferTypeImpl.PosType();

    Type<Component> COMPONENT = new ComponentNetworkBufferTypeImpl();

    Type<Hand> HAND = Enum(Hand.class);

    Type<byte[]> BYTE_ARRAY = new NetworkBufferTypeImpl.ByteArrayType();
    Type<long[]> LONG_ARRAY = new NetworkBufferTypeImpl.LongArrayType();
    Type<int[]> VAR_INT_ARRAY = new NetworkBufferTypeImpl.VarIntArrayType();
    Type<long[]> VAR_LONG_ARRAY = new NetworkBufferTypeImpl.VarLongArrayType();

    Type<BitSet> BITSET = LONG_ARRAY.transform(BitSet::valueOf, BitSet::toLongArray);
    Type<Instant> INSTANT_MS = LONG.transform(Instant::ofEpochMilli, Instant::toEpochMilli);
    // METADATA
    Type<int[]> VILLAGER_DATA = new NetworkBufferTypeImpl.VillagerDataType();
    Type<Point> VECTOR3 = new NetworkBufferTypeImpl.Vector3Type();
    Type<Point> VECTOR3D = new NetworkBufferTypeImpl.Vector3DType();
    Type<Point> VECTOR3B = new NetworkBufferTypeImpl.Vector3BType();
    Type<float[]> QUATERNION = new NetworkBufferTypeImpl.QuaternionType();
    Type<@Nullable Point> OPT_BLOCK_POSITION = BLOCK_POSITION.optional();
    Type<@Nullable UUID> OPT_UUID = UUID.optional();

    // Combinators

    static <E extends Enum<E>> @NotNull Type<E> Enum(@NotNull Class<E> enumClass) {
        final E[] values = enumClass.getEnumConstants();
        return VAR_INT.transform(integer -> values[integer], Enum::ordinal);
    }

    static <E extends Enum<E>> @NotNull Type<EnumSet<E>> EnumSet(@NotNull Class<E> enumClass) {
        return new NetworkBufferTypeImpl.EnumSetType<>(enumClass, enumClass.getEnumConstants());
    }

    static @NotNull Type<BitSet> FixedBitSet(int length) {
        return new NetworkBufferTypeImpl.FixedBitSetType(length);
    }

    static @NotNull Type<byte[]> FixedRawBytes(int length) {
        return new NetworkBufferTypeImpl.RawBytesType(length);
    }

    static <T> @NotNull Type<T> Lazy(@NotNull Supplier<@NotNull Type<T>> supplier) {
        return new NetworkBufferTypeImpl.LazyType<>(supplier);
    }

    <T> void write(@NotNull Type<T> type, @UnknownNullability T value) throws IndexOutOfBoundsException;

    <T> @UnknownNullability T read(@NotNull Type<T> type) throws IndexOutOfBoundsException;

    <T> void writeAt(long index, @NotNull Type<T> type, @UnknownNullability T value) throws IndexOutOfBoundsException;

    <T> @UnknownNullability T readAt(long index, @NotNull Type<T> type) throws IndexOutOfBoundsException;

    void copyTo(long srcOffset, byte @NotNull [] dest, long destOffset, long length);

    byte @NotNull [] extractBytes(@NotNull Consumer<@NotNull NetworkBuffer> extractor);

    @NotNull NetworkBuffer clear();

    int writeIndex();

    long readIndex();

    @NotNull NetworkBuffer writeIndex(long writeIndex);

    @NotNull NetworkBuffer readIndex(long readIndex);

    @NotNull NetworkBuffer index(long readIndex, long writeIndex);

    long advanceWrite(long length);

    long advanceRead(long length);

    long readableBytes();

    long writableBytes();

    long capacity();

    void readOnly();

    boolean isReadOnly();

    void resize(long newSize);

    void ensureWritable(long length);

    void compact();

    NetworkBuffer slice(long index, long length, long readIndex, long writeIndex);

    NetworkBuffer copy(long index, long length, long readIndex, long writeIndex);

    default NetworkBuffer copy(long index, long length) {
        return copy(index, length, readIndex(), writeIndex());
    }

    int readChannel(ReadableByteChannel channel) throws IOException;

    boolean writeChannel(SocketChannel channel) throws IOException;

    void cipher(Cipher cipher, long start, long length);

    long compress(long start, long length, NetworkBuffer output);

    long decompress(long start, long length, NetworkBuffer output) throws DataFormatException;


    interface Type<T> {
        void write(@NotNull NetworkBuffer buffer, T value);

        T read(@NotNull NetworkBuffer buffer);

        default <S> @NotNull Type<S> transform(@NotNull Function<T, S> to, @NotNull Function<S, T> from) {
            return new NetworkBufferTypeImpl.TransformType<>(this, to, from);
        }

        default <V> @NotNull Type<Map<T, V>> mapValue(@NotNull Type<V> valueType, int maxSize) {
            return new NetworkBufferTypeImpl.MapType<>(this, valueType, maxSize);
        }

        default <V> @NotNull Type<Map<T, V>> mapValue(@NotNull Type<V> valueType) {
            return mapValue(valueType, Integer.MAX_VALUE);
        }

        default @NotNull Type<List<T>> list(int maxSize) {
            return new NetworkBufferTypeImpl.ListType<>(this, maxSize);
        }

        default @NotNull Type<List<T>> list() {
            return list(Integer.MAX_VALUE);
        }

        default @NotNull Type<T> optional() {
            return new NetworkBufferTypeImpl.OptionalType<>(this);
        }
    }

    static @NotNull Builder builder(long size) {
        return new NetworkBufferImpl.Builder(size);
    }


    static @NotNull NetworkBuffer staticBuffer(long size) {
        return builder(size).build();
    }


    static @NotNull NetworkBuffer resizableBuffer(int initialSize) {
        return builder(initialSize)
                .autoResize(AutoResize.DOUBLE)
                .build();    }

    static @NotNull NetworkBuffer resizableBuffer() {
        return resizableBuffer(256);
    }

    static @NotNull NetworkBuffer wrap(byte @NotNull [] bytes, int readIndex, int writeIndex) {
        return NetworkBufferImpl.wrap(bytes, readIndex, writeIndex);
    }


    sealed interface Builder permits NetworkBufferImpl.Builder {
        @NotNull Builder autoResize(@Nullable AutoResize autoResize);

        @NotNull NetworkBuffer build();
    }

    @FunctionalInterface
    interface AutoResize {
        AutoResize DOUBLE = (capacity, targetSize) -> Math.max(capacity * 2, targetSize);

        long resize(long capacity, long targetSize);
    }

    static byte[] makeArray(@NotNull Consumer<@NotNull NetworkBuffer> writing) {
        NetworkBuffer buffer = resizableBuffer(256);
        writing.accept(buffer);
        return buffer.read(RAW_BYTES);
    }



    static <T> byte[] makeArray(@NotNull Type<T> type, @NotNull T value) {
        return makeArray(buffer -> buffer.write(type, value));
    }

    static void copy(NetworkBuffer srcBuffer, long srcOffset,
                     NetworkBuffer dstBuffer, long dstOffset, long length) {
        NetworkBufferImpl.copy(srcBuffer, srcOffset, dstBuffer, dstOffset, length);
    }

    static boolean equals(NetworkBuffer buffer1, NetworkBuffer buffer2) {
        return NetworkBufferImpl.equals(buffer1, buffer2);
    }
}