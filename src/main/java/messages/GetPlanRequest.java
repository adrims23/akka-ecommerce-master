package messages;

public class GetPlanRequest {

    private String planSkuId;

    public GetPlanRequest(String planSkuId) {
        this.planSkuId = planSkuId;
    }

    public String getCartId() {
        return planSkuId;
    }

    public void setCartId(String planSkuId) {
        this.planSkuId = planSkuId;
    }
}
