package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.Swap} option shall set
 * the disk offload (swap) size for the specified socket. A socket which has
 * {@link cuenen.raymond.akka.jeromq.Swap} set to a non-zero value
 * may exceed its high water mark; in this case outstanding messages shall be
 * offloaded to storage on disk rather than held in memory.
 * <p>
 * The value of {@link cuenen.raymond.akka.jeromq.Swap} defines
 * the maximum size of the swap space in bytes.
 */
public class Swap implements SocketOption {

    public static Swap create(long value) {
        return new Swap(value);
    }

    public final long value;

    public Swap(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Swap) {
            Swap that = (Swap) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Swap(%d)", value);
    }

}
