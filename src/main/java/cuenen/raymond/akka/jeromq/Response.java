package cuenen.raymond.akka.jeromq;

/**
 * Base interface for the events raised by a ZeroMQ socket actor.
 */
public interface Response {

    /**
     * When the ZeroMQ socket connects it sends this message to a listener.
     */
    public static final Response Connecting = new Response() {

        @Override
        public String toString() {
            return "Connecting";
        }

    };

    /**
     * When the ZeroMQ socket disconnects it sends this message to a listener.
     */
    public static final Response Closed = new Response() {

        @Override
        public String toString() {
            return "Closed";
        }

    };
}
