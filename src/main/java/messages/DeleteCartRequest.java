package messages;

import java.util.UUID;

public class DeleteCartRequest {

    private UUID cartId;

    private String accountId;

    public DeleteCartRequest(UUID cartId, String accountId) {
        this.cartId = cartId;
        this.accountId = accountId;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
