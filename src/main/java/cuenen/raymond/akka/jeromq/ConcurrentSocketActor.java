package cuenen.raymond.akka.jeromq;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.function.Procedure;
import akka.util.ByteString;
import static cuenen.raymond.akka.jeromq.Response.*;
import static cuenen.raymond.akka.jeromq.SocketOptionQuery.*;
import static cuenen.raymond.akka.jeromq.SocketType.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import scala.Option;
import scala.concurrent.duration.FiniteDuration;

public class ConcurrentSocketActor extends UntypedActor {

    private static interface PollMsg {

    }

    private static final PollMsg Poll = new PollMsg() {

        @Override
        public String toString() {
            return "Poll";
        }

    };

    private static final PollMsg PollCareful = new PollMsg() {

        @Override
        public String toString() {
            return "PollCareful";
        }

    };

    private static final Object Flush = new Object() {

        @Override
        public String toString() {
            return "Flush";
        }

    };

    private static final Context DefaultContext = Context.create();

    public final List<SocketOption> params;
    private final Context zmqContext;
    private Deserializer deserializer;
    private final ZMQSocketType socketType;
    private final Socket socket;
    private final Poller poller;
    private final Option<ActorRef> listenerOp;
    private final List<List<ByteString>> pendingSends = new ArrayList<>();
    private final Procedure<PollMsg> doPollTimeout;

