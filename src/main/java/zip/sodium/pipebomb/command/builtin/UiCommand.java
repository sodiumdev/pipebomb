package zip.sodium.pipebomb.command.builtin;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import zip.sodium.pipebomb.object.PlayerScreenHolder;
import zip.sodium.pipebomb.ui.Screen;
import zip.sodium.pipebomb.ui.component.builtin.ButtonComponent;
import zip.sodium.pipebomb.ui.component.builtin.CursorComponent;
import zip.sodium.pipebomb.ui.component.builtin.LogoComponent;

public final class UiCommand {
    private UiCommand() {}

    public static void register(final Commands commands) {
        commands.register(
                Commands.literal("ui")
                        .executes(UiCommand::execute)
                        .build()
        );
    }

    private static int execute(final CommandContext<CommandSourceStack> context) {
        final var executor = context.getSource().getExecutor();
        if (!(executor instanceof Player player))
            return 0;

        final var screen = new Screen(player);
        screen.addComponent(new CursorComponent());

        screen.addComponent(
                LogoComponent.builder()
                        .position(-150, -75)
                        .build()
        );

        screen.addComponent(
                ButtonComponent.builder()
                        .position(50, -40)
                        .scale(0.24F, 0.12F)
                        .label(Component.text("Play"))
                        .build()
        );

        screen.addComponent(
                ButtonComponent.builder()
                        .position(50, 0)
                        .scale(0.24F, 0.12F)
                        .label(Component.text("Credits"))
                        .build()
        );

        screen.addComponent(
                ButtonComponent.builder()
                        .position(50, 40)
                        .scale(0.24F, 0.12F)
                        .label(Component.text("Quit"))
                        .build()
        );

        PlayerScreenHolder.add(screen);

        return 1;
    }
}
