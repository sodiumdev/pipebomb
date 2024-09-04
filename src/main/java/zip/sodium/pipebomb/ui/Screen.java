package zip.sodium.pipebomb.ui;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnknownNullability;
import zip.sodium.pipebomb.ui.component.ScreenComponent;
import zip.sodium.pipebomb.util.EntityUtil;
import zip.sodium.pipebomb.util.VectorUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Screen extends ChannelDuplexHandler {
    private static final String INTERCEPTOR_NAME = "mouse_listener";

    private static final Field SERVER_PLAYER_CAMERA_FIELD;
    static {
        try {
            SERVER_PLAYER_CAMERA_FIELD = ServerPlayer.class.getDeclaredField("camera");
            SERVER_PLAYER_CAMERA_FIELD.setAccessible(true);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<ScreenComponent> components = ConcurrentHashMap.newKeySet();

    private final WeakReference<Player> player;
    private final Display.BlockDisplay camera;

    private volatile boolean clicked = false;
    private volatile double mouseX = 0;
    private volatile double mouseY = 0;

    public Screen(final Player player) {
        Preconditions.checkArgument(player != null, "Player is null");

        this.player = new WeakReference<>(player);

        final var handle = ((CraftPlayer) player).getHandle();
        final var conn = handle.connection.connection;
        conn.channel.pipeline().addBefore("packet_handler", INTERCEPTOR_NAME, this);

        final var level = handle.level();

        camera = EntityUtil.create(EntityType.BLOCK_DISPLAY, level, entity -> {
            entity.setBlockState(Blocks.AIR.defaultBlockState());
            entity.setPos(handle.getEyePosition());
        });

        handle.startRiding(camera, true);

        handle.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);
        handle.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, (float) GameType.SPECTATOR.getId()));

        try {
            SERVER_PLAYER_CAMERA_FIELD.set(handle, camera);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

        handle.connection.send(new ClientboundSetCameraPacket(camera));
        handle.connection.resetPosition();

        handle.connection.send(
                new ClientboundBlockUpdatePacket(
                        camera.blockPosition(),
                        Blocks.BARRIER.defaultBlockState()
                )
        );
    }

    public boolean clicked() {
        return clicked;
    }

    public double mouseX() {
        return mouseX;
    }

    public double mouseY() {
        return mouseY;
    }

    public @UnknownNullability Player player() {
        return player.get();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof ServerboundSwingPacket)
            clicked = true;
        else if (msg instanceof final ServerboundMovePlayerPacket.Rot move) {
            mouseX = VectorUtil.transformX(move.yRot);
            mouseY = VectorUtil.transformY(move.xRot);
        } else super.channelRead(ctx, msg);
    }

    public CompletableFuture<Void> tick() {
        if (player.get() == null)
            return CompletableFuture.completedFuture(null);

        return CompletableFuture.allOf(
                components.parallelStream()
                        .map(component -> CompletableFuture.runAsync(() -> component.tick(this)))
                        .toArray(CompletableFuture[]::new)
        ).thenRunAsync(this::tryCleanup).thenRunAsync(() -> {
            if (clicked)
                clicked = false;
        });
    }

    private void tryCleanup() {
        final Iterator<ScreenComponent> each = components.iterator();
        while (each.hasNext()) {
            final var next = each.next();
            if (next.removeIf(this)) {
                next.cleanup(this);
                each.remove();
            }
        }
    }

    public void addComponent(final ScreenComponent component) {
        components.add(component);
        component.boot(this);
    }

    public boolean removeIf() {
        final var player = player();

        if (player == null
                || !player.isConnected()
                || player.getGameMode() != GameMode.SPECTATOR)
            return true;

        final var handle = ((CraftPlayer) player).getHandle();
        return handle.getVehicle() != camera
                || handle.getCamera() != camera;
    }

    public void cleanup() {
        final var player = player();
        if (player == null || !player.isConnected())
            return;

        final var handle = ((CraftPlayer) player).getHandle();

        try {
            handle
                    .connection
                    .connection
                    .channel
                    .pipeline()
                    .remove(INTERCEPTOR_NAME);
        } catch (final NoSuchElementException ignored) {}

        ScreenComponent.move(() -> {
            if (handle.getVehicle() == camera)
                handle.stopRiding(true);
            if (handle.gameMode.getGameModeForPlayer() == GameType.SPECTATOR
                    && handle.getCamera() == camera)
                handle.setCamera(null);

            player.setGameMode(Objects.requireNonNullElse(
                    player.getPreviousGameMode(),
                    GameMode.SURVIVAL
            ));
        });

        final Iterator<ScreenComponent> each = components.iterator();
        while (each.hasNext()) {
            each.next().cleanup(this);
            each.remove();
        }
    }
}
