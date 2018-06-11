package actors.service.cassandra;

import com.datastax.driver.core.*;
import com.typesafe.config.ConfigFactory;
import constants.SessionConstants;
import messages.ItemInfo;
import util.ActivityListCodec;

import java.util.List;

public class SessionManager {


public static Session getSession() {
    Session session = null;

    if (session == null) {
        List<String> cassandraContactPoints = ConfigFactory.load().getStringList
                (SessionConstants.CASSANDRA_DB_HOST_NAME);
        Cluster.Builder builder = Cluster.builder();
        for (String cassandraContactPoint : cassandraContactPoints) {
            builder.addContactPoint(cassandraContactPoint);
        }
        builder.withPort(Integer.parseInt(ConfigFactory.load().getString(SessionConstants.CASSANDRA_DB_PORT)));
        CodecRegistry codecRegistry = new CodecRegistry();
        Cluster cluster = builder.withCodecRegistry(codecRegistry)
                .build();

        String ecomKeyspace
                = ConfigFactory.load().getString(SessionConstants.CASSANDRA_DB_KEYSPACE);
        UserType activityType = cluster.getMetadata().getKeyspace(ecomKeyspace).getUserType("iteminfo");
        TypeCodec<UDTValue> activityTypeCodec = codecRegistry.codecFor(activityType);
        ActivityListCodec addressCodec = new ActivityListCodec(activityTypeCodec, ItemInfo.class);
        codecRegistry.register(addressCodec);
        session = cluster.connect(ecomKeyspace);
    }
    return session;
}

}
