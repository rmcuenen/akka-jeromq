package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.Unsubscribe} option shall
 * remove an existing message filter on a
 * {@link cuenen.raymond.akka.jeromq.SocketType#Sub} socket. The
 * filter specified must match an existing filter previously established with
 * the {@link cuenen.raymond.akka.jeromq.Subscribe} option. If the
 * socket has several instances of the same filter attached the
 * {@link cuenen.raymond.akka.jeromq.Unsubscribe} option shall
 * remove only one instance, leaving the rest in place and functional.
 */
public class Unsubscribe implements PubSubOption {

    public static Unsubscribe create(ByteString payload) {
        return new Unsubscribe(payload);
    }

    public static Unsubscribe create(String topic) {
        return new Unsubscribe(topic);
    }

    private final ByteString payload;

    public Unsubscribe(ByteString payload) {
        this.payload = payload;
    }

    public Unsubscribe(String topic) {
        this(ByteString.fromString(topic));
    }

    @Override
    public ByteString payload() {
        return payload;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(payload);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Unsubscribe) {
            Unsubscribe that = (Unsubscribe) obj;
            return Objects.equals(this.payload, that.payload);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Unsubscribe(%s)", payload);
    }

}
