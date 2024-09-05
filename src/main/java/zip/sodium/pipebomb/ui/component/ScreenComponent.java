package zip.sodium.pipebomb.ui.component;

import net.minecraft.server.dedicated.DedicatedServer;
import org.jetbrains.annotations.NotNull;
import zip.sodium.pipebomb.ui.Screen;

public interface ScreenComponent {
    static void move(final Runnable runnable) {
        DedicatedServer.getServer().execute(runnable);
    }

    default void boot(final @NotNull Screen screen) {}
    default void tick(final @NotNull Screen screen) {}
    default void cleanup(final @NotNull Screen screen) {}

    default boolean shouldRemove(final @NotNull Screen screen) {
        return false;
    }
}
