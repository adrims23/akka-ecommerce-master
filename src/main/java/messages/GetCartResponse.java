package messages;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class GetCartResponse {

    private final String account_key;
    private final UUID cart_id;
    private final String cart_status;
    private final Date create_ts;
    private final Date modify_ts;
    private final Map<String,Map<String,ItemInfo>> activitylist;

    public GetCartResponse(String account_key, UUID cart_id, String cart_status, Date create_ts, Date modify_ts, Map<String, Map<String, ItemInfo>> activitylist) {
        this.account_key = account_key;
        this.cart_id = cart_id;
        this.cart_status = cart_status;
        this.create_ts = create_ts;
        this.modify_ts = modify_ts;
        this.activitylist = activitylist;
    }

    public String getAccount_key() {
        return account_key;
    }


    public UUID getCart_id() {
        return cart_id;
    }


    public String getCart_status() {
        return cart_status;
    }


    public Date getCreate_ts() {
        return create_ts;
    }


    public Date getModify_ts() {
        return modify_ts;
    }


    public Map<String, Map<String, ItemInfo>> getActivitylist() {
        return activitylist;
    }

}
