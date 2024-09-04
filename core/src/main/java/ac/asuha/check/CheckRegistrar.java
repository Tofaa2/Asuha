package ac.asuha.check;

import ac.asuha.Asuha;
import ac.asuha.player.AsuhaPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public interface CheckRegistrar {

    static @NotNull CheckRegistrar defaults(Asuha asuha) {
        return new Basic(asuha, ((anticheat, player)-> {
            return List.of();
        }));
    }

    @NotNull Asuha asuha();

    @NotNull Collection<Check> supplyChecks(AsuhaPlayer player);


    record Basic(
            Asuha asuha, BiFunction<Asuha, AsuhaPlayer, Collection<Check>> checkSupplier
            ) implements CheckRegistrar {


        @Override
        public @NotNull Collection<Check> supplyChecks(AsuhaPlayer player) {
            return checkSupplier.apply(asuha, player);
        }
    }

}
