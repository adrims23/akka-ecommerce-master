package messages;

import java.util.Map;

public class PlanSkuVo {
    // product_id | sku_id | prod_desc  | prod_enddate | prod_externalid | prod_frenchname
    // | prod_name | prod_startdate | sku_features| sku_order
    private String productId;
    private String skuId;
    private String productDesc;
    private String productFrenchName;
    private String productName;
    private Map<String,String> features;
    private int skuOrder;

    public PlanSkuVo(String productId, String skuId, String productDesc, String productFrenchName, String productName, Map<String, String> features, int skuOrder) {
        this.productId = productId;
        this.skuId = skuId;
        this.productDesc = productDesc;
        this.productFrenchName = productFrenchName;
        this.productName = productName;
        this.features = features;
        this.skuOrder=skuOrder;
    }

    public String getProductId() {
        return productId;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductFrenchName() {
        return productFrenchName;
    }

    public String getProductName() {
        return productName;
    }

    public Map<String, String> getFeatures() {
        return features;
    }
}
