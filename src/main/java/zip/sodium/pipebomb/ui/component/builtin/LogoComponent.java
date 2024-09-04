package zip.sodium.pipebomb.ui.component.builtin;

import com.google.common.base.Preconditions;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import zip.sodium.pipebomb.ui.Screen;
import zip.sodium.pipebomb.ui.component.ScreenComponent;
import zip.sodium.pipebomb.util.EntityUtil;
import zip.sodium.pipebomb.util.VectorUtil;

public class LogoComponent implements ScreenComponent {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {}

        private Vector2d position = null;
        private Vector2f scale = new Vector2f(1);

        public Builder position(final double x, final double y) {
            this.position = new Vector2d(x, y);

            return this;
        }

        public Builder scale(final float x, final float y) {
            this.scale = new Vector2f(x, y);

            return this;
        }

        public LogoComponent build() {
            return new LogoComponent(
                    Preconditions.checkNotNull(position),
                    Preconditions.checkNotNull(scale)
            );
        }
    }

    private Display.ItemDisplay logoDisplay;

    private final Vector2d position;
    private final Vector2f scale;

    private LogoComponent(final Vector2d position, final Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    @Override
    public void boot(final @NotNull Screen screen) {
        final var handle = ((CraftPlayer) screen.player()).getHandle();
        final var basePosition = VectorUtil.add(handle.getEyePosition(), position);
        final var level = handle.level();

        logoDisplay = EntityUtil.create(EntityType.ITEM_DISPLAY, level, entity -> {
            entity.setBrightnessOverride(Brightness.FULL_BRIGHT);
            entity.setTransformationInterpolationDuration(2);
            entity.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1);
            entity.setTransformation(VectorUtil.scaled(scale));
            entity.setItemStack(Items.DIAMOND_AXE.getDefaultInstance());
            entity.setItemTransform(ItemDisplayContext.GUI);
            entity.setPos(
                    basePosition.add(0, 0, VectorUtil.behind(3))
            );
        });
    }

    @Override
    public void cleanup(final @NotNull Screen screen) {
        ScreenComponent.move(() -> logoDisplay.remove(Entity.RemovalReason.DISCARDED));
    }
}
