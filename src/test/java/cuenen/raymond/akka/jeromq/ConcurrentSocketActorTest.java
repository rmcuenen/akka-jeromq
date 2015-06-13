package cuenen.raymond.akka.jeromq;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.JavaPartialFunction;
import akka.testkit.JavaTestKit;
import akka.testkit.TestKitExtension;
import akka.testkit.TestProbe;
import akka.util.ByteString;
import akka.util.Timeout;
import static cuenen.raymond.akka.jeromq.Response.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import scala.collection.JavaConversions;
import scala.concurrent.duration.Duration;

public class ConcurrentSocketActorTest extends JavaTestKit {

    /**
     * Convenience accessor to subscribe to all events
     */
    public static final Subscribe SubscribeAll = Subscribe.all;
    public static final ActorSystem system = ActorSystem.create("ConcurrentSocketActorTest");
    public final LoggingAdapter log = Logging.getLogger(system, this.getClass());
    public final Timeout timeout = Timeout.apply(duration("15 seconds"));
    public final List<String> endpoints = new ArrayList<>();

    public ConcurrentSocketActorTest() {
        super(system);
        log.info("ConcurrentSocketActor should");
    }

    @AfterClass
    public static void afterAll() throws Exception {
        JavaTestKit.shutdownActorSystem(system,
                duration("5 seconds").mul(TestKitExtension.get(system).TestTimeFactor()).min(duration("10 seconds")),
                Boolean.FALSE);
    }

    public void checkZeroMQInstallation() {
        try {
            ZeroMQVersion version = zmq().version();
            if (version.major >= 3 || (version.major >= 2 && version.minor >= 1)) {
                return;
            }
            invalidZeroMQVersion(version);
        } catch (LinkageError e) {
            zeroMQNotInstalled();
        }
    }

    public void invalidZeroMQVersion(ZeroMQVersion version) {
        final String message = String.format("WARNING: The tests are not run because invalid ZeroMQ version: %s. Version >= 2.1.x required.", version);
        log.info(message);
        throw new AssumptionViolatedException(message);
    }

    public void zeroMQNotInstalled() {
        final String message = "WARNING: The tests are not run because ZeroMQ is not installed. Version >= 2.1.x required.";
        log.info(message);
        throw new AssumptionViolatedException(message);
    }

