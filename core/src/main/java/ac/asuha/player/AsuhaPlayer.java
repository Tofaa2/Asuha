package ac.asuha.player;

import ac.asuha.check.Check;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AsuhaPlayer {

    private final UUID uuid;
    private final int entityId;
    private final Set<Check> checks;
    private String clientBrand = "unknown";

    public AsuhaPlayer(UUID uuid, int entityId) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.checks = ConcurrentHashMap.newKeySet();
    }

    public UUID uuid() {
        return uuid;
    }

    public int entityId() {
        return entityId;
    }

    public Collection<Check> checks() {
        return Collections.unmodifiableCollection(checks);
    }

    public boolean hasCheck(Check check) {
        return checks.contains(check);
    }

    public void addCheck(Check check) {
        checks.add(check);
    }

    public void removeCheck(Check check) {
        checks.remove(check);
    }

    public void addChecks(Collection<Check> checks) {
        this.checks.addAll(checks);
    }

    public String clientBrand() {
        return clientBrand;
    }

    public void clientBrand(String clientBrand) {
        this.clientBrand = clientBrand;
    }
}
