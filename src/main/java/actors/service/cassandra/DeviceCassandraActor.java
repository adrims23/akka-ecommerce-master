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
import messages.FetchDeviceResponse;
import messages.GetDeviceRequest;
import messages.PostDeviceRequest;

import java.util.ArrayList;
import java.util.List;

public class DeviceCassandraActor extends AbstractActor {

    private final Config config;
    /*default*/ ActorSystem system = context().system();
    final /*default*/ LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public DeviceCassandraActor(Config config) {
        this.config = config;
    }

    public static Props props(Config config) {
        return Props.create(DeviceCassandraActor.class, () -> new DeviceCassandraActor(config));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetDeviceRequest.class, msg -> {
            getDeviceList();
        }).match(PostDeviceRequest.class, msg -> {
            postDevice(msg);
        }).build();
    }

    private void postDevice(PostDeviceRequest message) {
        final Session session = SessionManager.getSession();
        log.info("inside postDevice");
        PreparedStatement statement = session.prepare("INSERT INTO ecommerce.devicesku(product_id,sku_id,sku_order,prod_name,prod_frenchname,prod_desc,prod_externalid) values(?,?,?,?,?,?,?)");
        BoundStatement boundStatement = statement.bind(message.getProduct_id(), message.getSku_id(), message.getSku_order(), message.getProd_name(), message.getProd_frenchName(), message.getProd_desc(), message.getProd_externalId());
        session.execute(boundStatement);
        log.info("Device inserted");

        getSender().tell("device inserted", ActorRef.noSender());


    }

    private void getDeviceList() {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM DEVICESKU");
        BoundStatement boundStatement = statement.bind();
        ResultSet result = session.execute(boundStatement);
        Object message = null;
        log.info("before result set fetch");
        if (result == null) {
            //getSender().tell("There are no Plans available right now ", ActorRef.noSender());
//            throw new NoDataAvailableException("There are no Devices available right now");
            log.info("There are no Devices available right now");
        }
        List<Row> devices = result.all();
        List<FetchDeviceResponse> deviceList = new ArrayList<>();

        devices.forEach(device -> {
            FetchDeviceResponse fetchDevice = new FetchDeviceResponse(device.getString("product_id"),
                    device.getString("sku_id"),
                    device.getString("sku_order"),
                    device.getString("prod_name"),
                    device.getString("prod_frenchname"),
                    device.getString("prod_desc"),
                    device.getString("prod_externalId")
            );
            deviceList.add(fetchDevice);


        });

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(deviceList);
        } catch (JsonProcessingException e) {
           log.info("json issue");
        }

        getSender().tell(jsonInString, ActorRef.noSender());

    }
}
