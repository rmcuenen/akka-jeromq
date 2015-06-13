package cuenen.raymond.akka.jeromq;

import static cuenen.raymond.akka.jeromq.Response.*;
import static cuenen.raymond.akka.jeromq.SocketOptionQuery.*;

public final class ZeroMQ {

    /**
     * The message that is sent when an ZeroMQ socket connects.
     * <pre>
     * if (message == connecting()) {
     *   // Socket connected
     * }
     * </pre>
     *
     * @return the single instance of Connecting
     */
    public static Response connecting() {
        return Connecting;
    }

    /**
     * The message that is sent when an ZeroMQ socket disconnects.
     * <pre>
     * if (message == closed()) {
     *   // Socket disconnected
     * }
     * </pre>
     *
     * @return the single instance of Closed
     */
    public static Response closed() {
        return Closed;
    }

    /**
     * The message to ask a ZeroMQ socket for its affinity configuration.
     * <pre>
     * socket.ask(affinity())
     * </pre>
     *
     * @return the single instance of Affinity
     */
    public static SocketOptionQuery affinity() {
        return Affinity;
    }

    /**
     * The message to ask a ZeroMQ socket for its backlog configuration.
     * <pre>
     * socket.ask(backlog())
     * </pre>
     *
     * @return the single instance of Backlog
     */
    public static SocketOptionQuery backlog() {
        return Backlog;
    }

    /**
     * The message to ask a ZeroMQ socket for its file descriptor configuration.
     * <pre>
     * socket.ask(fileDescriptor())
     * </pre>
     *
     * @return the single instance of FileDescriptor
     */
    public static SocketOptionQuery fileDescriptor() {
        return FileDescriptor;
    }

    /**
     * The message to ask a ZeroMQ socket for its identity configuration.
     * <pre>
     * socket.ask(identity())
     * </pre>
     *
     * @return the single instance of Identity
     */
    public static SocketOptionQuery identity() {
        return Identity;
    }

    /**
     * The message to ask a ZeroMQ socket for its linger configuration.
     * <pre>
     * socket.ask(linger())
     * </pre>
     *
     * @return the single instance of Linger
     */
    public static SocketOptionQuery linger() {
        return Linger;
    }

    /**
     * The message to ask a ZeroMQ socket for its max message size
     * configuration.
     * <pre>
     * socket.ask(maxMessageSize())
     * </pre>
     *
     * @return the single instance of MaxMsgSize
     */
    public static SocketOptionQuery maxMessageSize() {
        return MaxMsgSize;
    }

    /**
     * The message to ask a ZeroMQ socket for its multicast hops configuration.
     * <pre>
     * socket.ask(multicastHops())
     * </pre>
     *
     * @return the single instance of MulticastHops
     */
    public static SocketOptionQuery multicastHops() {
        return MulticastHops;
    }

    /**
     * The message to ask a ZeroMQ socket for its multicast loop configuration.
     * <pre>
     * socket.ask(multicastLoop())
     * </pre>
     *
     * @return the single instance of MulticastLoop
     */
    public static SocketOptionQuery multicastLoop() {
        return MulticastLoop;
    }

    /**
     * The message to ask a ZeroMQ socket for its rate configuration.
     * <pre>
     * socket.ask(rate())
     * </pre>
     *
     * @return the single instance of Rate
     */
    public static SocketOptionQuery rate() {
        return Rate;
    }

    /**
     * The message to ask a ZeroMQ socket for its receive bufferSize
     * configuration.
     * <pre>
     * socket.ask(receiveBufferSize())
     * </pre>
     *
     * @return the single instance of ReceiveBufferSize
     */
    public static SocketOptionQuery receiveBufferSize() {
        return ReceiveBufferSize;
    }

    /**
     * The message to ask a ZeroMQ socket for its receive high watermark
     * configuration.
     * <pre>
     * socket.ask(receiveHighWatermark())
     * </pre>
     *
     * @return the single instance of ReceiveHighWatermark
     */
    public static SocketOptionQuery receiveHighWatermark() {
        return ReceiveHighWatermark;
    }

    /**
     * The message to ask a ZeroMQ socket for its reconnect interval
     * configuration.
     * <pre>
     * socket.ask(reconnectIVL())
     * </pre>
     *
     * @return the single instance of ReconnectIVL
     */
    public static SocketOptionQuery reconnectIVL() {
        return ReconnectIVL;
    }

    /**
     * The message to ask a ZeroMQ socket for its max reconnect interval
     * configuration.
     * <pre>
     * socket.ask(reconnectIVLMax())
     * </pre>
     *
     * @return the single instance of ReconnectIVLMax
     */
    public static SocketOptionQuery reconnectIVLMax() {
        return ReconnectIVLMax;
    }

    /**
     * The message to ask a ZeroMQ socket for its recovery interval
     * configuration.
     * <pre>
     * socket.ask(recoveryInterval())
     * </pre>
     *
     * @return the single instance of RecoveryInterval
     */
    public static SocketOptionQuery recoveryInterval() {
        return RecoveryInterval;
    }

    /**
     * The message to ask a ZeroMQ socket for its send buffer size
     * configuration.
     * <pre>
     * socket.ask(sendBufferSize())
     * </pre>
     *
     * @return the single instance of SendBufferSize
     */
    public static SocketOptionQuery sendBufferSize() {
        return SendBufferSize;
    }

    /**
     * The message to ask a ZeroMQ socket for its send high watermark
     * configuration.
     * <pre>
     * socket.ask(sendHighWatermark())
     * </pre>
     *
     * @return the single instance of SendHighWatermark
     */
    public static SocketOptionQuery sendHighWatermark() {
        return SendHighWatermark;
    }

    /**
     * The message to ask a ZeroMQ socket for its swap configuration.
     * <pre>
     * socket.ask(swap())
     * </pre>
     *
     * @return the single instance of Swap
     */
    public static SocketOptionQuery swap() {
        return Swap;
    }

    private ZeroMQ() {
    }

}
