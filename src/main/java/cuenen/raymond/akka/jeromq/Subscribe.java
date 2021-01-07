package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.Objects;

/**
 * The {@link cuenen.raymond.akka.jeromq.Subscribe} option
 * establishes a new message filter on a
 * {@link cuenen.raymond.akka.jeromq.SocketType#Pub} socket. Newly
 * created {@link cuenen.raymond.akka.jeromq.SocketType#Sub}
 * sockets filter out all incoming messages, therefore you should send this
 * option to establish an initial message filter.
 * <p>
 * An empty payload of length zero will subscribe to all incoming messages. A
 * non-empty payload will subscribe to all messages beginning with the specified
 * prefix. Multiple filters may be attached to a single
 * {@link cuenen.raymond.akka.jeromq.SocketType#Sub} socket, in
 * which case a message will be accepted if it matches at least one filter.
 * <p>
 * {@code payload} - the topic to subscribe to
 */
public class Subscribe implements PubSubOption {

    public static final Subscribe all = new Subscribe(ByteString.emptyByteString());

    public static Subscribe create(ByteString payload) {
        return new Subscribe(payload);
    }

    public static Subscribe create(String topic) {
        if (topic == null || topic.isEmpty()) {
            return all;
        }
        return new Subscribe(topic);
    }

    private final ByteString payload;

    /**
     * @param payload the topic to subscribe to
     */
    public Subscribe(ByteString payload) {
        this.payload = payload;
    }

    public Subscribe(String topic) {
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
        if (obj instanceof Subscribe) {
            Subscribe that = (Subscribe) obj;
            return Objects.equals(this.payload, that.payload);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Subscribe(%s)", payload);
    }

}
