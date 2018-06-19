package actors.service.cassandra;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.datastax.driver.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
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

    private void deleteCart(String accountId, UUID cartId) {
        final Session session = SessionManager.getSession();
        log.info("inside deleteCart");
        PreparedStatement statement = session.prepare("SELECT * FROM SHOPPINGCART where account_key=? and cart_id=?");
        BoundStatement boundStatement = statement.bind(accountId,cartId);
        ResultSet result = session.execute(boundStatement);
        if(result.isExhausted()){
            log.info("There are no cart for this account : "+accountId);
        } else {
            PreparedStatement delStatement = session.prepare("DELETE FROM shoppingcart WHERE account_key = ? and  cart_id = ?");
            BoundStatement delBoundStatement = delStatement.bind(accountId, cartId);
            session.execute(delBoundStatement);
        }

        log.info("before result set fetch");
        String jsonInString="deleted successfully";
        getSender().tell(jsonInString,ActorRef.noSender());

    }
}
