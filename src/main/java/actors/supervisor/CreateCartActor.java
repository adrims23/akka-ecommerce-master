package actors.supervisor;


import actors.service.cassandra.CartCassandraActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import messages.CreateCartRequest;

public class CreateCartActor extends AbstractActor {

    private final ActorRef cartCassandraActor;
    private final Config config;

    public CreateCartActor(Config config) {
        this.config = config;
        cartCassandraActor=getContext().actorOf(FromConfig.getInstance().props(CartCassandraActor.props(config)),"cartCassandraActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(CreateCartRequest.class, message->cartCassandraActor.tell(message, getSender())).build();
    }

    public static Props props(Config config) {
        return Props.create(CreateCartActor.class, () -> new CreateCartActor(config));
    }
}
