package zip.sodium.pipebomb.ui.component.builtin;

import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import zip.sodium.pipebomb.ui.Screen;
import zip.sodium.pipebomb.ui.component.ScreenComponent;
import zip.sodium.pipebomb.util.EntityUtil;
import zip.sodium.pipebomb.util.VectorUtil;

public class BackgroundComponent implements ScreenComponent {
    private Display.BlockDisplay background;

    @Override
    public void boot(final @NotNull Screen screen) {
        final var handle = ((CraftPlayer) screen.player()).getHandle();
        final var level = handle.level();

        background = EntityUtil.create(EntityType.BLOCK_DISPLAY, level, entity -> {
            entity.setBrightnessOverride(Brightness.FULL_BRIGHT);
            entity.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1);
            entity.setTransformation(VectorUtil.scaledCentered(new Vector2f(10)));
            entity.setBlockState(Blocks.BLACK_CONCRETE.defaultBlockState());
            entity.setPos(
                    VectorUtil.add(handle.getEyePosition(), 0, 0)
                            .add(0, 0, VectorUtil.behind(2))
            );
        });
    }

    @Override
    public void cleanup(final @NotNull Screen entity) {
        ScreenComponent.move(() -> background.remove(Entity.RemovalReason.DISCARDED));
    }
}
