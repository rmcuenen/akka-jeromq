package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.SendHighWatermark} option
 * shall set the high water mark for outbound messages on the specified socket.
 * The high water mark is a hard limit on the maximum number of outstanding
 * messages ØMQ shall queue in memory for any single peer that the specified
 * socket is communicating with.
 * <p>
 * If this limit has been reached the socket shall enter an exceptional state
 * and depending on the socket type, ØMQ shall take appropriate action such as
 * blocking or dropping sent messages.
 * <p>
 * This is a ZeroMQ 3.0 option.
 */
public class SendHighWatermark implements SocketOption {

    public static SendHighWatermark create(int value) {
        return new SendHighWatermark(value);
    }

    public final int value;

    public SendHighWatermark(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SendHighWatermark) {
            SendHighWatermark that = (SendHighWatermark) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("SendHighWatermark(%d)", value);
    }

}
