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
    ActorSystem system = context().system();
    final LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public DeviceCassandraActor(Config config) {
        this.config = config;
    }

    public static Props props(Config config)
    {
        return Props.create(DeviceCassandraActor.class,()->new DeviceCassandraActor(config));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetDeviceRequest.class, msg ->{
            getDeviceList();
        }).match(PostDeviceRequest.class, msg ->{
            postDevice(msg);
        }).build();
    }

    private void postDevice(PostDeviceRequest message) {
        final Session session = SessionManager.getSession();
        log.info("inside postDevice");
        PreparedStatement statement = session.prepare("INSERT INTO ecommerce.devicesku(product_id,sku_id,sku_order,prod_name,prod_frenchname,prod_desc,prod_externalid) values(?,?,?,?,?,?,?)");
        BoundStatement boundStatement = statement.bind(message.getProduct_id(),message.getSku_id(),message.getSku_order(),message.getProd_name(),message.getProd_frenchName(),message.getProd_desc(),message.getProd_externalId());
        ResultSet result = session.execute(boundStatement);
        Object msg=null;
        log.info("Device inserted");

        getSender().tell("device inserted",ActorRef.noSender());


    }

    private void getDeviceList() {
        final Session session = SessionManager.getSession();
        log.info("inside getDeviceList");
        PreparedStatement statement = session.prepare("SELECT * FROM DEVICESKU");
        BoundStatement boundStatement = statement.bind();
        ResultSet result = session.execute(boundStatement);
        Object message=null;

        log.info("before result set fetch");

        List<Row> devices=result.all();
        List<FetchDeviceResponse> deviceList=new ArrayList<>();
        FetchDeviceResponse fetchDevice=new FetchDeviceResponse();
        devices.forEach(device -> {
            fetchDevice.setProduct_id(device.getString("product_id"));
            fetchDevice.setSku_id(device.getString("sku_id"));
//            fetchDevice.setSku_order(device.getString("sku_order"));
            fetchDevice.setProd_name(device.getString("prod_name"));
            fetchDevice.setProd_frenchName(device.getString("prod_frenchname"));
            fetchDevice.setProd_desc(device.getString("prod_desc"));
            fetchDevice.setProd_externalId(device.getString("prod_externalId"));

            log.info(fetchDevice.getProd_desc());
            deviceList.add(fetchDevice);


        });

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString=null;
        try {
            jsonInString=mapper.writeValueAsString(deviceList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        getSender().tell(jsonInString,ActorRef.noSender());

    }
}
