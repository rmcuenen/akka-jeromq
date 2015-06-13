package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Controls whether data sent via multicast transports using the specified
 * socket can also be received by the sending host via loop-back. A value of
 * zero disables the loop-back functionality, while the default value of 1
 * enables the loop-back functionality. Leaving multicast loop-back enabled when
 * it is not required can have a negative impact on performance. Where possible,
 * disable McastLoop in production environments.
 * <p>
 * {@code value} - Flag indicating whether or not loopback multicast is enabled
 */
public class MulticastLoop implements SocketOption {

    public static MulticastLoop create(boolean value) {
        return new MulticastLoop(value);
    }

    public final boolean value;

    public MulticastLoop(boolean value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MulticastLoop) {
            MulticastLoop that = (MulticastLoop) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("MulticastLoop(%s)", value);
    }

}
