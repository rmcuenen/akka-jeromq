package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Sets the time-to-live field in every multicast packet sent from this socket.
 * The default is 1 which means that the multicast packets don't leave the local
 * network.
 * <p>
 * This is a ZeroMQ 3.0 option.
 */
public class MulticastHops implements SocketOption {

    public static MulticastHops create(long value) {
        return new MulticastHops(value);
    }

    public final long value;

    public MulticastHops(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MulticastHops) {
            MulticastHops that = (MulticastHops) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("MulticastHops(%d)", value);
    }

}
