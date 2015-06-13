package cuenen.raymond.akka.jeromq;

import akka.actor.ActorRef;
import java.util.Objects;

/**
 * An option containing the listener for the socket.
 */
public class Listener implements SocketMeta {

    public static Listener create(ActorRef listener) {
        return new Listener(listener);
    }

    public final ActorRef listener;

    public Listener(ActorRef listener) {
        this.listener = listener;
    }

    @Override
    public int hashCode() {
        return 41 + Objects.hashCode(listener);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Listener) {
            Listener that = (Listener) obj;
            return Objects.equals(this.listener, that.listener);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Listener(%s)", listener);
    }

}
