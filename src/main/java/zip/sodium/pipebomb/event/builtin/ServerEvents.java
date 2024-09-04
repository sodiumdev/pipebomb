package zip.sodium.pipebomb.event.builtin;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zip.sodium.pipebomb.object.PlayerScreenHolder;

public final class ServerEvents implements Listener {
    public static final Listener INSTANCE = new ServerEvents();

    private ServerEvents() {}

    @EventHandler
    public void onTickStart(final ServerTickStartEvent event) {
        PlayerScreenHolder.tick();
    }
}
