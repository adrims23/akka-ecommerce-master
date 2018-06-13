package actors.service.cassandra;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import messages.DeleteCartRequest;

import java.util.List;
import java.util.UUID;

public class DeleteCartCassandraActor extends AbstractActor {

    private final Config appConfig;

    public DeleteCartCassandraActor(Config appConfig) {
        this.appConfig = appConfig;
    }
    ActorSystem system = context().system();
    final LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public static Props props(Config config) {
        return Props.create(DeleteCartCassandraActor.class,
                () -> new DeleteCartCassandraActor(config));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DeleteCartRequest.class, msg ->{
            deleteCart(msg.getAccountId(),msg.getCartId());
        }).build();
    }

    private void deleteCart(String accountId, UUID cartId) throws JsonProcessingException {
        final Session session = SessionManager.getSession();
        log.info("inside deleteCart");
        PreparedStatement statement = session.prepare("DELETE FROM shoppingcart WHERE account_key = ? and  cart_id = ?");
        BoundStatement boundStatement = statement.bind(accountId, cartId);
        ResultSet result = session.execute(boundStatement);
        Object message=null;

        log.info("before result set fetch");

//        List<Row> deletionResult=result.all();




        ObjectMapper mapper = new ObjectMapper();
        String jsonInString=null;
//        if(deletionResult.size()>0)
            jsonInString="deleted successfully";


        getSender().tell(jsonInString,ActorRef.noSender());

    }
}