    public String endpoints(int index) throws Exception {
        if (endpoints.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                try (ServerSocket socket = new ServerSocket(0)) {
                    endpoints.add("tcp://127.0.0.1:" + socket.getLocalPort());
                }
            }
        }
        return endpoints.get(index);
    }

    public ZeroMQExtension zmq() {
        return ZeroMQExtension.get(system);
    }

    @Test
    public void supportPubSubConnections() throws Exception {
        log.info("support pub-sub connections in");
        final TestProbe subscriberProbe = TestProbe.apply(system);
        final Context context = Context.create();
        final String endpoint = endpoints(0);
        final ActorRef publisher = zmq().newSocket(SocketType.Pub, context, Bind.create(endpoint));
        final ActorRef subscriber = zmq().newSocket(SocketType.Sub, context, Listener.create(subscriberProbe.ref()),
                Connect.create(endpoint), SubscribeAll);
        final Cancellable msgGenerator = system.scheduler().schedule(
                duration("100 millis"),
                duration("10 millis"),
                new Runnable() {

                    public int number;

                    @Override
                    public void run() {
                        publisher.tell(ZMQMessage.create(ByteString.fromString(String.valueOf(number)),
                                        ByteString.empty()), getRef());
                        number += 1;
                    }
                },
                system.dispatcher());
        try {
            subscriberProbe.expectMsg(Connecting);
            List<Integer> msgNumbers = JavaConversions.seqAsJavaList(subscriberProbe.receiveWhile(duration("3 seconds"), Duration.Inf(), Integer.MAX_VALUE,
                    new JavaPartialFunction<Object, Integer>() {

                        @Override
                        public Integer apply(Object in, boolean isCheck) throws Exception {
                            if (in instanceof ZMQMessage) {
                                ZMQMessage msg = (ZMQMessage) in;
                                if (msg.frames.size() == 2) {
                                    if (isCheck) {
                                        return null;
                                    }
                                    assertEquals("length should be(0)", 0, msg.frames.get(1).length());
                                    return Integer.parseInt(msg.frames.get(0).utf8String());
                                }
                            }
                            throw noMatch();
                        }
                    }));
            assertTrue("length should be > 0", msgNumbers.size() > 0);
            final List<Integer> list = new ArrayList<>();
            for (int i = msgNumbers.get(0); i <= msgNumbers.get(msgNumbers.size() - 1); i++) {
                list.add(i);
            }
            assertEquals("msgNumbers should be(...)", list, msgNumbers);
        } finally {
            msgGenerator.cancel();
            watch(subscriber);
            system.stop(subscriber);
            Object msg = subscriberProbe.receiveWhile(duration("3 seconds"), Duration.Inf(), Integer.MAX_VALUE,
                    new JavaPartialFunction<Object, Object>() {

                        @Override
                        public Object apply(Object in, boolean isCheck) throws Exception {
                            return in;
                        }
                    }).last();
            assertEquals(Closed, msg);
            expectTerminated(duration("5 seconds"), subscriber);
            watch(publisher);
            system.stop(publisher);
            expectTerminated(duration("5 seconds"), publisher);
            context.term();
        }
    }

    @Test
    public void supportReqRepConnections() throws Exception {
        log.info("support req-rep connections in");
        checkZeroMQInstallation();
        final TestProbe requesterProbe = TestProbe.apply(system);
        final TestProbe replierProbe = TestProbe.apply(system);
        final Context context = Context.create();
        final String endpoint = endpoints(1);
        final ActorRef requester = zmq().newSocket(SocketType.Req, context,
                Listener.create(requesterProbe.ref()), Bind.create(endpoint));
        final ActorRef replier = zmq().newSocket(SocketType.Rep, context,
                Listener.create(replierProbe.ref()), Connect.create(endpoint));
        try {
            replierProbe.expectMsg(Connecting);
            final ZMQMessage request = ZMQMessage.create(ByteString.fromString("Request"));
            final ZMQMessage reply = ZMQMessage.create(ByteString.fromString("Reply"));
            requester.tell(request, getRef());
            replierProbe.expectMsg(request);
            replier.tell(reply, getRef());
            requesterProbe.expectMsg(reply);
        } finally {
            watch(replier);
            system.stop(replier);
            replierProbe.expectMsg(Closed);
            expectTerminated(duration("5 seconds"), replier);
            watch(requester);
            system.stop(requester);
            expectTerminated(duration("5 seconds"), requester);
            context.term();
        }
    }

    @Test
    public void supportPushPullConnections() throws Exception {
        log.info("support push-pull connections in");
        checkZeroMQInstallation();
        final TestProbe pullerProbe = TestProbe.apply(system);
        final Context context = Context.create();
        final String endpoint = endpoints(2);
        final ActorRef pusher = zmq().newSocket(SocketType.Push, context, Bind.create(endpoint));
        final ActorRef puller = zmq().newSocket(SocketType.Pull, context,
                Listener.create(pullerProbe.ref()), Connect.create(endpoint));
        try {
            pullerProbe.expectMsg(Connecting);
            final ZMQMessage message = ZMQMessage.create(ByteString.fromString("Pushed message"));
            pusher.tell(message, getRef());
            pullerProbe.expectMsg(message);
        } finally {
            watch(puller);
            system.stop(puller);
            pullerProbe.expectMsg(Closed);
            expectTerminated(duration("5 seconds"), puller);
            watch(pusher);
            system.stop(pusher);
            expectTerminated(duration("5 seconds"), pusher);
            context.term();
        }
    }
}
