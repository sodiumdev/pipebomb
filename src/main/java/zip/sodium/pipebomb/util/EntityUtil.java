package zip.sodium.pipebomb.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.UnknownNullability;
import zip.sodium.pipebomb.Pipebomb;

import java.util.function.Consumer;

public final class EntityUtil {
    private EntityUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T extends Entity> @UnknownNullability T create(final EntityType<T> entityType, final Level level, final Consumer<T> configurator) {
        final var button = entityType.create(level);
        if (button == null) {
            Pipebomb.logger().warning("Failed to create entity, are you calling from the wrong thread?");

            return null;
        }

        configurator.accept(button);
        level.addFreshEntity(button);

        return button;
    }
}
