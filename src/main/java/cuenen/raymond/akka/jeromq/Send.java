package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Send a message over the zeromq socket.
 */
public class Send implements Request {

    public static Send create(List<ByteString> frames) {
        return new Send(frames);
    }

    public final List<ByteString> frames;

    public Send(List<ByteString> frames) {
        this.frames = Collections.unmodifiableList(frames);
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(frames);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Send) {
            Send that = (Send) obj;
            return Objects.equals(this.frames, that.frames);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Send(%s)", frames);
    }

}
