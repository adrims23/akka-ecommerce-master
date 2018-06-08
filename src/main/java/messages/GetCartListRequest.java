package messages;

import java.util.Date;
import java.util.UUID;

public class GetCartListRequest {

    private String account_id;

    public GetCartListRequest(String account_id) {
        this.account_id = account_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
