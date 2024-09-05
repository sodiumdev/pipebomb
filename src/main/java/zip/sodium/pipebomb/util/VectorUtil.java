package zip.sodium.pipebomb.util;

import com.mojang.math.Transformation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class VectorUtil {
    public static final float ALMOST_ZERO = 0.001f;

    public static final double XY_FACTOR = -7 / 900F;
    public static final double Y_BIAS = 1.025;

    private VectorUtil() {
        throw new UnsupportedOperationException();
    }

    public static Transformation scaledCentered(final Vector2f scale) {
        return new Transformation(
                new Vector3f(
                        scale.x * -0.5F,
                        scale.y * -0.5F,
                        0
                ),
                null,
                new Vector3f(
                        scale.x,
                        scale.y,
                        ALMOST_ZERO
                ),
                null
        );
    }

    public static Transformation scaled(final Vector2f scale) {
        return new Transformation(
                null,
                null,
                new Vector3f(
                        scale.x,
                        scale.y,
                        ALMOST_ZERO
                ),
                null
        );
    }

    public static Transformation scaledText(final Vector2f scale) {
        return new Transformation(
                new Vector3f(
                        0,
                        scale.y * -0.25F,
                        0
                ),
                null,
                new Vector3f(
                        scale.x,
                        scale.y,
                        ALMOST_ZERO
                ),
                null
        );
    }

    public static double behind(final int index) {
        return 1 + ALMOST_ZERO * index;
    }

    public static double transformX(final double x) {
        return x * XY_FACTOR;
    }

    public static double transformY(final double y) {
        return y * XY_FACTOR - Y_BIAS;
    }

    public static Vec3 add(final Vec3 a, final Vector2d b) {
        return add(a, b.x, b.y);
    }

    public static Vec3 add(final Vec3 a, final double x, final double y) {
        return a.add(
                new Vec3(
                        transformX(x),
                        transformY(y),
                        0
                )
        );
    }

    public static Vec3 lerp(final Vec3 a, final Vec3 b, final double t) {
        return b.subtract(a)
                .multiply(t, t, t)
                .add(a);
    }

    public static Vec3 moveTowards(final Vec3 current, final Vec3 target, final double maxDistanceDelta) {
        final double targetX = target.x - current.x;
        final double targetY = target.y - current.y;
        final double targetZ = target.z - current.z;

        final double distance = targetX * targetX + targetY * targetY + targetZ * targetZ;
        if (distance == 0 || (maxDistanceDelta >= 0 && distance <= maxDistanceDelta * maxDistanceDelta))
            return target;

        final double dist = Math.sqrt(distance);

        return new Vec3(
                current.x + targetX / dist * maxDistanceDelta,
                current.y + targetY / dist * maxDistanceDelta,
                current.z + targetZ / dist * maxDistanceDelta
        );
    }
}
