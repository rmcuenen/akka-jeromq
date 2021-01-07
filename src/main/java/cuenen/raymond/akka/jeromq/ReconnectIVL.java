package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Sets the recovery interval for multicast transports using the specified
 * socket. The recovery interval determines the maximum time in seconds that a
 * receiver can be absent from a multicast group before unrecoverable data loss
 * will occur.
 * <p>
 * Exercise care when setting large recovery intervals as the data needed for
 * recovery will be held in memory. For example, a 1 minute recovery interval at
 * a data rate of 1Gbps requires a 7GB in-memory buffer.
 * <p>
 * {@code value} - The interval in seconds
 */
public class ReconnectIVL implements SocketOption {

    public static ReconnectIVL create(int value) {
        return new ReconnectIVL(value);
    }

    public final int value;

    public ReconnectIVL(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReconnectIVL) {
            ReconnectIVL that = (ReconnectIVL) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ReconnectIVL(%d)", value);
    }

}
