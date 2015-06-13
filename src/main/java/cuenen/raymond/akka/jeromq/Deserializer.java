package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.List;

/**
 * A base interface for message deserializers.
 */
public interface Deserializer extends SocketOption {

    Object create(List<ByteString> frames);
}
