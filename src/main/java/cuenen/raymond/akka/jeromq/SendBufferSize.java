package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.SendBufferSize} option
 * shall set the underlying kernel transmit buffer size for the socket to the
 * specified size in bytes. A value of zero means leave the OS default
 * unchanged. For details please refer to your operating system documentation
 * for the SO_SNDBUF socket option.
 * <p>
 * This is a ZeroMQ 2.x only option.
 */
public class SendBufferSize implements SocketOption {

    public static SendBufferSize create(int value) {
        return new SendBufferSize(value);
    }

    public final int value;

    public SendBufferSize(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SendBufferSize) {
            SendBufferSize that = (SendBufferSize) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("SendBufferSize(%d)", value);
    }

}
