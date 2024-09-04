package zip.sodium.pipebomb;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import zip.sodium.pipebomb.command.CommandRegistrar;
import zip.sodium.pipebomb.event.EventRegistrar;
import zip.sodium.pipebomb.object.PlayerScreenHolder;

import java.util.logging.Logger;

public final class Pipebomb extends JavaPlugin {
    private static Pipebomb instance;

    public static @NotNull Logger logger() {
        Preconditions.checkArgument(instance != null, "Plugin not initialized yet");

        return instance.getLogger();
    }

    @Override
    public void onEnable() {
        instance = this;

        EventRegistrar.acknowledge(this);
        CommandRegistrar.acknowledge(this);
    }

    @Override
    public void onDisable() {
        PlayerScreenHolder.cleanup();
    }
}
