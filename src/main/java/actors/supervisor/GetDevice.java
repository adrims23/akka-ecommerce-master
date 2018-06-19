package actors.supervisor;

import actors.service.cassandra.DeviceCassandraActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import messages.GetDeviceRequest;
import messages.PostDeviceRequest;
import scala.concurrent.duration.Duration;


public class GetDevice extends AbstractActor {

    private final ActorRef deviceCassandraActor;
    private final Config config;

    public static Props props(Config config) {
        return Props.create(GetDevice.class, () -> new GetDevice(config));
    }

//    public GetDevice(ActorRef deviceCassandraActor) {
//        this.deviceCassandraActor = deviceCassandraActor;
//    }


    public GetDevice(Config config) {
        this.config = config;
        this.deviceCassandraActor = getContext().actorOf(FromConfig.getInstance().props(DeviceCassandraActor.props(config)), "deviceCassandraActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetDeviceRequest.class, msg -> {
            deviceCassandraActor.tell(msg, getSender());
        }).match(PostDeviceRequest.class, msg -> {
            deviceCassandraActor.tell(msg, getSender());
        }) .build();
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(NoDataAvailableException.class, e -> SupervisorStrategy.resume()).
                    match(NullPointerException.class, n -> SupervisorStrategy.restart()).
                    match(IllegalArgumentException.class, i -> SupervisorStrategy.stop()).
                    matchAny(o -> SupervisorStrategy.stop()).build());
}
