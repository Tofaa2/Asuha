package ac.asuha.packet.type.common;

import ac.asuha.buffer.NetworkBuffer;
import ac.asuha.packet.Packet;

public class PluginMessagePacket implements Packet.Client, Packet.Server {

    private byte[] data;
    private String identifier;

    public PluginMessagePacket() {}

    public byte[] data() {
        return data;
    }

    public String identifier() {
        return identifier;
    }

    public void data(byte[] data) {
        this.data = data;
    }

    public void identifier(String identifier) {
        this.identifier = identifier;
    }

    public PluginMessagePacket(String identifier, byte[] data) {
        this.identifier = identifier;
        if (identifier.length() >= 255) {
            throw new IllegalArgumentException("identifier length exceeds 255");
        }
        this.data = data;
    }


    @Override
    public void read(NetworkBuffer buffer) {
        identifier = buffer.read(NetworkBuffer.STRING);
        data = buffer.read(NetworkBuffer.RAW_BYTES);
    }

    @Override
    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.STRING, identifier);
        buffer.write(NetworkBuffer.RAW_BYTES, data);
    }
}
