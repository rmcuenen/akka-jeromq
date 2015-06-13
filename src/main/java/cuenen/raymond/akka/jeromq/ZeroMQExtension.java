package cuenen.raymond.akka.jeromq;

import akka.actor.AbstractExtensionId;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import static akka.actor.SupervisorStrategy.*;
import akka.actor.UntypedActor;
import akka.dispatch.RequiresMessageQueue;
import akka.dispatch.UnboundedMessageQueueSemantics;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.util.Timeout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * The extension for the ZeroMQ module
 * <p>
 * {@code system} - The ActorSystem this extension belongs to.
 */
public final class ZeroMQExtension implements Extension {

    public static ZeroMQExtension get(ActorSystem system) {
        return ZeroMQExtensionId.get(system);
    }

    private static final ZeroMQExtensionId ZeroMQExtensionId = new ZeroMQExtensionId();

    /**
     * The {@link akka.actor.ExtensionId} and
     * {@link akka.actor.ExtensionIdProvider} for the ZeroMQ module.
     */
    private static final class ZeroMQExtensionId extends AbstractExtensionId<ZeroMQExtension> implements ExtensionIdProvider {

        private final String minVersionString = "2.1.0";
        private final int minVersion = ZMQ.makeVersion(2, 1, 0);

        @Override
        public ZeroMQExtensionId lookup() {
            return this;
        }

        @Override
        public ZeroMQExtension createExtension(ExtendedActorSystem system) {
            return new ZeroMQExtension(system);
        }

    }

    public final FiniteDuration DefaultPollTimeout;
    public final Timeout NewSocketTimeout;
    public final TimeUnit pollTimeUnit;
    private final ActorRef zeromqGuardian;

    public ZeroMQExtension(ActorSystem system) {
        DefaultPollTimeout = FiniteDuration.create(system.settings().config()
                .getDuration("akka.zeromq.poll-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        NewSocketTimeout = Timeout.apply(FiniteDuration.create(system.settings().config()
                .getDuration("akka.zeromq.new-socket-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
        pollTimeUnit = version().major >= 3 ? TimeUnit.MILLISECONDS : TimeUnit.MICROSECONDS;
        verifyZeroMQVersion();
        zeromqGuardian = system.actorOf(Props.create(ZeroMQGuardian.class), "zeromq");
    }

    /**
     * The version of the ZeroMQ library.
     *
     * @return a {@link cuenen.raymond.akka.jeromq.ZeroMQVersion}
     */
    public ZeroMQVersion version() {
        return ZeroMQVersion.create(ZMQ.getMajorVersion(), ZMQ.getMinorVersion(), ZMQ.getPatchVersion());
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build the ZeroMQ
     * socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newSocketProps(SocketOption... socketParameters) {
        verifyZeroMQVersion();
        boolean exists = false;
        for (SocketOption s : socketParameters) {
            if (s instanceof SocketType.ZMQSocketType) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new IllegalArgumentException("requirement failed: A socket type is required");
        }
        final List<SocketOption> params = Collections.unmodifiableList(Arrays.asList(socketParameters));
        return Props.create(ConcurrentSocketActor.class, params).withDispatcher("akka.zeromq.socket-dispatcher");
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Publisher socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newPubSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Pub;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Subscriber socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newSubSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Sub;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Dealer socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newDealerSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Dealer;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Router socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newRouterSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Router;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Push socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newPushSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Push;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Pull socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newPullSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Pull;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Req socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newReqSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Req;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the {@link akka.actor.Props} to build a ZeroMQ
     * Rep socket actor.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.Props}
     */
    public Props newRepSocketProps(SocketOption... socketParameters) {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Rep;
        return newSocketProps(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ socket. You
     * can pass in as many configuration options as you want and the order of
     * the configuration options doesn't matter They are matched on type and the
     * first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newSocket(SocketOption... socketParameters) throws Exception {
        return (ActorRef) Await.result(
                Patterns.ask(zeromqGuardian, newSocketProps(socketParameters), NewSocketTimeout),
                NewSocketTimeout.duration());
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Publisher
     * socket. You can pass in as many configuration options as you want and the
     * order of the configuration options doesn't matter They are matched on
     * type and the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newPubSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Pub;
        return newSocket(options);
    }

    /**
     * Convenience for creating a publisher socket.
     */
    public ActorRef newPubSocket(Bind bind) throws Exception {
        return newSocket(SocketType.Pub, bind);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Subscriber
     * socket. You can pass in as many configuration options as you want and the
     * order of the configuration options doesn't matter They are matched on
     * type and the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newSubSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Sub;
        return newSocket(options);
    }

    /**
     * Convenience for creating a subscriber socket.
     */
    public ActorRef newSubSocket(Connect connect, Listener listener, Subscribe subscribe) throws Exception {
        return newSocket(SocketType.Sub, connect, listener, subscribe);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Dealer socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newDealerSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Dealer;
        return newSocket(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Router socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newRouterSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Router;
        return newSocket(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Push socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newPushSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Push;
        return newSocket(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Pull socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newPullSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Pull;
        return newSocket(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Req socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newReqSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Req;
        return newSocket(options);
    }

    /**
     * Factory method to create the actor representing the ZeroMQ Rep socket.
     * You can pass in as many configuration options as you want and the order
     * of the configuration options doesn't matter They are matched on type and
     * the first one found wins.
     *
     * @param socketParameters a varargs list of
     * {@link cuenen.raymond.akka.jeromq.SocketOption} to configure the socket
     * @return the {@link akka.actor.ActorRef}
     */
    public ActorRef newRepSocket(SocketOption... socketParameters) throws Exception {
        final SocketOption[] options = new SocketOption[socketParameters.length + 1];
        System.arraycopy(socketParameters, 0, options, 1, socketParameters.length);
        options[0] = SocketType.Rep;
        return newSocket(options);
    }

    private void verifyZeroMQVersion() {
        if (ZMQ.getFullVersion() <= ZeroMQExtensionId.minVersion) {
            throw new IllegalArgumentException(
                    String.format("requirement failed: Unsupported ZeroMQ version: %s, akka needs at least: %s",
                            ZMQ.getVersionString(), ZeroMQExtensionId.minVersionString));
        }
    }

    private static class ZeroMQGuardian extends UntypedActor implements RequiresMessageQueue<UnboundedMessageQueueSemantics> {

        private final SupervisorStrategy supervisorStrategy = new OneForOneStrategy(-1, Duration.Inf(), new Function<Throwable, SupervisorStrategy.Directive>() {

            @Override
            public Directive apply(Throwable ex) throws Exception {
                if (ex instanceof ZMQException && nonfatal((ZMQException) ex)) {
                    return resume();
                }
                return stop();
            }
        }, true);

        @Override
        public SupervisorStrategy supervisorStrategy() {
            return supervisorStrategy;
        }

        private boolean nonfatal(ZMQException ex) {
            switch (ex.getErrorCode()) {
                case zmq.ZError.EFSM:
                case zmq.ZError.ENOTSUP:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Props) {
                sender().tell(getContext().actorOf((Props) message), self());
            } else {
                unhandled(message);
            }
        }

    }
}
