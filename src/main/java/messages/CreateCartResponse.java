package messages;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class CreateCartResponse {
    private UUID cartId;
    private String accountKey;
    private String cartStatus;
    private Date creationTime;
    private Date modifiedTime;
    Map<String, Map<String, ItemInfo>> activityMap;

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

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
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

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Map<String, Map<String, ItemInfo>> getActivityMap() {
        return activityMap;
    }

    public void setActivityMap(Map<String, Map<String, ItemInfo>> activityMap) {
        this.activityMap = activityMap;
    }
}
