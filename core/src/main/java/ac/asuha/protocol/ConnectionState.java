package ac.asuha.protocol;

public enum ConnectionState {

    HANDSHAKE, // Essentially no state,
    STATUS, // also unused,
    LOGIN,
    CONFIGURATION,
    PLAY // where every check must be processed
    ;

    public boolean isAllowedChecking() {
        return this == PLAY;
    }


}
