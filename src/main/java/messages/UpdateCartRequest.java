package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class UpdateCartRequest {
    private String accountId;
    private UUID cartId;
    private Map<String,Map<String,ItemInfo>> activitylist;
    private String cartStatus;

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

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(String cartStatus) {
        this.cartStatus = cartStatus;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public Map<String, Map<String, ItemInfo>> getActivitylist() {
        return activitylist;
    }

    public void setActivitylist(Map<String, Map<String, ItemInfo>> activitylist) {
        this.activitylist = activitylist;
    }
}
