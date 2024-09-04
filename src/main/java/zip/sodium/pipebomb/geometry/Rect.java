package zip.sodium.pipebomb.geometry;

import org.joml.Vector2f;

public record Rect(double minX, double minY,
                   double maxX, double maxY) {
    public Rect scale(final Vector2f scale) {
        return new Rect(
                (minX - 0.5 * scale.x),
                (minY - 0.5) * scale.y,
                (maxX - 0.5) * scale.x,
                (maxY - 0.5) * scale.y
        );
    }
}
