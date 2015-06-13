package cuenen.raymond.akka.jeromq;

import java.util.Arrays;
import java.util.Objects;

/**
 * Sets the identity of the specified socket. Socket identity determines if
 * existing ØMQ infrastructure (message queues, forwarding devices) shall be
 * identified with a specific application and persist across multiple runs of
 * the application.
 * <p>
 * If the socket has no identity, each run of an application is completely
 * separate from other runs. However, with identity set the socket shall re-use
 * any existing ØMQ infrastructure configured by the previous run(s). Thus the
 * application may receive messages that were sent in the meantime, message
 * queue limits shall be shared with previous run(s) and so on.
 * <p>
 * Identity should be at least one byte and at most 255 bytes long. Identities
 * starting with binary zero are reserved for use by ØMQ infrastructure.
 * <p>
 * {@code value} - The identity string for this socket
 */
public class Identity implements SocketOption {

    public static Identity create(byte[] value) {
        return new Identity(value);
    }

    public final byte[] value;

    public Identity(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identity) {
            Identity that = (Identity) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Identity(%s)", Arrays.toString(value));
    }

}
