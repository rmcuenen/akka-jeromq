package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * A Model to represent a version of the zeromq library.
 */
public class ZeroMQVersion {

    public static ZeroMQVersion create(int major, int minor, int patch) {
        return new ZeroMQVersion(major, minor, patch);
    }

    public final int major;
    public final int minor;
    public final int patch;

    public ZeroMQVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public int hashCode() {
        return 41 * (41 * (41 + Objects.hashCode(major)) + Objects.hashCode(minor)) + Objects.hashCode(patch);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZeroMQVersion) {
            ZeroMQVersion that = (ZeroMQVersion) obj;
            return Objects.equals(this.major, that.major)
                    && Objects.equals(this.minor, that.minor)
                    && Objects.equals(this.patch, that.patch);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

}
