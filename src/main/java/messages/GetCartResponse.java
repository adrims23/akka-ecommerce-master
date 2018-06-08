package messages;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class GetCartResponse {

    private String account_key;
    private UUID cart_id;
    private String cart_status;
    private Date create_ts;
    private Date modify_ts;
    private Map<String,Map<String,ItemInfo>> activitylist;

    public String getAccount_key() {
        return account_key;
    }

    public void setAccount_key(String account_key) {
        this.account_key = account_key;
    }

    public UUID getCart_id() {
        return cart_id;
    }

    public void setCart_id(UUID cart_id) {
        this.cart_id = cart_id;
    }

    public String getCart_status() {
        return cart_status;
    }

    public void setCart_status(String cart_status) {
        this.cart_status = cart_status;
    }

    public Date getCreate_ts() {
        return create_ts;
    }

    public void setCreate_ts(Date create_ts) {
        this.create_ts = create_ts;
    }

    public Date getModify_ts() {
        return modify_ts;
    }

    public void setModify_ts(Date modify_ts) {
        this.modify_ts = modify_ts;
    }

    public Map<String, Map<String, ItemInfo>> getActivitylist() {
        return activitylist;
    }

    public void setActivitylist(Map<String, Map<String, ItemInfo>> activitylist) {
        this.activitylist = activitylist;
    }
}
