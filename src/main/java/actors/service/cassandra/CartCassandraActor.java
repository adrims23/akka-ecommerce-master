package actors.service.cassandra;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.reflect.TypeToken;
import com.typesafe.config.Config;
import messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartCassandraActor extends AbstractActor {

    private final Config config;
    /* default */ ActorSystem system = context().system();
    public static final TypeToken<Map<String, Map<String, ItemInfo>>> TYPE_TOKEN = new TypeToken<Map<String, Map<String, ItemInfo>>>() {

    };

    /* default */  final LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public CartCassandraActor(Config config) {
        super();
        this.config = config;
    }

    public static Props props(Config config)
    {
        return Props.create(CartCassandraActor.class,()->new CartCassandraActor(config));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetCartListRequest.class, msg ->{
            getCartListDetails(msg);
        }).match(GetCartRequest.class, msg ->{
            getCartDetails(msg);
        }).match(CreateCartRequest.class, msg ->{
            createAndPersisitCart(msg);
        }).match(UpdateCartRequest.class, msg->{
            updateCart(msg);
        }).build();
    }

    private void createAndPersisitCart(CreateCartRequest cartRequest)  {
        try {

            final Session session = SessionManager.getSession();


            PreparedStatement statement = session.prepare("INSERT INTO ecommerce.shoppingcart (account_key, cart_id, cart_status,create_ts,activitylist) VALUES (?,?,?,?,?)");

            BoundStatement boundStatement = statement.bind()
                    .setString("account_key", cartRequest.getAccountKey())
                    .setUUID("cart_id", UUIDs.timeBased())
                    .setString("cart_status", cartRequest.getCartStatus())
                    .setTimestamp("create_ts", new java.util.Date())
                    .set("activitylist", cartRequest.getActivityMap(), TYPE_TOKEN);


            ResultSet result = session.execute(boundStatement);

            if (result != null) {

                getSender().tell("success", ActorRef.noSender());
            }
        } catch (Exception e) {
//            getSender().tell("Some problem while creating cart ", ActorRef.noSender());
            log.info("Some problem ");

        }
    }


    private void getCartDetails(GetCartRequest msg)  {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM SHOPPINGCART where account_key=? and cart_id=?");
        BoundStatement boundStatement = statement.bind(msg.getAccount_id(),msg.getCart_id());
        ResultSet result = session.execute(boundStatement);
//        String message=null;
        if(result==null){
            if(log.isInfoEnabled()) {
                log.info("There are no cart for this account : " + msg.getAccount_id());
            }
        }
        Row cartDetails=result.one();

            GetCartResponse cartResponse=new GetCartResponse(cartDetails.getString("account_key"),
                    cartDetails.getUUID("cart_id"),
                    cartDetails.getString("cart_status"),
                    cartDetails.getTimestamp("create_ts"),
                    cartDetails.getTimestamp("modify_ts"),
                    (Map<String, Map<String, ItemInfo>>) cartDetails.getObject("activitylist"));


        log.info("before result set fetch");
        getSender().tell(cartResponse,ActorRef.noSender());

    }

    private void getCartListDetails(GetCartListRequest msg)  {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM SHOPPINGCART where account_key=?");
        BoundStatement boundStatement = statement.bind(msg.getAccount_id());
        ResultSet result = session.execute(boundStatement);
//        String message=null;

        if(result==null){
            if(log.isInfoEnabled()) {
                log.info("There are no cart for this account : " + msg.getAccount_id());
            }
        }

        List<Row> cartDetails=result.all();
        List<GetCartResponse> cartList=new ArrayList<>();
        cartDetails.forEach(cart -> {
            GetCartResponse cartResponse=new GetCartResponse(cart.getString("account_key"),
                    cart.getUUID("cart_id"),
                    cart.getString("cart_status"),
                    cart.getTimestamp("create_ts"),
                    cart.getTimestamp("modify_ts"),
                    (Map<String, Map<String, ItemInfo>>) cart.getObject("activitylist"));

            cartList.add(cartResponse);




        });

        log.info("before result set fetch");
        getSender().tell(cartList,ActorRef.noSender());

    }

    private void updateCart(UpdateCartRequest message) {
        final Session session = SessionManager.getSession();
        log.info("inside updateCart method in Device Cassandra Actor");
        PreparedStatement statement = session.prepare("UPDATE shoppingcart SET cart_status = ?, modify_ts=?, activitylist=? " +
                "WHERE account_key= ? AND cart_id = ?");
        BoundStatement boundStatement = statement.bind()
                .setString("cart_status", message.getCartStatus())
                .setTimestamp("modify_ts", new java.util.Date())
                .set("activitylist", message.getActivitylist(), TYPE_TOKEN)
                .setString("account_key", message.getAccountId())
                .setUUID("cart_id", message.getCartId());



        session.execute(boundStatement);
        if(log.isInfoEnabled()) {
            log.info("cart with account id " + message.getAccountId() + "has been updated");
        }
        getSender().tell("cart Updated", ActorRef.noSender());
    }
}
