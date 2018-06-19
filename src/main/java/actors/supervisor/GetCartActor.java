package actors.supervisor;

import actors.service.cassandra.CartCassandraActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.IllegalArgumentException;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import messages.GetCartListRequest;
import messages.GetCartRequest;
import scala.concurrent.duration.Duration;


import java.io.IOException;

public class GetCartActor extends AbstractActor {
    private final ActorRef cartCassandraActor;
    private final Config config;

    public GetCartActor(Config config) {
        this.config = config;
        this.cartCassandraActor = getContext().actorOf(FromConfig.getInstance().
                props(CartCassandraActor.props(config)), "cartCassandraActor");
    }

    public static Props props(Config config) {
        return Props.create(GetCartActor.class, () -> new GetCartActor(config));
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetCartListRequest.class, message -> {
                    cartCassandraActor.tell(message, getSender());
                })
                .match(GetCartRequest.class, message -> {
                    cartCassandraActor.tell(message, getSender());
                })

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
