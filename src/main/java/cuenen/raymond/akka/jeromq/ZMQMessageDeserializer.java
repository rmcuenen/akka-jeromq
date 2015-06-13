package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.List;

/**
 * Deserializes ZeroMQ messages into an immutable sequence of frames.
 */
public class ZMQMessageDeserializer implements Deserializer {

    @Override
    public ZMQMessage create(List<ByteString> frames) {
        return ZMQMessage.create(frames);
    }

}
