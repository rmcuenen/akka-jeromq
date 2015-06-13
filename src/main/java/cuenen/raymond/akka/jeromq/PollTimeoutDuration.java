package cuenen.raymond.akka.jeromq;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.FiniteDuration;

/**
 * An option containing the duration a poll cycle should wait for a message
 * before it loops.
 */
public class PollTimeoutDuration implements SocketMeta {

    public static PollTimeoutDuration create() {
        return new PollTimeoutDuration(FiniteDuration.create(100, TimeUnit.MILLISECONDS));
    }

    public static PollTimeoutDuration create(FiniteDuration duration) {
        return new PollTimeoutDuration(duration);
    }

    public final FiniteDuration duration;

    public PollTimeoutDuration(FiniteDuration duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PollTimeoutDuration) {
            PollTimeoutDuration that = (PollTimeoutDuration) obj;
            return Objects.equals(this.duration, that.duration);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("PollTimeoutDuration(%s)", duration);
    }

}
