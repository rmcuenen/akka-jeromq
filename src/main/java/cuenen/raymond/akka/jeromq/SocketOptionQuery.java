package cuenen.raymond.akka.jeromq;

/**
 * A marker interface to group option queries together.
 */
public interface SocketOptionQuery extends Request {

    /**
     * Gets the linger option.
     *
     * @see cuenen.raymond.akka.jeromq.Linger
     */
    public static final SocketOptionQuery Linger = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "Linger";
        }

    };

    /**
     * Gets the recover interval.
     *
     * @see cuenen.raymond.akka.jeromq.ReconnectIVL
     */
    public static final SocketOptionQuery ReconnectIVL = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "ReconnectIVL";
        }

    };

    /**
     * Gets the max reconnect IVL.
     *
     * @see cuenen.raymond.akka.jeromq.ReconnectIVLMax
     */
    public static final SocketOptionQuery ReconnectIVLMax = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "ReconnectIVLMax";
        }

    };

    /**
     * Gets the backlog.
     *
     * @see cuenen.raymond.akka.jeromq.Backlog
     */
    public static final SocketOptionQuery Backlog = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "Backlog";
        }

    };

    public static final SocketOptionQuery MaxMsgSize = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "MaxMsgSize";
        }

    };

    /**
     * Gets the SendHWM.
     *
     * @see cuenen.raymond.akka.jeromq.SendHighWatermark
     */
    public static final SocketOptionQuery SendHighWatermark = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "SendHighWatermark";
        }

    };

    /**
     * Gets the ReceiveHighWatermark.
     *
     * @see cuenen.raymond.akka.jeromq.ReceiveHighWatermark
     */
    public static final SocketOptionQuery ReceiveHighWatermark = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "ReceiveHighWatermark";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.Affinity} value.
     */
    public static final SocketOptionQuery Affinity = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "Affinity";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.Identity}.
     */
    public static final SocketOptionQuery Identity = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "Identity";
        }

    };

    /**
     * Gets the send or receive rate for the socket.
     */
    public static final SocketOptionQuery Rate = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "Rate";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.RecoveryInterval}.
     */
    public static final SocketOptionQuery RecoveryInterval = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "RecoveryInterval";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.MulticastHops}.
     */
    public static final SocketOptionQuery MulticastHops = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "MulticastHops";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.SendBufferSize}.
     */
    public static final SocketOptionQuery SendBufferSize = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "SendBufferSize";
        }

    };

    /**
     * Gets the {@link cuenen.raymond.akka.jeromq.ReceiveBufferSize}.
     */
    public static final SocketOptionQuery ReceiveBufferSize = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "ReceiveBufferSize";
        }

    };

    /**
     * Gets the file descriptor associated with the ZeroMQ socket.
     */
    public static final SocketOptionQuery FileDescriptor = new SocketOptionQuery() {

        @Override
        public String toString() {
            return "FileDescriptor";
        }

    };
}
