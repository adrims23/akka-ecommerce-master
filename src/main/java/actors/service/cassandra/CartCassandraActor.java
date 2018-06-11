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
import java.lang.IllegalArgumentException;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartCassandraActor extends AbstractActor {

    private final Config config;
    ActorSystem system = context().system();
    public static final TypeToken<Map<String, Map<String, ItemInfo>>> TYPE_TOKEN = new TypeToken<Map<String, Map<String, ItemInfo>>>() {

    };

    final LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public CartCassandraActor(Config config) {
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

    private void createAndPersisitCart(CreateCartRequest cartRequest) throws Exception {
        try {

            final Session session = SessionManager.getSession();
            if(validate(cartRequest)){
                throw new IllegalArgumentException("Mandatory fields Missing");
            }

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
            } else {
                getSender().tell("Some problem while creating cart ", ActorRef.noSender());
                //throw new NoDataAvailableException("There are no Plans available right now");
            }
        } catch (Exception e) {
            getSender().tell("Some problem while creating cart ", ActorRef.noSender());
            throw new Exception(e);
        }
    }

    private Boolean validate(CreateCartRequest cartRequest) {
        if(cartRequest.getAccountKey()==null || cartRequest.getActivityMap()==null){
            return false;
        }
        return true;
    }

    private void getCartDetails(GetCartRequest msg) throws NoDataAvailableException {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM SHOPPINGCART where account_key=? and cart_id=?");
        BoundStatement boundStatement = statement.bind(msg.getAccount_id(),msg.getCart_id());
        ResultSet result = session.execute(boundStatement);
        String message=null;
        if(result==null){
            //getSender().tell("There are no Plans available right now ", ActorRef.noSender());
            throw new NoDataAvailableException("There are no cart for this account : "+msg.getAccount_id());
        }
//        GetCartResponse cartResponse=new GetCartResponse();
        Row cartDetails=result.one();

            GetCartResponse cartResponse=new GetCartResponse();
            cartResponse.setAccount_key(cartDetails.getString("account_key"));
            cartResponse.setCart_id(cartDetails.getUUID("cart_id"));
            cartResponse.setCart_status(cartDetails.getString("cart_status"));
            cartResponse.setCreate_ts(cartDetails.getTimestamp("create_ts"));
            cartResponse.setModify_ts(cartDetails.getTimestamp("modify_ts"));
            if (null != cartDetails) {
                Map<String, Map<String, ItemInfo>> activityMap = (Map<String, Map<String, ItemInfo>>) cartDetails.getObject("activitylist");
                cartResponse.setActivitylist(activityMap);
            }

        log.info("before result set fetch");
        getSender().tell(cartResponse,ActorRef.noSender());

    }

    private void getCartListDetails(GetCartListRequest msg) throws NoDataAvailableException {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM SHOPPINGCART where account_key=?");
        BoundStatement boundStatement = statement.bind(msg.getAccount_id());
        ResultSet result = session.execute(boundStatement);
        String message=null;

        if(result==null){
            //getSender().tell("There are no Plans available right now ", ActorRef.noSender());
            throw new NoDataAvailableException("There are no cart for this account : "+msg.getAccount_id());
        }

//        GetCartResponse cartResponse=new GetCartResponse();
        List<Row> cartDetails=result.all();
        List<GetCartResponse> cartList=new ArrayList<>();
//        FetchDeviceResponse fetchDevice=new FetchDeviceResponse();
        cartDetails.forEach(cart -> {
            GetCartResponse cartResponse=new GetCartResponse();
            cartResponse.setAccount_key(cart.getString("account_key"));
            cartResponse.setCart_id(cart.getUUID("cart_id"));
            cartResponse.setCart_status(cart.getString("cart_status"));
            cartResponse.setCreate_ts(cart.getTimestamp("create_ts"));
            cartResponse.setModify_ts(cart.getTimestamp("modify_ts"));
          Map<String, Map<String, ItemInfo>> activityMap = (Map<String, Map<String, ItemInfo>>) cart.getObject("activitylist");
          cartResponse.setActivitylist(activityMap);
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



//        BoundStatement boundStatement = statement.bind(message.getCartStatus(),message.getAccountId(),message.getCartId());
        ResultSet resultSet = session.execute(boundStatement);
        Object msg = null;
        log.info("cart with account id "+ message.getAccountId() + "has been updated");
        getSender().tell("cart Updated", ActorRef.noSender());
    }
}
