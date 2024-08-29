package ac.asuha.protocol;

public enum GameMode {

    SURVIVAL((byte) 0, true),
    CREATIVE((byte) 1, false),
    ADVENTURE((byte) 2, true),
    SPECTATOR((byte) 3, false);

    private final byte id;
    private final boolean canTakeDamage;

    GameMode(byte id, boolean canTakeDamage) {
        this.id = id;
        this.canTakeDamage = canTakeDamage;
    }

    public byte id() {
        return id;
    }

    public boolean canTakeDamage() {
        return canTakeDamage;
    }
}
