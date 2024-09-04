package ac.asuha.check.badpacket;

import ac.asuha.Asuha;
import ac.asuha.check.Check;
import ac.asuha.check.CheckData;
import ac.asuha.packet.Packet;
import ac.asuha.packet.type.client.play.PositionPacket;
import ac.asuha.player.AsuhaPlayer;

public class YawPitchCheck extends Check {

    public YawPitchCheck(Asuha asuha, AsuhaPlayer player, CheckData data) {
        super(asuha, player, data);
    }

    @Override
    public void check(Packet.Client packet) {
        if (!(packet instanceof PositionPacket)) return;

        float yaw = 0; float pitch = 0;

        if (packet instanceof PositionPacket.SetRotation sr) {
            yaw = sr.yaw();
            pitch = sr.pitch();
        }
        if (packet instanceof PositionPacket.WithRotation wr) {
            yaw = wr.yaw();
            pitch = wr.pitch();
        }

        final boolean invalidPitch = checkForInvalid(pitch, true);
        final boolean invalidYaw = checkForInvalid(yaw, false);

        if (invalidPitch || invalidYaw) triggerViolation("pitch %f yaw %f", pitch, yaw);
    }

    private static boolean checkForInvalid(float rotation, boolean checkPitch) {
        if (Math.abs(rotation) > 90 && checkPitch) return true;
        return !Float.isFinite(rotation);
    }
}
