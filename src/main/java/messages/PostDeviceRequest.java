package messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDeviceRequest {
    private final String product_id;
    private final String sku_id;
    private final int sku_order;
    private final String prod_name;
    private final String prod_frenchName;
    private final String prod_desc;
    private final String prod_externalId;

    @JsonCreator
    public PostDeviceRequest(@JsonProperty("product_id") String product_id, @JsonProperty("sku_id") String sku_id, @JsonProperty("sku_order") int sku_order, @JsonProperty("product_name") String prod_name, @JsonProperty("prod_frenchName") String prod_frenchName, @JsonProperty("prod_desc") String prod_desc, @JsonProperty("prod_externalId") String prod_externalId) {
        this.product_id = product_id;
        this.sku_id = sku_id;
        this.sku_order = sku_order;
        this.prod_name = prod_name;
        this.prod_frenchName = prod_frenchName;
        this.prod_desc = prod_desc;
        this.prod_externalId = prod_externalId;
    }

    public String getProduct_id() {
        return product_id;
    }


    public String getSku_id() {
        return sku_id;
    }


    public int getSku_order() {
        return sku_order;
    }


    public String getProd_name() {
        return prod_name;
    }


    public String getProd_frenchName() {
        return prod_frenchName;
    }


    public String getProd_desc() {
        return prod_desc;
    }


    public String getProd_externalId() {
        return prod_externalId;
    }

}