    public ConcurrentSocketActor(List<SocketOption> params) {
        this.params = Collections.unmodifiableList(params);
        Context ctx = null;
        ZMQSocketType type = null;
        ActorRef lst = null;
        for (SocketOption option : params) {
            if (option instanceof Context && ctx == null) {
                ctx = (Context) option;
            } else if (option instanceof Deserializer && deserializer == null) {
                deserializer = (Deserializer) option;
            } else if (option instanceof ZMQSocketType && type == null) {
                type = (ZMQSocketType) option;
            } else if (option instanceof Listener && lst == null) {
                lst = ((Listener) option).listener;
            }
        }
        zmqContext = ctx == null ? DefaultContext : ctx;
        if (deserializer == null) {
            deserializer = new ZMQMessageDeserializer();
        }
        socketType = type;
        if (socketType == null) {
            throw new IllegalArgumentException("A socket type is required");
        }
        socket = zmqContext.socket(socketType);
        poller = zmqContext.poller();
        listenerOp = lst == null ? Option.<ActorRef>empty() : Option.apply(lst);
        doPollTimeout = new Procedure<PollMsg>() {

            private final ZeroMQExtension ext = ZeroMQExtension.get(getContext().system());
            private final FiniteDuration duration;

            {
                FiniteDuration fromConfig = null;
                for (SocketOption option : ConcurrentSocketActor.this.params) {
                    if (option instanceof PollTimeoutDuration) {
                        fromConfig = ((PollTimeoutDuration) option).duration;
                        break;
                    }
                }
                duration = fromConfig == null ? ext.DefaultPollTimeout : fromConfig;
            }

            @Override
            public void apply(PollMsg msg) throws Exception {
                if (duration.gt(FiniteDuration.Zero())) {
                    // for positive timeout values, do poll (i.e. block this thread)
                    final long pollLength = (long) duration.toUnit(ext.pollTimeUnit);
                    poller.poll(pollLength);
                    self().tell(msg, self());
                } else {
                    final FiniteDuration d = (FiniteDuration) duration.neg();
                    // for negative timeout values, schedule Poll token -duration into the future
                    getContext().system().scheduler().scheduleOnce(d, self(), msg, getContext().dispatcher(), self());
                }
            }
        };
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PollMsg) {
            doPoll((PollMsg) message);
        } else if (message instanceof ZMQMessage) {
            handleRequest(Send.create(((ZMQMessage) message).frames));
        } else if (message instanceof Request) {
            handleRequest((Request) message);
        } else if (Flush.equals(message)) {
            flush();
        } else if (message instanceof Terminated) {
            getContext().stop(self());
        } else {
            unhandled(message);
        }
    }

    private void handleRequest(Request msg) throws Exception {
        if (msg instanceof Send) {
            List<ByteString> frames = ((Send) msg).frames;
            if (!frames.isEmpty()) {
                final boolean flushNow = pendingSends.isEmpty();
                pendingSends.add(frames);
                if (flushNow) {
                    flush();
                }
            }
        } else if (msg instanceof SocketOption) {
            handleSocketOption((SocketOption) msg);
        } else if (msg instanceof SocketOptionQuery) {
            handleSocketOptionQuery((SocketOptionQuery) msg);
        } else {
            throw noMatch(msg);
        }
    }

    private void handleConnectOption(SocketConnectOption msg) throws Exception {
        if (msg instanceof Connect) {
            socket.connect(msg.endpoint());
            notifyListener(Connecting);
        } else if (msg instanceof Bind) {
            socket.bind(msg.endpoint());
        } else {
            throw noMatch(msg);
        }
    }

    private void handlePubSubOption(PubSubOption msg) throws Exception {
        if (msg instanceof Subscribe) {
            socket.subscribe(msg.payload().toArray());
        } else if (msg instanceof Unsubscribe) {
            socket.unsubscribe(msg.payload().toArray());
        } else {
            throw noMatch(msg);
        }
    }

    private void handleSocketOption(SocketOption msg) throws Exception {
        if (msg instanceof SocketMeta) {
            throw new IllegalStateException("SocketMeta " + msg + " only allowed for setting up a socket");
        } else if (msg instanceof SocketConnectOption) {
            handleConnectOption((SocketConnectOption) msg);
        } else if (msg instanceof PubSubOption) {
            handlePubSubOption((PubSubOption) msg);
        } else if (msg instanceof Linger) {
            socket.setLinger(((Linger) msg).value);
        } else if (msg instanceof ReconnectIVL) {
            socket.setReconnectIVL(((ReconnectIVL) msg).value);
        } else if (msg instanceof Backlog) {
            socket.setBacklog(((Backlog) msg).value);
        } else if (msg instanceof ReconnectIVLMax) {
            socket.setReconnectIVLMax(((ReconnectIVLMax) msg).value);
        } else if (msg instanceof MaxMsgSize) {
            socket.setMaxMsgSize(((MaxMsgSize) msg).value);
        } else if (msg instanceof SendHighWatermark) {
            socket.setSndHWM(((SendHighWatermark) msg).value);
        } else if (msg instanceof ReceiveHighWatermark) {
            socket.setRcvHWM(((ReceiveHighWatermark) msg).value);
        } else if (msg instanceof HighWatermark) {
            socket.setHWM(((HighWatermark) msg).value);
        } else if (msg instanceof Swap) {
            socket.setSwap(((Swap) msg).value);
        } else if (msg instanceof Affinity) {
            socket.setAffinity(((Affinity) msg).value);
        } else if (msg instanceof Identity) {
            socket.setIdentity(((Identity) msg).value);
        } else if (msg instanceof Rate) {
            socket.setRate(((Rate) msg).value);
        } else if (msg instanceof RecoveryInterval) {
            socket.setRecoveryInterval(((RecoveryInterval) msg).value);
        } else if (msg instanceof MulticastLoop) {
            socket.setMulticastLoop(((MulticastLoop) msg).value);
        } else if (msg instanceof MulticastHops) {
            socket.setMulticastHops(((MulticastHops) msg).value);
        } else if (msg instanceof SendBufferSize) {
            socket.setSendBufferSize(((SendBufferSize) msg).value);
        } else if (msg instanceof ReceiveBufferSize) {
            socket.setReceiveBufferSize(((ReceiveBufferSize) msg).value);
        } else if (msg instanceof Deserializer) {
            deserializer = (Deserializer) msg;
        } else {
            throw noMatch(msg);
        }
    }

    private void handleSocketOptionQuery(SocketOptionQuery msg) throws Exception {
        Object message;
        if (Linger.equals(msg)) {
            message = socket.getLinger();
        } else if (ReconnectIVL.equals(msg)) {
            message = socket.getReconnectIVL();
        } else if (Backlog.equals(msg)) {
            message = socket.getBacklog();
        } else if (ReconnectIVLMax.equals(msg)) {
            message = socket.getReconnectIVLMax();
        } else if (MaxMsgSize.equals(msg)) {
            message = socket.getMaxMsgSize();
        } else if (SendHighWatermark.equals(msg)) {
            message = socket.getSndHWM();
        } else if (ReceiveHighWatermark.equals(msg)) {
            message = socket.getRcvHWM();
        } else if (Swap.equals(msg)) {
            message = socket.getSwap();
        } else if (Affinity.equals(msg)) {
            message = socket.getAffinity();
        } else if (Identity.equals(msg)) {
            message = socket.getIdentity();
        } else if (Rate.equals(msg)) {
            message = socket.getRate();
        } else if (RecoveryInterval.equals(msg)) {
            message = socket.getRecoveryInterval();
        } else if (MulticastLoop.equals(msg)) {
            message = socket.hasMulticastLoop();
        } else if (MulticastHops.equals(msg)) {
            message = socket.getMulticastHops();
        } else if (SendBufferSize.equals(msg)) {
            message = socket.getSendBufferSize();
        } else if (ReceiveBufferSize.equals(msg)) {
            message = socket.getReceiveBufferSize();
        } else if (FileDescriptor.equals(msg)) {
            message = socket.getFD();
        } else {
            throw noMatch(msg);
        }
        sender().tell(message, self());
    }

    @Override
    public void preStart() throws Exception {
        watchListener();
        setupSocket();
        poller.register(socket, Poller.POLLIN);
        setupConnection();
        if (Pub.equals(socketType) || Push.equals(socketType)) {
            // don't pull
        } else if (Sub.equals(socketType) || Pull.equals(socketType)
                || Pair.equals(socketType) || Dealer.equals(socketType)
                || Router.equals(socketType)) {
            self().tell(Poll, self());
        } else if (Req.equals(socketType) || Rep.equals(socketType)) {
            self().tell(PollCareful, self());
        } else {
            throw noMatch(socketType);
        }
    }

    private void setupConnection() throws Exception {
        for (SocketOption option : params) {
            if (option instanceof SocketConnectOption
                    || option instanceof PubSubOption) {
                self().tell(option, self());
            }
        }
    }

    private void setupSocket() throws Exception {
        for (SocketOption option : params) {
            if (option instanceof SocketConnectOption
                    || option instanceof PubSubOption
                    || option instanceof SocketMeta) {
                // ignore, handled differently
                continue;
            }
            self().tell(option, self());
        }
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        for (ActorRef child : getContext().getChildren()) {
            getContext().stop(child);
        }
        //Do not call postStop
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        // Do nothing
    }

    @Override
    public void postStop() throws Exception {
        try {
            if (socket != null) {
                poller.unregister(socket);
                socket.close();
            }
        } finally {
            notifyListener(Closed);
        }
    }

    private boolean flushMessage(List<ByteString> i) throws Exception {
        if (i.isEmpty()) {
            return true;
        }
        final ByteString head = i.get(0);
        final List<ByteString> tail = Collections.unmodifiableList(i.subList(1, i.size()));
        if (socket.send(head.toArray(), tail.isEmpty() ? 0 : ZMQ.SNDMORE)) {
            return flushMessage(tail);
        }
        pendingSends.add(0, i); // Reenqueue the rest of the message so the next flush takes care of it
        self().tell(Flush, self());
        return false;
    }

    private void flush() throws Exception {
        if (!pendingSends.isEmpty() && flushMessage(pendingSends.remove(0))) {
            flush(); // Flush while things are going well
        }
    }

    private void doPoll(PollMsg mode) throws Exception {
        doPoll(mode, 10);
    }

    private void doPoll(PollMsg mode, int togo) throws Exception {
        if (togo <= 0) {
            self().tell(mode, self());
        } else {
            final List<ByteString> frames = receiveMessage(mode);
            if (frames.isEmpty()) {
                doPollTimeout.apply(mode);
            } else {
                notifyListener(deserializer.create(frames));
                doPoll(mode, togo - 1);
            }
        }
    }

    private List<ByteString> receiveMessage(PollMsg mode) throws Exception {
        return receiveMessage(mode, Collections.<ByteString>emptyList());
    }

    private List<ByteString> receiveMessage(PollMsg mode, List<ByteString> currentFrames) throws Exception {
        if (PollCareful.equals(mode) && (poller.poll(0) <= 0)) {
            if (currentFrames.isEmpty()) {
                return currentFrames;
            }
            throw new IllegalStateException("Received partial transmission!");
        }
        final byte[] bytes = socket.recv(Poll.equals(mode) ? ZMQ.NOBLOCK : 0);
        if (bytes == null) {
            /*EAGAIN*/
            if (currentFrames.isEmpty()) {
                return currentFrames;
            }
            return receiveMessage(mode, currentFrames);
        }
        List<ByteString> frames = new ArrayList<>(currentFrames);
        frames.add(ByteString.fromArray(bytes));
        if (socket.hasReceiveMore()) {
            return receiveMessage(mode, Collections.unmodifiableList(frames));
        }
        return Collections.unmodifiableList(frames);
    }

    private void watchListener() throws Exception {
        if (listenerOp.isDefined()) {
            getContext().watch(listenerOp.get());
        }
    }

    private void notifyListener(Object message) throws Exception {
        if (listenerOp.isDefined()) {
            listenerOp.get().tell(message, self());
        }
    }

    public static RuntimeException noMatch(final Object obj) {
        return new RuntimeException() {
            private String objString = null;

            @Override
            public String getMessage() {
                if (objString == null) {
                    createObjString();
                }
                return objString;
            }

            private void createObjString() {
                if (obj == null) {
                    objString = "null";
                } else {
                    final String ofClass = "of class " + obj.getClass().getName();
                    try {
                        objString = obj.toString() + " (" + ofClass + ")";
                    } catch (Throwable t) {
                        objString = "an instance " + ofClass;
                    }
                }
            }

            @Override
            public String toString() {
                String s = "MatchError";
                String message = getLocalizedMessage();
                return (message != null) ? (s + ": " + message) : s;
            }

        };
    }
}
