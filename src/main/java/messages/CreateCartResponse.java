package messages;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CreateCartResponse {
    private final UUID cartId;
    private final String accountKey;
    private final String cartStatus;
    private final Date creationTime;
    private final Date modifiedTime;
    private final Map<String, Map<String, ItemInfo>> activityMap;

    public CreateCartResponse(UUID cartId, String accountKey, String cartStatus, Date creationTime, Date modifiedTime, Map<String, Map<String, ItemInfo>> activityMap) {
        this.cartId = cartId;
        this.accountKey = accountKey;
        this.cartStatus = cartStatus;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
        this.activityMap = activityMap;
    }

    public UUID getCartId() {
        return cartId;
    }


    public String getAccountKey() {
        return accountKey;
    }


    public String getCartStatus() {
        return cartStatus;
    }


    public Date getCreationTime() {
        return creationTime;
    }


    public Date getModifiedTime() {
        return modifiedTime;
    }


    public Map<String, Map<String, ItemInfo>> getActivityMap() {
        return activityMap;
    }

}
