package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Configure this socket to have a linger of the specified value.
 * <p>
 * The linger period determines how long pending messages which have yet to be
 * sent to a peer shall linger in memory after a socket is closed, and further
 * affects the termination of the socket's context.
 * <p>
 * The following outlines the different behaviours:
 * <ul>
 * <li>The default value of -1 specifies an infinite linger period. Pending
 * messages shall not be discarded after the socket is closed; attempting to
 * terminate the socket's context shall block until all pending messages have
 * been sent to a peer.</li>
 * <li>The value of 0 specifies no linger period. Pending messages shall be
 * discarded immediately when the socket is closed.</li>
 * <li>Positive values specify an upper bound for the linger period in
 * milliseconds. Pending messages shall not be discarded after the socket is
 * closed; attempting to terminate the socket's context shall block until either
 * all pending messages have been sent to a peer, or the linger period expires,
 * after which any pending messages shall be discarded.</li>
 * </ul>
 *
 * {@code value} - The value in milliseconds for the linger option
 */
public class Linger implements SocketOption {
    
    public static final Linger no = new Linger(0);

    public static Linger create(long value) {
        return new Linger(value);
    }

    public final long value;

    public Linger(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Linger) {
            Linger that = (Linger) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Linger(%d)", value);
    }

}
