package zip.sodium.pipebomb.ui.component.builtin;

import com.google.common.base.Preconditions;
import com.mojang.math.Transformation;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import zip.sodium.pipebomb.geometry.Rect;
import zip.sodium.pipebomb.ui.Screen;
import zip.sodium.pipebomb.ui.component.ScreenComponent;
import zip.sodium.pipebomb.util.EntityUtil;
import zip.sodium.pipebomb.util.VectorUtil;

import java.util.function.Consumer;

public class ButtonComponent implements ScreenComponent {
    public static final Brightness HOVER_BRIGHTNESS = new Brightness(8, 8);
    public static final Brightness CLICK_BRIGHTNESS = new Brightness(5, 5);

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {}

        private Consumer<Screen> onClick = screen -> {};
        private Rect rect = new Rect(0, 0, 1, 1);
        private Vector2d position = null;
        private Vector2f scale = new Vector2f(1);
        private Component label = Component.text("");

        public Builder onClick(final Consumer<Screen> onClick) {
            this.onClick = onClick;

            return this;
        }

        public Builder rect(final Rect rect) {
            this.rect = rect;

            return this;
        }

        public Builder position(final double x, final double y) {
            this.position = new Vector2d(x, y);

            return this;
        }

        public Builder scale(final float x, final float y) {
            this.scale = new Vector2f(x, y);

            return this;
        }

        public Builder label(final Component label) {
            this.label = label;

            return this;
        }

        public ButtonComponent build() {
            return new ButtonComponent(
                    Preconditions.checkNotNull(rect),
                    Preconditions.checkNotNull(position),
                    Preconditions.checkNotNull(scale),
                    Preconditions.checkNotNull(label),
                    Preconditions.checkNotNull(onClick)
            );
        }
    }

    private Display.BlockDisplay buttonDisplay;
    private Display.TextDisplay labelDisplay;

    private final Rect rect;
    private final Vector2d position;
    private final Vector2f scale;
    private final Component label;
    private final Consumer<Screen> onClick;

    private volatile boolean hovering = false;

    private ButtonComponent(final Rect rect,
                            final Vector2d position,
                            final Vector2f scale,
                            final Component label,
                            final Consumer<Screen> onClick) {
        this.rect = rect.scale(scale);
        this.position = position;
        this.scale = scale;
        this.label = label;
        this.onClick = onClick;
    }

    @Override
    public void boot(final @NotNull Screen screen) {
        final var handle = ((CraftPlayer) screen.player()).getHandle();
        final var basePosition = VectorUtil.add(handle.getEyePosition(), position);
        final var level = handle.level();

        buttonDisplay = EntityUtil.create(EntityType.BLOCK_DISPLAY, level, entity -> {
            entity.setBrightnessOverride(Brightness.FULL_BRIGHT);
            entity.setTransformationInterpolationDuration(2);
            entity.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1);
            entity.setTransformation(VectorUtil.scaled(scale));
            entity.setBlockState(Blocks.GRAY_CONCRETE.defaultBlockState());
            entity.setPos(
                    basePosition.add(0, 0, VectorUtil.behind(3))
            );
        });

        labelDisplay = EntityUtil.create(EntityType.TEXT_DISPLAY, level, entity -> {
            entity.setBrightnessOverride(Brightness.FULL_BRIGHT);
            entity.setBillboardConstraints(Display.BillboardConstraints.CENTER);
            entity.setTransformationInterpolationDuration(2);
            entity.getEntityData().set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, 1);
            entity.setTransformation(VectorUtil.scaledText(scale));
            entity.setText(PaperAdventure.asVanilla(label));
            entity.getEntityData().set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, 0);
            entity.setPos(
                    basePosition.add(0, 0, VectorUtil.behind(2))
            );
        });
    }

    @Override
    public void tick(final @NotNull Screen screen) {
        final var handle = ((CraftPlayer) screen.player()).getHandle();

        final var buttonPos = buttonDisplay.position().subtract(handle.getEyePosition());

        final double mouseX = screen.mouseX() - buttonPos.x;
        final double mouseY = screen.mouseY() - buttonPos.y;

        if (rect.minX() <= mouseX && rect.maxX() >= mouseX
                && rect.minY() <= mouseY
                && rect.maxY() >= mouseY) {
            if (!hovering) {
                hovering = true;

                onHoverStart(screen);
            }

            if (screen.clicked())
                onClick(screen);
        } else if (hovering) {
            hovering = false;

            onHoverStop(screen);
        }
    }

    protected void onClick(final @NotNull Screen screen) {
        ScreenComponent.move(() -> {
            onClick.accept(screen);

            buttonDisplay.setBrightnessOverride(CLICK_BRIGHTNESS);
            buttonDisplay.setTransformationInterpolationDelay(-1);
        });
    }

    protected void onHoverStart(final @NotNull Screen screen) {
        ScreenComponent.move(() -> {
            buttonDisplay.setTransformation(new Transformation(
                    new Vector3f(
                            -scale.x * 1.1F / 2,
                            -scale.y * 1.1F / 2,
                            0
                    ),
                    null,
                    new Vector3f(
                            scale.x * 1.1F,
                            scale.y * 1.1F,
                            VectorUtil.ALMOST_ZERO
                    ),
                    null
            ));
            buttonDisplay.setBrightnessOverride(HOVER_BRIGHTNESS);
            buttonDisplay.setTransformationInterpolationDelay(-1);
        });
    }

    protected void onHoverStop(final @NotNull Screen screen) {
        ScreenComponent.move(() -> {
            buttonDisplay.setTransformation(new Transformation(
                    new Vector3f(
                            -scale.x * 0.5F,
                            -scale.y * 0.5F,
                            0
                    ),
                    null,
                    new Vector3f(
                            scale.x,
                            scale.y,
                            VectorUtil.ALMOST_ZERO
                    ),
                    null
            ));
            buttonDisplay.setBrightnessOverride(Brightness.FULL_BRIGHT);
            buttonDisplay.setTransformationInterpolationDelay(-1);
        });
    }

    @Override
    public void cleanup(final @NotNull Screen screen) {
        ScreenComponent.move(() -> {
            buttonDisplay.remove(Entity.RemovalReason.DISCARDED);
            labelDisplay.remove(Entity.RemovalReason.DISCARDED);
        });
    }
}
