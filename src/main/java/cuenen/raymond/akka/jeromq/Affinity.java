package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.Affinity} option shall
 * set the I/O thread affinity for newly created connections on the specified
 * socket.
 * <p>
 * Affinity determines which threads from the ØMQ I/O thread pool associated
 * with the socket's context shall handle newly created connections. A value of
 * zero specifies no affinity, meaning that work shall be distributed fairly
 * among all ØMQ I/O threads in the thread pool. For non-zero values, the lowest
 * bit corresponds to thread 1, second lowest bit to thread 2 and so on. For
 * example, a value of 3 specifies that subsequent connections on socket shall
 * be handled exclusively by I/O threads 1 and 2.
 */
public class Affinity implements SocketOption {

    public static Affinity create(long value) {
        return new Affinity(value);
    }

    public final long value;

    public Affinity(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Affinity) {
            Affinity that = (Affinity) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Affinity(%d)", value);
    }

}
