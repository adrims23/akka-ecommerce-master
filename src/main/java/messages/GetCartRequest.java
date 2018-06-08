package messages;

import java.util.UUID;

public class GetCartRequest {
    private String account_id;
    private UUID cart_id;

    public GetCartRequest(String account_id, UUID cart_id) {
        this.account_id = account_id;
        this.cart_id = cart_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public UUID getCart_id() {
        return cart_id;
    }

    public void setCart_id(UUID cart_id) {
        this.cart_id = cart_id;
    }
}
