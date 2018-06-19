package actors.supervisor;

import actors.service.cassandra.CassandraPlanSkusReader;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import jdk.nashorn.internal.parser.JSONParser;
import messages.GetPlanRequest;
import scala.concurrent.duration.Duration;


import java.util.HashMap;
import java.util.Map;

public class GetPlanActor extends AbstractActor {
    private final ActorRef cassandraPlanReaderActor;
    private final Config config;

    public GetPlanActor(Config config) {
        this.config = config;
        this.cassandraPlanReaderActor = getContext().actorOf(FromConfig.getInstance().
                props(CassandraPlanSkusReader.props(config)), "cassandraPlanReaderActor");
    }

    public static Props props(Config config) {
        return Props.create(GetPlanActor.class, () -> new GetPlanActor(config));
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(GetPlanRequest.class, message -> {
                    cassandraPlanReaderActor.tell(message, getSender());
                })

                .build();

    }


    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(NoDataAvailableException.class, e -> SupervisorStrategy.resume()).
                    match(NullPointerException.class, n -> SupervisorStrategy.restart()).
                    match(IllegalArgumentException.class, i -> SupervisorStrategy.stop()).
                    matchAny(o -> SupervisorStrategy.stop()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
