package lol.magix.windtrace.objects;

import emu.grasscutter.utils.Position;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @EqualsAndHashCode
public final class Vector3 {
    public static final Vector3 ZERO = new Vector3();

    public static Vector3 from(Position position) {
        return new Vector3(position.getX(), position.getY(), position.getZ());
    }

    @Getter @Setter
    private float x, y, z;

    public Vector3() {
        this.x = 0.0f; this.y = 0.0f; this.z = 0.0f;
    }

    public Vector3(float x, float y, float z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Vector3(Vector3 vector) {
        this.x = vector.x; this.y = vector.y; this.z = vector.z;
    }

    /*
     * Conversion methods.
     */

    public Position to() {
        return new Position(this.x, this.y, this.z);
    }

    /*
     * Mathematical methods.
     */

    public float distance(Vector3 vector) {
        return (float) Math.sqrt(
                Math.pow(this.x - vector.x, 2) +
                Math.pow(this.y - vector.y, 2) +
                Math.pow(this.z - vector.z, 2)
        );
    }

    /*
     * Simple operation methods.
     */

    public Vector3 add(Vector3 vector) {
        this.x += vector.x; this.y += vector.y; this.z += vector.z;
        return this;
    }

    public Vector3 add(float x, float y, float z) {
        this.x += x; this.y += y; this.z += z;
        return this;
    }

    public Vector3 subtract(Vector3 vector) {
        this.x -= vector.x; this.y -= vector.y; this.z -= vector.z;
        return this;
    }

    public Vector3 subtract(float x, float y, float z) {
        this.x -= x; this.y -= y; this.z -= z;
        return this;
    }

    public Vector3 multiply(Vector3 vector) {
        this.x *= vector.x; this.y *= vector.y; this.z *= vector.z;
        return this;
    }

    public Vector3 multiply(float x, float y, float z) {
        this.x *= x; this.y *= y; this.z *= z;
        return this;
    }

    public Vector3 divide(Vector3 vector) {
        this.x /= vector.x; this.y /= vector.y; this.z /= vector.z;
        return this;
    }

    public Vector3 divide(float x, float y, float z) {
        this.x /= x; this.y /= y; this.z /= z;
        return this;
    }
}