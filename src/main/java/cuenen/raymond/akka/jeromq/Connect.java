package cuenen.raymond.akka.jeromq;

import java.util.Objects;

/**
 * This socket should be a client socket and connect to the specified endpoint.
 * <p>
 * {@code endpoint} - URI (ex. tcp://127.0.0.1:5432)
 */
public class Connect implements SocketConnectOption {

    public static Connect create(String endpoint) {
        return new Connect(endpoint);
    }

    private final String endpoint;

    public Connect(String endpoint) {
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
        if (obj instanceof Connect) {
            Connect that = (Connect) obj;
            return Objects.equals(this.endpoint, that.endpoint);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Connect(%s)", endpoint);
    }

}
