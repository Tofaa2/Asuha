package ac.asuha.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Single class abuse, system heavily inspired by Minestoms NetworkBuffer and its implementation.
public class PacketBuffer {

    public static PacketBuffer wrap(ByteBuffer buffer, boolean resizable) {
        return new PacketBuffer(buffer, resizable);
    }

    public static PacketBuffer wrap(ByteBuffer buffer) {
        return wrap(buffer, true);
    }

    public static PacketBuffer allocate(int size, boolean resizable) {
        return wrap(ByteBuffer.allocate(size), resizable);
    }

    public static PacketBuffer allocate(int size) {
        return allocate(size, true);
    }

    public static PacketBuffer allocateDirect(int size, boolean resizable) {
        return wrap(ByteBuffer.allocateDirect(size), resizable);
    }

    public static PacketBuffer allocateDirect(int size) {
        return allocateDirect(size, true);
    }

    public static PacketBuffer create(byte[] data, boolean resizable) {
        return wrap(ByteBuffer.wrap(data), resizable);
    }

    public static PacketBuffer create(byte[] data) {
        return create(data, true);
    }


    ByteBuffer buffer;
    int readerIndex;
    int writerIndex;

    boolean resizable;

    PacketBuffer(ByteBuffer buffer, boolean resizeable) {
        this.buffer = buffer;
        this.resizable = resizeable;
    }


    public int size() {
        return buffer.capacity();
    }

    public byte[] getRawBytes() {
        final int limit = buffer.limit();
        final int length = limit - readerIndex;
        assert length > 0 : "Invalid remaining:" + length;
        final byte[] bytes = new byte[length];
        buffer.get(readerIndex, bytes);
        readerIndex += length;
        return bytes;
    }

    public void putRawBytes(byte[] data) {
        ensureSize(data.length);
        buffer.put(writerIndex, data);
        writerIndex += data.length;
    }

    public int readerIndex() {
        return readerIndex;
    }

    public int writerIndex() {
        return writerIndex;
    }

    public <T> T read(Type<T> type) {
        return type.read(this);
    }

    public <T> void write(Type<T> type, T value) {
        type.write(this, value);
    }

