package messages;

import java.util.UUID;

public class DeleteCartRequest {

    private final UUID cartId;

    private final String accountId;

    public DeleteCartRequest(UUID cartId, String accountId) {
        this.cartId = cartId;
        this.accountId = accountId;
    }

    public UUID getCartId() {
        return cartId;
    }


    public String getAccountId() {
        return accountId;
    }


}
