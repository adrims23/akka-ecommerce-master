package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class UpdateCartRequestBody {

    private final Map<String,Map<String,ItemInfo>> activitylist;
    private final String cartStatus;

    public UpdateCartRequestBody(@JsonProperty("activitylist") Map<String, Map<String, ItemInfo>> activitylist, @JsonProperty("cartStatus") String cartStatus) {
        this.activitylist = activitylist;
        this.cartStatus = cartStatus;
    }

        public String getCartStatus() {
        return cartStatus;
    }


    public Map<String, Map<String, ItemInfo>> getActivitylist() {
        return activitylist;
    }

}
