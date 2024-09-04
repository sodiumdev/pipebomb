package zip.sodium.pipebomb.ui.component.builtin;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import zip.sodium.pipebomb.ui.Screen;
import zip.sodium.pipebomb.ui.component.ScreenComponent;
import zip.sodium.pipebomb.util.EntityUtil;
import zip.sodium.pipebomb.util.VectorUtil;

public final class CursorComponent implements ScreenComponent {
    private Display.BlockDisplay cursor;

    @Override
    public void boot(final @NotNull Screen screen) {
        final var handle = ((CraftPlayer) screen.player()).getHandle();
        final var level = handle.level();

        cursor = EntityUtil.create(EntityType.BLOCK_DISPLAY, level, entity -> {
            entity.setBrightnessOverride(Brightness.FULL_BRIGHT);
            entity.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1);
            entity.setTransformation(new Transformation(
                    new Vector3f(
                            -0.0125F / 2,
                            -0.0125F / 2,
                            0
                    ),
                    null,
                    new Vector3f(
                            0.0125F,
                            0.0125F,
                            VectorUtil.ALMOST_ZERO
                    ),
                    null
            ));
            entity.setBlockState(Blocks.WHITE_CONCRETE.defaultBlockState());
            entity.setPos(
                    handle.getEyePosition().add(
                            screen.mouseX(),
                            screen.mouseY(),
                            1
                    )
            );
        });
    }

    @Override
    public void tick(final @NotNull Screen screen) {
        final var player = screen.player();
        final var handle = ((CraftPlayer) player).getHandle();
        final var position = handle.getEyePosition().add(
                screen.mouseX(),
                screen.mouseY(),
                1
        );

        ScreenComponent.move(() -> cursor.setPos(position));
    }

    @Override
    public void cleanup(final @NotNull Screen entity) {
        ScreenComponent.move(() -> cursor.remove(Entity.RemovalReason.DISCARDED));
    }
}
