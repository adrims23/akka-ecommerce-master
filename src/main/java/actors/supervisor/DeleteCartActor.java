package actors.supervisor;

import actors.service.cassandra.CassandraDataReaderActor;
import actors.service.cassandra.DeleteCartCassandraActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.typesafe.config.Config;
import messages.DeleteCartRequest;
import messages.GetCartRequest;
import scala.concurrent.duration.Duration;


import java.io.IOException;

public class DeleteCartActor extends AbstractActor {
    private final ActorRef deleteCartCassandraActor;
    private final Config config;

    public DeleteCartActor(Config config) {
        this.config = config;
        this.deleteCartCassandraActor = getContext().actorOf(FromConfig.getInstance().
                props(DeleteCartCassandraActor.props(config)), "deleteCartCassandraActor");
    }

    public static Props props(Config config) {
        return Props.create(DeleteCartActor.class, () -> new DeleteCartActor(config));
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeleteCartRequest.class, message -> {
                    deleteCartCassandraActor.tell(message, getSender());})
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(50, Duration.create("1 minute"),
                DeciderBuilder.match(NullPointerException.class, e -> SupervisorStrategy.resume())
                        .match(IOException.class, e -> SupervisorStrategy.resume())
                        .match(Exception.class, e -> SupervisorStrategy.restart())
                        .match(Throwable.class, e -> SupervisorStrategy.restart())
                        .build()
        );
    }

}
