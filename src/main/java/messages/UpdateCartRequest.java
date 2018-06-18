package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class UpdateCartRequest {
    private final String accountId;
    private final UUID cartId;
    private final Map<String,Map<String,ItemInfo>> activitylist;
    private final String cartStatus;

    public UpdateCartRequest( String accountId, @JsonProperty("cartStatus") String cartStatus,
                              UUID cartId, @JsonProperty("activitylist") Map<String,Map<String,ItemInfo>> activitylist) {
        this.accountId = accountId;
        this.cartStatus = cartStatus;
        this.cartId = cartId;
        this.activitylist = activitylist;
    }

    public String getAccountId() {
        return accountId;
    }


    public String getCartStatus() {
        return cartStatus;
    }


    public UUID getCartId() {
        return cartId;
    }


    public Map<String, Map<String, ItemInfo>> getActivitylist() {
        return activitylist;
    }

}
