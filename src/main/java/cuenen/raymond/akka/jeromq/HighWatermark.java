package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.HighWatermark} option
 * shall set the high water mark for the specified socket. The high water mark
 * is a hard limit on the maximum number of outstanding messages ØMQ shall queue
 * in memory for any single peer that the specified socket is communicating
 * with.
 * <p>
 * If this limit has been reached the socket shall enter an exceptional state
 * and depending on the socket type, ØMQ shall take appropriate action such as
 * blocking or dropping sent messages. The default
 * {@link cuenen.raymond.akka.jeromq.HighWatermark} value of zero
 * means "no limit".
 */
public class HighWatermark implements SocketOption {

    public static HighWatermark create(int value) {
        return new HighWatermark(value);
    }

    public final int value;

    public HighWatermark(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HighWatermark) {
            HighWatermark that = (HighWatermark) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("HighWatermark(%d)", value);
    }

}
