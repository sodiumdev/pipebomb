package zip.sodium.pipebomb.object;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import zip.sodium.pipebomb.ui.Screen;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class PlayerScreenHolder {
    private PlayerScreenHolder() {
        throw new UnsupportedOperationException();
    }

    private static final Set<Screen> PLAYER_SCREENS = ConcurrentHashMap.newKeySet();

    public static CompletableFuture<Void> tick() {
        return CompletableFuture.allOf(
                PLAYER_SCREENS.stream()
                        .parallel()
                        .map(Screen::tick)
                        .toArray(CompletableFuture[]::new)
        ).thenRunAsync(PlayerScreenHolder::tryCleanup);
    }

    private static void tryCleanup() {
        final Iterator<Screen> each = PLAYER_SCREENS.iterator();
        while (each.hasNext()) {
            final var next = each.next();
            if (next.shouldRemove()) {
                next.cleanup();
                each.remove();
            }
        }
    }

    public static void add(final Screen screen) {
        PLAYER_SCREENS.add(screen);
    }

    public static CompletableFuture<@Nullable Screen> search(final Player player) {
        return CompletableFuture.supplyAsync(() ->
                PLAYER_SCREENS.stream()
                        .parallel()
                        .filter(screen -> screen.player() == player)
                        .findFirst()
                        .orElse(null)
        );
    }

    public static void cleanup() {
        PLAYER_SCREENS.forEach(Screen::cleanup);
        PLAYER_SCREENS.clear();
    }
}
