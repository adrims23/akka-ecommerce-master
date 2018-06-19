package actors.supervisor;


import actors.service.cassandra.CartCassandraActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;

import java.lang.IllegalArgumentException;

import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import messages.CreateCartRequest;
import scala.concurrent.duration.Duration;

public class CreateCartActor extends AbstractActor {

    private final ActorRef cartCassandraActor;
    private final Config config;

    public CreateCartActor(Config config) {
        super();
        this.config = config;
        cartCassandraActor = getContext().actorOf(FromConfig.getInstance().props(CartCassandraActor.props(config)), "cartCassandraActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateCartRequest.class, message -> cartCassandraActor.tell(message, getSender()))
                .build();
    }

    public static Props props(Config config) {
        return Props.create(CreateCartActor.class, () -> new CreateCartActor(config));
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(NoDataAvailableException.class, e -> SupervisorStrategy.resume()).
                    match(NullPointerException.class, n -> SupervisorStrategy.restart()).
                    match(IllegalArgumentException.class, i -> SupervisorStrategy.stop()).
                    matchAny(o -> SupervisorStrategy.stop()).build());
}
