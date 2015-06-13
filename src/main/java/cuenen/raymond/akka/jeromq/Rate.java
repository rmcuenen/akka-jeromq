package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Sets the maximum send or receive data rate for multicast transports such as
 * pgm using the specified socket.
 * <p>
 * {@code value} - The kilobits per second
 */
public class Rate implements SocketOption {

    public static Rate create(long value) {
        return new Rate(value);
    }

    public final long value;

    public Rate(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rate) {
            Rate that = (Rate) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Rate(%d)", value);
    }

}
