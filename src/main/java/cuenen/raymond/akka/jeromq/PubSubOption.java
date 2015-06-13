package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;

/**
 * A base interface for pubsub options for the ZeroMQ socket.
 */
public interface PubSubOption extends SocketOption {

    ByteString payload();
}
