package actors.supervisor;

import actors.service.cassandra.CartCassandraActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import messages.UpdateCartRequest;
import scala.concurrent.duration.Duration;

import java.io.IOException;

public class UpdateCartActor extends AbstractActor {

    private final ActorRef cartCassandraActor;
    private final Config config;

    public UpdateCartActor(Config config){
        this.config=config;
        this.cartCassandraActor = getContext().actorOf(FromConfig.getInstance().
                props(CartCassandraActor.props(config)), "cartCassandraActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateCartRequest.class, message -> {
                    cartCassandraActor.tell(message,getSender());
                })
                .build();
    }

    public static Props props(Config appConfig) {
        return Props.create(UpdateCartActor.class,()->new UpdateCartActor(appConfig));
    }

    public SupervisorStrategy supervisorStrategy(){
        return new OneForOneStrategy(50, Duration.create("1 minute"),
                DeciderBuilder.match(NullPointerException.class, e -> SupervisorStrategy.resume())
                        .match(IOException.class, e -> SupervisorStrategy.resume())
                        .match(Exception.class, e -> SupervisorStrategy.restart())
                        .match(Throwable.class, e -> SupervisorStrategy.restart())
                        .build()
        );
    }
}
