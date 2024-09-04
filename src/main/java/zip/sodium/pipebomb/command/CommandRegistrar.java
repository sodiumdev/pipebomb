package zip.sodium.pipebomb.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import zip.sodium.pipebomb.command.builtin.UiCommand;

public final class CommandRegistrar {
    private CommandRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void acknowledge(final Plugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            UiCommand.register(commands);
        });
    }
}
