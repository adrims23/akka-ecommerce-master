package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CreateCartRequest {
    private String accountKey;
    private String cartStatus;
    Map<String, Map<String, ItemInfo>> activityMap;

    public CreateCartRequest(@JsonProperty("accountKey") String accountKey, @JsonProperty("cartStatus") String cartStatus,@JsonProperty("activityMap") Map<String, Map<String, ItemInfo>> activityMap) {
        this.accountKey = accountKey;
        this.cartStatus = cartStatus;
        this.activityMap = activityMap;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(String cartStatus) {
        this.cartStatus = cartStatus;
    }

    public Map<String, Map<String, ItemInfo>> getActivityMap() {
        return activityMap;
    }

    public void setActivityMap(Map<String, Map<String, ItemInfo>> activityMap) {
        this.activityMap = activityMap;
    }
}
