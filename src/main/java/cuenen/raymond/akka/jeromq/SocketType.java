package cuenen.raymond.akka.jeromq;

import org.zeromq.ZMQ;

/**
 * The different socket types you can create with zeromq.
 */
public final class SocketType {

    public static abstract class ZMQSocketType implements SocketMeta {

        public final int id;

        public ZMQSocketType(int id) {
            this.id = id;
        }

    }

    /**
     * A Publisher socket.
     */
    public static final ZMQSocketType Pub = new ZMQSocketType(ZMQ.PUB) {

        @Override
        public String toString() {
            return "Pub";
        }

    };

    /**
     * A subscriber socket.
     */
    public static final ZMQSocketType Sub = new ZMQSocketType(ZMQ.SUB) {

        @Override
        public String toString() {
            return "Sub";
        }

    };

    /**
     * A dealer socket.
     */
    public static final ZMQSocketType Dealer = new ZMQSocketType(ZMQ.DEALER) {

        @Override
        public String toString() {
            return "Dealer";
        }

    };

    /**
     * A router socket.
     */
    public static final ZMQSocketType Router = new ZMQSocketType(ZMQ.ROUTER) {

        @Override
        public String toString() {
            return "Router";
        }

    };

    /**
     * A request socket.
     */
    public static final ZMQSocketType Req = new ZMQSocketType(ZMQ.REQ) {

        @Override
        public String toString() {
            return "Req";
        }

    };

    /**
     * A reply socket.
     */
    public static final ZMQSocketType Rep = new ZMQSocketType(ZMQ.REP) {

        @Override
        public String toString() {
            return "Rep";
        }

    };

    /**
     * A push socket.
     */
    public static final ZMQSocketType Push = new ZMQSocketType(ZMQ.PUSH) {

        @Override
        public String toString() {
            return "Push";
        }

    };

    /**
     * A pull socket.
     */
    public static final ZMQSocketType Pull = new ZMQSocketType(ZMQ.PULL) {

        @Override
        public String toString() {
            return "Pull";
        }

    };

    /**
     * A Pair socket.
     */
    public static final ZMQSocketType Pair = new ZMQSocketType(ZMQ.PAIR) {

        @Override
        public String toString() {
            return "Pair";
        }

    };

    private SocketType() {
    }

}
