package actors.supervisor;

import actors.service.cassandra.DeviceCassandraActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import messages.GetDeviceRequest;
import messages.PostDeviceRequest;

public class GetDevice extends AbstractActor {

    private final ActorRef deviceCassandraActor;
    private final Config config;

    public static Props props(Config config)
    {
        return Props.create(GetDevice.class,()->new GetDevice(config));
    }

//    public GetDevice(ActorRef deviceCassandraActor) {
//        this.deviceCassandraActor = deviceCassandraActor;
//    }


    public GetDevice(Config config) {
        this.config = config;
        this.deviceCassandraActor=getContext().actorOf(FromConfig.getInstance().props(DeviceCassandraActor.props(config)),"deviceCassandraActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetDeviceRequest.class, msg ->{
//            Patterns.ask(deviceCassandraActor,"all",10000);
            deviceCassandraActor.tell(msg,getSender());
        }).match(PostDeviceRequest.class, msg ->{
            deviceCassandraActor.tell(msg,getSender());
        }).build();
    }
}
