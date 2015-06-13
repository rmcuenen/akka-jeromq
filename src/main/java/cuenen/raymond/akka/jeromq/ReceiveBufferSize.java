package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.ReceiveBufferSize} option
 * shall set the underlying kernel receive buffer size for the socket to the
 * specified size in bytes. A value of zero means leave the OS default
 * unchanged. For details refer to your operating system documentation for the
 * SO_RCVBUF socket option.
 */
public class ReceiveBufferSize implements SocketOption {

    public static ReceiveBufferSize create(long value) {
        return new ReceiveBufferSize(value);
    }

    public final long value;

    public ReceiveBufferSize(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReceiveBufferSize) {
            ReceiveBufferSize that = (ReceiveBufferSize) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ReceiveBufferSize(%d)", value);
    }

}
