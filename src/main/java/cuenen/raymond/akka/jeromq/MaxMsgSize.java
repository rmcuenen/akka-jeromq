package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Limits the size of the inbound message. If a peer sends a message larger than
 * {@link cuenen.raymond.akka.jeromq.MaxMsgSize} it is
 * disconnected. Value of -1 means no limit.
 * <p>
 * This is a ZeroMQ 3.0 option.
 */
public class MaxMsgSize implements SocketOption {

    public static MaxMsgSize create(long value) {
        return new MaxMsgSize(value);
    }

    public final long value;

    public MaxMsgSize(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MaxMsgSize) {
            MaxMsgSize that = (MaxMsgSize) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("MaxMsgSize(%d)", value);
    }

}
