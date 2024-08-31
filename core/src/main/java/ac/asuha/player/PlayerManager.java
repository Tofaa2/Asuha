package ac.asuha.player;

import ac.asuha.Asuha;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final Asuha asuha;
    private final Map<UUID, AsuhaPlayer> players;

    public PlayerManager(Asuha asuha) {
        this.asuha = asuha;
        this.players = new ConcurrentHashMap<>();
    }

    public Asuha asuha() {
        return asuha;
    }
}
