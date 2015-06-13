package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.ReconnectIVLMax} option
 * shall set the maximum reconnection interval for the specified socket. This is
 * the maximum period ØMQ shall wait between attempts to reconnect. On each
 * reconnect attempt, the previous interval shall be doubled until
 * {@link cuenen.raymond.akka.jeromq.ReconnectIVLMax} is reached.
 * This allows for exponential backoff strategy. Default value means no
 * exponential backoff is performed and reconnect interval calculations are only
 * based on {@link cuenen.raymond.akka.jeromq.ReconnectIVL}.
 * <p>
 * This is a ZeroMQ 3.0 option.
 *
 * @see cuenen.raymond.akka.jeromq.ReconnectIVL
 */
public class ReconnectIVLMax implements SocketOption {

    public static ReconnectIVLMax create(long value) {
        return new ReconnectIVLMax(value);
    }

    public final long value;

    public ReconnectIVLMax(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReconnectIVLMax) {
            ReconnectIVLMax that = (ReconnectIVLMax) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ReconnectIVLMax(%d)", value);
    }

}
