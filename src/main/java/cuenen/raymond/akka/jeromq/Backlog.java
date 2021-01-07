package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.Backlog} option shall set
 * the maximum length of the queue of outstanding peer connections for the
 * specified socket; this only applies to connection-oriented transports. For
 * details refer to your operating system documentation for the listen function.
 */
public class Backlog implements SocketOption {

    public static Backlog create(int value) {
        return new Backlog(value);
    }

    public final int value;

    public Backlog(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Backlog) {
            Backlog that = (Backlog) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Backlog(%d)", value);
    }

}
