package cuenen.raymond.akka.jeromq;

/**
 * The different socket types you can create with zeromq.
 */
public final class SocketType {

    public static abstract class ZMQSocketType implements SocketMeta {

        public final org.zeromq.SocketType id;

        public ZMQSocketType(org.zeromq.SocketType id) {
            this.id = id;
        }

    }

    /**
     * A Publisher socket.
     */
    public static final ZMQSocketType Pub = new ZMQSocketType(org.zeromq.SocketType.PUB) {

        @Override
        public String toString() {
            return "Pub";
        }

    };

    /**
     * A subscriber socket.
     */
    public static final ZMQSocketType Sub = new ZMQSocketType(org.zeromq.SocketType.SUB) {

        @Override
        public String toString() {
            return "Sub";
        }

    };

    /**
     * A dealer socket.
     */
    public static final ZMQSocketType Dealer = new ZMQSocketType(org.zeromq.SocketType.DEALER) {

        @Override
        public String toString() {
            return "Dealer";
        }

    };

    /**
     * A router socket.
     */
    public static final ZMQSocketType Router = new ZMQSocketType(org.zeromq.SocketType.ROUTER) {

        @Override
        public String toString() {
            return "Router";
        }

    };

    /**
     * A request socket.
     */
    public static final ZMQSocketType Req = new ZMQSocketType(org.zeromq.SocketType.REQ) {

        @Override
        public String toString() {
            return "Req";
        }

    };

    /**
     * A reply socket.
     */
    public static final ZMQSocketType Rep = new ZMQSocketType(org.zeromq.SocketType.REP) {

        @Override
        public String toString() {
            return "Rep";
        }

    };

    /**
     * A push socket.
     */
    public static final ZMQSocketType Push = new ZMQSocketType(org.zeromq.SocketType.PUSH) {

        @Override
        public String toString() {
            return "Push";
        }

    };

    /**
     * A pull socket.
     */
    public static final ZMQSocketType Pull = new ZMQSocketType(org.zeromq.SocketType.PULL) {

        @Override
        public String toString() {
            return "Pull";
        }

    };

    /**
     * A Pair socket.
     */
    public static final ZMQSocketType Pair = new ZMQSocketType(org.zeromq.SocketType.PAIR) {

        @Override
        public String toString() {
            return "Pair";
        }

    };

    private SocketType() {
    }

}
