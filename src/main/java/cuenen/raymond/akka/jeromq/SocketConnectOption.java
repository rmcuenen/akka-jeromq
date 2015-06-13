package cuenen.raymond.akka.jeromq;

/**
 * A base interface for connection options for a ZeroMQ socket.
 */
public interface SocketConnectOption extends SocketOption {

    String endpoint();
}
