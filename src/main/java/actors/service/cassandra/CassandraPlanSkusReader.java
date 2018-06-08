package actors.service.cassandra;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import messages.PlanSkuVo;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import exception.NoDataAvailableException;
import messages.GetPlanRequest;

import java.util.ArrayList;
import java.util.List;


public class CassandraPlanSkusReader extends AbstractActor {
    private final Config appConfig;
    SessionManager sessionManager = new SessionManager();

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

    private void getPlanSkus() throws NoDataAvailableException {
        // product_id | sku_id | prod_desc  | prod_enddate | prod_externalid | prod_frenchname
        // | prod_name | prod_startdate | sku_features| sku_order

        List<PlanSkuVo> planList = new ArrayList();
        final Session session = sessionManager.getSession();


        System.out.print("in GetPlanSkus");
        PreparedStatement statement = session.prepare("SELECT * FROM PLANSKU");
        BoundStatement boundStatement = statement.bind();
        ResultSet result = session.execute(boundStatement);
        //ResultSet result = session.execute(stmt.toString());
        System.out.println(result);
        if (result != null) {
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
            String jsonInString = null;
            try {
                jsonInString = mapper.writeValueAsString(planList);
            } catch (JsonProcessingException e) {
                getSender().tell("ExceptionOccured : "+e.getMessage(), ActorRef.noSender());
                e.printStackTrace();
            }

            getSender().tell(jsonInString, ActorRef.noSender());
        } else {
            getSender().tell("There are no Plans available right now ", ActorRef.noSender());
            throw new NoDataAvailableException("There are no Plans available right now");
        }

    }

    public static Props props(Config appConfig) {
        return Props.create(CassandraPlanSkusReader.class,
                () -> new CassandraPlanSkusReader(appConfig));
    }

}
