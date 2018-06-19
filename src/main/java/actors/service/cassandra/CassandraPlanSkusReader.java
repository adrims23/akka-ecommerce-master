package actors.service.cassandra;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import messages.PlanSkuVo;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import messages.GetPlanRequest;

import java.util.ArrayList;
import java.util.List;


public class CassandraPlanSkusReader extends AbstractActor {
    private final Config appConfig;
    ActorSystem system = context().system();
    SessionManager sessionManager = new SessionManager();
    final LoggingAdapter log = Logging.getLogger(system.eventStream(), this);

    public CassandraPlanSkusReader(Config appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GetPlanRequest.class, response ->
        {
            System.out.print("matched for casandra actor");
            getPlanSkus();
        }).build();
    }

    private void getPlanSkus() {
        // product_id | sku_id | prod_desc  | prod_enddate | prod_externalid | prod_frenchname
        // | prod_name | prod_startdate | sku_features| sku_order

        List<PlanSkuVo> planList = new ArrayList<>();
        final Session session = sessionManager.getSession();


        PreparedStatement statement = session.prepare("SELECT * FROM PLANSKU");
        BoundStatement boundStatement = statement.bind();
        ResultSet result = session.execute(boundStatement);

        if (result != null) {
            log.info("Populating results for getPlanSkus");
            result.forEach(row -> {
                PlanSkuVo planSkuVo = new PlanSkuVo(row.getString("product_id"),
                        row.getString("sku_id"),
                        row.getString("prod_desc"),
                        row.getString("prod_frenchname"),
                        row.getString("prod_name"),
                        row.getMap("sku_features", String.class, String.class),
                        row.getInt("sku_order"));
                planList.add(planSkuVo);
            });

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString ="";
            try {
                jsonInString = mapper.writeValueAsString(planList);
            } catch (JsonProcessingException e) {
                log.info("error in forming json ");
            }

            getSender().tell(jsonInString, ActorRef.noSender());
        } else {
            log.info("No Planskus found");
//            throw new NoDataAvailableException("There are no Plans available right now");
        }

    }

    public static Props props(Config appConfig) {
        return Props.create(CassandraPlanSkusReader.class,
                () -> new CassandraPlanSkusReader(appConfig));
    }

}
