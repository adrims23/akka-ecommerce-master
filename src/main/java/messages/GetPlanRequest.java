package messages;

public class GetPlanRequest {

    private final String planSkuId;

    public GetPlanRequest(String planSkuId) {
        this.planSkuId = planSkuId;
    }

    public String getCartId() {
        return planSkuId;
    }

}
