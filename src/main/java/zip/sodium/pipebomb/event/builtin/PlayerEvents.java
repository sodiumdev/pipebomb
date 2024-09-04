package zip.sodium.pipebomb.event.builtin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerEvents implements Listener {
    public static final Listener INSTANCE = new PlayerEvents();

    private PlayerEvents() {}

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {}
}
