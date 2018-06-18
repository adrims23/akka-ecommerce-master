package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CreateCartRequest {
    private final String accountKey;
    private final String cartStatus;
    private final Map<String, Map<String, ItemInfo>> activityMap;

    public CreateCartRequest(@JsonProperty("accountKey") String accountKey, @JsonProperty("cartStatus") String cartStatus,@JsonProperty("activityMap") Map<String, Map<String, ItemInfo>> activityMap) {
        this.accountKey = accountKey;
        this.cartStatus = cartStatus;
        this.activityMap = activityMap;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public Map<String, Map<String, ItemInfo>> getActivityMap() {
        return activityMap;
    }

}
