package cuenen.raymond.akka.jeromq;

import org.zeromq.ZMQ;

/**
 * Represents an I/O thread pool for ZeroMQ sockets. By default the ZeroMQ
 * module uses an I/O thread pool with 1 thread. For most applications that
 * should be sufficient.
 */
public class Context implements SocketMeta {

    public static Context create() {
        return new Context(1);
    }

    public static Context create(int numIoThreads) {
        return new Context(numIoThreads);
    }

    private final org.zeromq.ZMQ.Context context;

    public Context(int numIoThreads) {
        context = ZMQ.context(numIoThreads);
    }

    public ZMQ.Socket socket(SocketType.ZMQSocketType socketType) {
        return context.socket(socketType.id);
    }

    public ZMQ.Poller poller() {
        return context.poller();
    }

    public void term() {
        context.term();
    }
}
