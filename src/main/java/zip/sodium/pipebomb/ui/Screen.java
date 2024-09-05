package zip.sodium.pipebomb.ui;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import zip.sodium.pipebomb.object.PlayerScreenHolder;
import zip.sodium.pipebomb.ui.component.ScreenComponent;

import java.util.concurrent.CompletableFuture;

public interface Screen  {
    static Screen create(final @NotNull Player player) {
        Preconditions.checkArgument(player != null, "Player is null");

        return DisplayScreen.create(player);
    }

    boolean clicked();

    double mouseX();
    double mouseY();

    @UnknownNullability Player player();

    CompletableFuture<Void> tick();

    void addComponent(final @NotNull ScreenComponent component);

    boolean shouldRemove();
    void cleanup();

    default void open() {
        PlayerScreenHolder.add(this);
    }

    void close();
}
