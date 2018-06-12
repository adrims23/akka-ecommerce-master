package bootstrap;

import actors.supervisor.*;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.*;
import akka.pattern.PatternsCS;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.*;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.NotUsed;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.typesafe.config.Config;
import constants.RogersConstants;
import messages.*;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Path;


public class RoutesActor extends AbstractActor {

    private final Config appConfig;
    private final ActorRef getCartSupervisorActor;
//    private final ActorRef deviceCassandraActor;
    private final ActorRef deleteCartSupervisorActor;
    private final ActorRef updateCartSupervisorActor;
    private final ActorRef createCartSupervisorActor;
    private final ActorRef getDeviceActor;
    private final ActorRef getPlanActor;
    public final ObjectMapper objectMapper;
    private final CompletionStage<ServerBinding> binding;

    public RoutesActor(Config appConfig) {
        super();
        this.appConfig = appConfig;
        ActorSystem system = context().system();
        this.objectMapper = new ObjectMapper();
        this.getCartSupervisorActor = system.actorOf(GetCartActor.props(appConfig), "GetCartActor");
        this.getPlanActor = system.actorOf(GetPlanActor.props(appConfig), "GetPlanActor");
        this.updateCartSupervisorActor = system.actorOf(UpdateCartActor.props(appConfig), "UpdateCartActor");
        this.deleteCartSupervisorActor = system.actorOf(DeleteCartActor.props(appConfig), "DeleteCartActor");
        this.createCartSupervisorActor = system.actorOf(CreateCartActor.props(appConfig), "CreateCartActor");


//        this.deviceCassandraActor=system.actorOf(DeviceCassandraActor.props(),"DeviceCassandraActor");
        this.getDeviceActor = system.actorOf(GetDevice.props(appConfig),"GetDeviceActor");
        // Set up and start the HTTP server
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        // In order to access all directives we need an instance where the routes are defined
        final Routes routes = new Routes();
        final String hostname = appConfig.getString(RogersConstants.DEVICEMS_HTTP_HOSTNAME);
        final int port = appConfig.getInt(RogersConstants.DEVICEMS_HTTP_PORT);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                routes.createRoute(getSelf(), hostname, port)
                        .flow(system, materializer);
        binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(hostname, port), materializer);

    }

    public static Props props(Config config) {
        return Props.create(RoutesActor.class, () -> new RoutesActor(config));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Stop.class, stop -> {
                    //Trigger HTTP server unbinding from the port
                    binding.thenCompose(ServerBinding::unbind)
                            //Shutdown when done
                            .thenAccept(unbound -> getContext().stop(getSelf()));
                }).build();
    }

    private enum Stop {
        INSTANCE
    }

    // In order to access all directives the code needs to be inside an instance of a class that extends AllDirectives
    class Routes extends AllDirectives {
        public Route createRoute(ActorRef mainActor, String hostname, int port) {
            final String host = hostname + ":" + port;
            return route(
                    fetchCartListDetails(),
                    fetchDeviceDetails(),
                    addDeviceDetails(),
                    fetchCartDetails(),
                    fetchPlanSkus(),
                    deleteCartDetails(),
                    createCart(),
                    updateCart(),
                    getStopPath(mainActor, host)
            );
        }

        public Route fetchCartListDetails() {
            ///v1/commerce/{accountID}/cart
            final PathMatcher1 segment = PathMatchers.segment(appConfig.getString(RogersConstants.CARTMS_API_COMMERCE))
                    .slash(PathMatchers.segment())
                    .slash(appConfig.getString(RogersConstants.CARTMS_API_CART));
            return route(
                get(() -> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            () -> path(segment, accountId ->{
                                    final CompletionStage<Object> consolidatedDetailF =
                                    PatternsCS.ask(getCartSupervisorActor, new GetCartListRequest(accountId.toString()),
                                    appConfig.getLong("actors.timeout"));
                                    return onSuccess(() -> consolidatedDetailF,
                                    cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                            })
                            )
                        )

            );
        }

        public Route fetchCartDetails() {
            ///v1/commerce/{accountID}/cart/{cartId}
            final PathMatcher2 segment = PathMatchers.segment(appConfig.getString(RogersConstants.CARTMS_API_COMMERCE))
                    .slash(PathMatchers.segment())
                    .slash(appConfig.getString(RogersConstants.CARTMS_API_CART))
                    .slash(PathMatchers.segment());
            return route(
                    get(() -> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            () -> path(segment, (accountId,cartId) ->{
                                final CompletionStage<Object> consolidatedDetailF =
                                        PatternsCS.ask(getCartSupervisorActor, new GetCartRequest(accountId.toString(),UUID.fromString(cartId.toString())),
                                                appConfig.getLong("actors.timeout"));
                                return onSuccess(() -> consolidatedDetailF,
                                        cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                            })
                            )
                    )

            );
        }

        //POST request
        public Route addDeviceDetails() {
            ///v1/commerce/device
            final PathMatcher0 segment = PathMatchers.segment(appConfig.getString(RogersConstants.DEVICEMS_API_COMMERCE))
                    .slash(appConfig.getString(RogersConstants.DEVICEMS_API_DEVICE));
            return route(
                    post(() -> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            () -> path(segment, () ->entity(Jackson.unmarshaller(PostDeviceRequest.class), message -> {
                                final CompletionStage<Object> consolidatedDetailF =
                                        PatternsCS.ask(getDeviceActor, message,
                                                appConfig.getLong("actors.timeout"));
                                return onSuccess(() -> consolidatedDetailF,
                                        cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                            })
                            )
                    ))

            );

        }

        public Route fetchDeviceDetails() {
            ///v1/commerce/device
            final PathMatcher0 segment = PathMatchers.segment(appConfig.getString(RogersConstants.DEVICEMS_API_COMMERCE))
                    .slash(appConfig.getString(RogersConstants.DEVICEMS_API_DEVICE));
            return route(
                    get(() -> pathPrefix(appConfig.getString(RogersConstants.DEVICEMS_API_VERSION),
                            () -> path(segment,()->{
                                final CompletionStage<Object> consolidatedDetailF =
                                        PatternsCS.ask(getDeviceActor, new GetDeviceRequest(),
                                                appConfig.getLong("actors.timeout"));
                                return onSuccess(() -> consolidatedDetailF,
                                        cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                            })
                            )

                    )
            );
        }

        public Route fetchPlanSkus() {
            ///v1/plan
            final PathMatcher1 segment = PathMatchers.segment(appConfig.getString(RogersConstants.PLANMS_API_PLAN))
                    .slash(PathMatchers.segment());


            return route(
                    get(() -> pathPrefix(appConfig.getString(RogersConstants.PLANMS_API_VERSION),
                            () -> {
                                return path(planId -> {
                                    final CompletionStage<Object> consolidatedDetailF =
                                            PatternsCS.ask(getPlanActor, new GetPlanRequest(planId.toString()),
                                                    appConfig.getLong("actors.timeout"));
                                    return onSuccess(() -> consolidatedDetailF,
                                            planDetails -> this.complete(StatusCodes.OK, planDetails, Jackson.marshaller()));
                                });
                            }
                            )
                    )

            );
        }

        public Route deleteCartDetails() {
            ///v1/commerce/{AccountId}/cart/{cartId}
            final PathMatcher2 segment = PathMatchers.segment(appConfig.getString(RogersConstants.CARTMS_API_COMMERCE))
                    .slash(PathMatchers.segment())
                    .slash(appConfig.getString(RogersConstants.CARTMS_API_CART))
                    .slash(PathMatchers.segment());
            return route(
                    delete (() -> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            ()  -> path(segment, (accountId,cartId)  ->{
                                final CompletionStage<Object> consolidatedDetailF =
                                        PatternsCS.ask(deleteCartSupervisorActor,
                                                new DeleteCartRequest(UUID.fromString(cartId.toString()), accountId.toString()),
                                                appConfig.getLong("actors.timeout"));
                                return onSuccess(() -> consolidatedDetailF,
                                        deleteCartDetails -> this.complete(StatusCodes.OK, deleteCartDetails, Jackson.marshaller()));
                            })
                            )
                    )

            );
        }

        private Route createCart() {
            ///v1/commerce/{AccountID}/cart
            System.out.println("test in");
            final PathMatcher1 segment = PathMatchers.segment(appConfig.getString(RogersConstants.CARTMS_API_COMMERCE))
                    .slash(PathMatchers.segment())
                    .slash(appConfig.getString(RogersConstants.CARTMS_API_CART));
            return route(
                    post(() -> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            () -> path(segment, (accountID) ->entity(Jackson.unmarshaller(CreateCartRequest.class), message -> {
                                        final CompletionStage<Object> consolidatedDetailF =
                                                PatternsCS.ask(createCartSupervisorActor, new CreateCartRequest(accountID.toString(),message.getCartStatus(),message.getActivityMap()),
                                                        appConfig.getLong("actors.timeout"));
                                        return onSuccess(() -> consolidatedDetailF,
                                                cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                                    })
                            )
                    ))

            );

        }

        private Route updateCart() {
            ///v1/commerce/{AccountID}/cart/{cartId}
            final PathMatcher2 segment = PathMatchers.segment(appConfig.getString(RogersConstants.CARTMS_API_COMMERCE))
                    .slash(PathMatchers.segment())
                    .slash(appConfig.getString(RogersConstants.CARTMS_API_CART))
                    .slash(PathMatchers.segment());
            return route(
                    put(()-> pathPrefix(appConfig.getString(RogersConstants.CARTMS_API_VERSION),
                            ()-> path(segment, (accountId,cartId) -> entity(Jackson.unmarshaller(UpdateCartRequestBody.class),
                                    requestObj -> {
                                        final CompletionStage<Object> consolidatedDetailF =
                                                PatternsCS.ask(updateCartSupervisorActor,new UpdateCartRequest(accountId
                                                                .toString(),
                                                                requestObj.getCartStatus(),
                                                                UUID.fromString(cartId.toString()),
                                                                requestObj.getActivitylist()),
                                                        appConfig.getLong("actors.timeout"));
                                        return onSuccess(()-> consolidatedDetailF,
                                                cartDetails -> this.complete(StatusCodes.OK, cartDetails, Jackson.marshaller()));
                                    })
                            )))
            );
        }


        @Path("/stop")
        public Route getStopPath(ActorRef actorRef, String host) {
            return route(path(RogersConstants.STOP_ROUTE, () ->
                            get(() -> {
                                actorRef.tell(Stop.INSTANCE, ActorRef.noSender());
                                return complete("Server " + host + " is shutting Down\n");
                            })
                    )
            );
        }
    }


}
