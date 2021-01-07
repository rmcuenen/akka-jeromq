package cuenen.raymond.akka.jeromq;

import akka.util.ByteString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A message received over the zeromq socket.
 */
public class ZMQMessage {

    public static interface Converter<T> {

        public ByteString apply(T frame);
    }

    public static final ZMQMessage empty = new ZMQMessage(Collections.<ByteString>emptyList());

    public static ZMQMessage create(List<ByteString> frames) {
        return new ZMQMessage(frames);
    }

    /**
     * Create a message from the given frames.
     *
     * @param frames the frames of the returned ZMQMessage
     * @return a ZMQMessage with the given frames
     */
    public static ZMQMessage create(ByteString... frames) {
        if (frames == null || frames.length == 0) {
            return empty;
        }
        return new ZMQMessage(Arrays.asList(frames));
    }

    @SafeVarargs
    public static <T> ZMQMessage create(Converter<T> converter, T... frames) {
        final List<ByteString> frameList = new ArrayList<>();
        for (T frame : frames) {
            frameList.add(converter.apply(frame));
        }
        return new ZMQMessage(frameList);
    }

    public final List<ByteString> frames;

    public ZMQMessage(List<ByteString> frames) {
        this.frames = Collections.unmodifiableList(frames);
    }

    public ByteString frame(int frameIndex) {
        return frames.get(frameIndex);
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(frames);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZMQMessage) {
            ZMQMessage that = (ZMQMessage) obj;
            return Objects.equals(this.frames, that.frames);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ZMQMessage(%s)", frames);
    }

}
