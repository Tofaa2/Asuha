package ac.asuha.packet.type.client.play;

import ac.asuha.buffer.NetworkBuffer;
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
        public void read(NetworkBuffer buffer) {
            yaw = buffer.read(NetworkBuffer.FLOAT);
            pitch = buffer.read(NetworkBuffer.FLOAT);
        }
    }

    public static final class SetOnGround extends PositionPacket {
        @Override
        public void read(NetworkBuffer buffer) {
            this.onGround = buffer.read(NetworkBuffer.BOOLEAN);
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
        public void read(NetworkBuffer buffer) {
            double x = buffer.read(NetworkBuffer.DOUBLE);
            double y = buffer.read(NetworkBuffer.DOUBLE);
            double z = buffer.read(NetworkBuffer.DOUBLE);
            yaw = buffer.read(NetworkBuffer.FLOAT);
            pitch = buffer.read(NetworkBuffer.FLOAT);
            onGround = buffer.read(NetworkBuffer.BOOLEAN);
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
    public void read(NetworkBuffer buffer) {
        double x = buffer.read(NetworkBuffer.DOUBLE);
        double y = buffer.read(NetworkBuffer.DOUBLE);
        double z = buffer.read(NetworkBuffer.DOUBLE);
        onGround = buffer.read(NetworkBuffer.BOOLEAN);
        point = new Point(x, y, z);
    }
}
