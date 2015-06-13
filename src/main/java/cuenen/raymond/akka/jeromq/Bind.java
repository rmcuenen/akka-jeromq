package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * Start listening with this server socket on the specified address.
 */
public class Bind implements SocketConnectOption {

    public static Bind create(String endpoint) {
        return new Bind(endpoint);
    }
    private final String endpoint;

    public Bind(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String endpoint() {
        return endpoint;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(endpoint);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bind) {
            Bind that = (Bind) obj;
            return Objects.equals(this.endpoint, that.endpoint);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Bind(%s)", endpoint);
    }

}
