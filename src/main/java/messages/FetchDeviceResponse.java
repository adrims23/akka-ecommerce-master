package messages;

public class FetchDeviceResponse {

    private final String productId;
    private final String skuId;
    private final String skuOrder;
    private final String prodName;
    private final String prodFrenchName;
    private final String prodDesc;
    private final String prodExternalId;



    public FetchDeviceResponse(String product_id, String sku_id, String sku_order, String prodName, String prodFrenchName, String prodDesc, String prodExternalId) {
        this.productId = product_id;
        this.skuId = sku_id;
        this.skuOrder = sku_order;
        this.prodName = prodName;
        this.prodFrenchName = prodFrenchName;
        this.prodDesc = prodDesc;
        this.prodExternalId = prodExternalId;
    }

    public String getProductId() {
        return productId;
    }

    public String getSkuId() {
        return skuId;
    }


    public String getSkuOrder() {
        return skuOrder;
    }


    public String getProdName() {
        return prodName;
    }


    public String getProdFrenchName() {
        return prodFrenchName;
    }


    public String getProdDesc() {
        return prodDesc;
    }


    public String getProdExternalId() {
        return prodExternalId;
    }

}
