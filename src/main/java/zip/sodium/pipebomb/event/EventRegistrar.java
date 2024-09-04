package zip.sodium.pipebomb.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import zip.sodium.pipebomb.event.builtin.PlayerEvents;
import zip.sodium.pipebomb.event.builtin.ServerEvents;

public final class EventRegistrar {
    private EventRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void acknowledge(final Plugin plugin) {
        final var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(PlayerEvents.INSTANCE, plugin);
        pluginManager.registerEvents(ServerEvents.INSTANCE, plugin);
    }
}
