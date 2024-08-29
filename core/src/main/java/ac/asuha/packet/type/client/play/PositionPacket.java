package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.PacketBuffer;
import ac.asuha.coordinate.Point;
import ac.asuha.packet.Packet;
import org.jetbrains.annotations.Nullable;

public class PositionPacket implements Packet.Client {

    public static final class SetRotation extends PositionPacket {

        private float yaw, pitch;

        public float yaw() {
            return yaw;
        }

        public float pitch() {
            return pitch;
        }

        @Override
        public void read(PacketBuffer buffer) {
            yaw = buffer.read(PacketBuffer.Type.FLOAT);
            pitch = buffer.read(PacketBuffer.Type.FLOAT);
        }
    }

    public static final class SetOnGround extends PositionPacket {
        @Override
        public void read(PacketBuffer buffer) {
            this.onGround = buffer.read(PacketBuffer.Type.BOOL);
        }
    }

    public static final class WithRotation extends PositionPacket {
        private float yaw, pitch;

        public float yaw() {
            return yaw;
        }
        public float pitch() {
            return pitch;
        }

        @Override
        public void read(PacketBuffer buffer) {
            double x = buffer.read(PacketBuffer.Type.DOUBLE);
            double y = buffer.read(PacketBuffer.Type.DOUBLE);
            double z = buffer.read(PacketBuffer.Type.DOUBLE);
            yaw = buffer.read(PacketBuffer.Type.FLOAT);
            pitch = buffer.read(PacketBuffer.Type.FLOAT);
            onGround = buffer.read(PacketBuffer.Type.BOOL);
        }
    }

    protected Point point;
    protected boolean onGround;

    public @Nullable Point point() {
        return point;
    }

    public boolean onGround() {
        return onGround;
    }


    @Override
    public void read(PacketBuffer buffer) {
        double x = buffer.read(PacketBuffer.Type.DOUBLE);
        double y = buffer.read(PacketBuffer.Type.DOUBLE);
        double z = buffer.read(PacketBuffer.Type.DOUBLE);
        onGround = buffer.read(PacketBuffer.Type.BOOL);
        point = new Point(x, y, z);
    }
}