    public <T extends Enum<T>> T readEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[read(Type.VAR_INT)];
    }

    public <T extends Enum<T>> void writeEnum(T value) {
        write(Type.VAR_INT, value.ordinal());
    }

    public <T> List<T> readCollection(Type<T> type, int max) {
        int size = read(Type.VAR_INT);
        if (size > max) {
            throw new IllegalStateException("Collection size " + size + " is larger than the specified max of " + max);
        }
        var list = new ArrayList<T>(size);
        for (int i = 0 ; i < size; i++) {
            list.add(read(type));
        }
        return list;
    }

    public <T> void writeCollection(Type<T> type, Collection<T> collection) {
        if (collection == null) {
            write(Type.BYTE, (byte) 0);
            return;
        }
        write(Type.VAR_INT, collection.size());
        for (var value : collection) {
            write(type, value);
        }
    }

    public void ensureSize(int length) {
        if (!resizable) return; // No insurance
        if (buffer.capacity() < writerIndex + length) {
            final int newCapacity = Math.max(buffer.capacity() * 2, writerIndex + length);
            ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity);
            buffer.position(0);
            newBuffer.put(buffer);
            buffer = newBuffer.clear();
        }
    }

    public interface Type<T> {

        int USHORT_MASK = 0xFFFF;
        int SEGMENT_BITS = 0x7F;
        int CONTINUE_BIT = 0x80;

        Type<Boolean> BOOL = new Type<>() {
            @Override
            public Boolean read(PacketBuffer buffer) {
                var value = buffer.buffer.get(buffer.readerIndex);
                buffer.readerIndex += 1;
                return value == 1;
            }

            @Override
            public void write(PacketBuffer buffer, Boolean value) {
                buffer.ensureSize(1);
                buffer.buffer.put(buffer.writerIndex, value ? (byte) 1 : (byte) 0);
                buffer.writerIndex += 1;
            }
        };
        Type<Byte> BYTE = new Type<>() {
            @Override
            public Byte read(PacketBuffer buffer) {
                var value = buffer.buffer.get(buffer.readerIndex);
                buffer.readerIndex += 1;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Byte value) {
                buffer.ensureSize(1);
                buffer.buffer.put(buffer.writerIndex, value);
                buffer.writerIndex += 1;
            }
        };
        Type<Integer> INT = new Type<>() {
            @Override
            public Integer read(PacketBuffer buffer) {
                var value = buffer.buffer.getInt(buffer.readerIndex);
                buffer.readerIndex += 4;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Integer value) {
                buffer.ensureSize(4);
                buffer.buffer.putInt(buffer.writerIndex);
                buffer.writerIndex += 4;
            }
        };
        Type<Short> SHORT = new Type<>() {
            @Override
            public Short read(PacketBuffer buffer) {
                var value = buffer.buffer.getShort(buffer.readerIndex);
                buffer.readerIndex += 2;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Short value) {
                buffer.ensureSize(2);
                buffer.buffer.putShort(buffer.writerIndex, value);
                buffer.writerIndex += 2;
            }
        };
        Type<Integer> USHORT = new Type<>() {
            @Override
            public Integer read(PacketBuffer buffer) {
                var value = buffer.buffer.getShort(buffer.readerIndex);
                buffer.readerIndex += 2;
                return value & USHORT_MASK;
            }

            @Override
            public void write(PacketBuffer buffer, Integer value) {
                buffer.ensureSize(2);
                buffer.buffer.putShort(buffer.writerIndex, (short) (value & USHORT_MASK));
                buffer.writerIndex += 2;
            }
        };
        Type<Long> LONG = new Type<>() {
            @Override
            public Long read(PacketBuffer buffer) {
                var value = buffer.buffer.getLong(buffer.readerIndex);
                buffer.readerIndex += 8;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Long value) {
                buffer.ensureSize(8);
                buffer.buffer.putLong(buffer.writerIndex);
                buffer.writerIndex+=8;
            }
        };
        Type<Float> FLOAT = new Type<>() {
            @Override
            public Float read(PacketBuffer buffer) {
                var value = buffer.buffer.getFloat(buffer.readerIndex);
                buffer.readerIndex += 4;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Float value) {
                buffer.ensureSize(4);
                buffer.buffer.putFloat(buffer.writerIndex, value);
                buffer.writerIndex+=4;
            }
        };
        Type<Double> DOUBLE = new Type<>() {
            @Override
            public Double read(PacketBuffer buffer) {
                var value = buffer.buffer.getDouble(buffer.readerIndex);
                buffer.readerIndex += 8;
                return value;
            }

            @Override
            public void write(PacketBuffer buffer, Double value) {
                buffer.ensureSize(8);
                buffer.buffer.putDouble(buffer.writerIndex, value);
                buffer.writerIndex+=8;
            }
        };
        Type<Integer> VAR_INT = new Type<>() {
            @Override
            public Integer read(PacketBuffer buffer) {
                var index = buffer.readerIndex;
                var result = 0;
                for (int i = 0; ; i +=7) {
                    var b = buffer.buffer.get(index++);
                    result |= (b & SEGMENT_BITS) << i;
                    if (b >= 0) {
                        buffer.readerIndex += index - buffer.readerIndex;
                        return result;
                    }
                }
            }

            @Override
            public void write(PacketBuffer buffer, Integer value) {
                int i = value; // actually so dumb
                while (true) {
                    if ((value & ~SEGMENT_BITS) == 0) {
                        buffer.buffer.put(buffer.writerIndex, (byte)i);
                        buffer.writerIndex +=1;
                        return;
                    }
                    buffer.buffer.put(buffer.writerIndex, (byte)  ((value & SEGMENT_BITS) | CONTINUE_BIT));
                    buffer.writerIndex +=1;
                    i >>>= 7;
                }
            }
        };
        Type<String> STRING = new Type<>() {
            @Override
            public String read(PacketBuffer buffer) {
                var length = buffer.read(VAR_INT);
                var remaining = buffer.buffer.limit() -  buffer.readerIndex;
                assert length > remaining : "String is too long (length:" + length + " readable: " + remaining;
                var bytes = new byte[length];
                buffer.buffer.get(buffer.readerIndex, bytes);
                buffer.readerIndex += length;
                return new String(bytes, StandardCharsets.UTF_8);
            }

            @Override
            public void write(PacketBuffer buffer, String value) {
                var bytes = value.getBytes(StandardCharsets.UTF_8);
                buffer.write(VAR_INT, bytes.length);
                buffer.putRawBytes(bytes);
            }
        };

        T read(PacketBuffer buffer);

        void write(PacketBuffer buffer, T value);

    }

}
