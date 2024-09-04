package zip.sodium.pipebomb.object;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import zip.sodium.pipebomb.ui.Screen;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerScreenHolder {
    private PlayerScreenHolder() {
        throw new UnsupportedOperationException();
    }

    private static final Set<Screen> PLAYER_SCREENS = ConcurrentHashMap.newKeySet() ;

    public static CompletableFuture<Void> tick() {
        return CompletableFuture.allOf(
                PLAYER_SCREENS.parallelStream()
                        .map(Screen::tick)
                        .toArray(CompletableFuture[]::new)
        ).thenRunAsync(PlayerScreenHolder::tryCleanup);
    }

    private static void tryCleanup() {
        final Iterator<Screen> each = PLAYER_SCREENS.iterator();
        while (each.hasNext()) {
            final var next = each.next();
            if (next.removeIf()) {
                next.cleanup();
                each.remove();
            }
        }
    }

    public static void add(final Screen entity) {
        PLAYER_SCREENS.add(entity);
    }

    public static CompletableFuture<@Nullable Screen> search(final Player player) {
        return CompletableFuture.supplyAsync(() ->
                PLAYER_SCREENS.parallelStream()
                        .filter(entity -> entity.player() == player)
                        .findFirst()
                        .orElse(null)
        );
    }

    @ApiStatus.Internal
    public static void cleanup() {
        PLAYER_SCREENS.forEach(Screen::cleanup);
    }
}
