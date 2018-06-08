package messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostDeviceRequest {
    private String product_id;
    private String sku_id;
    private int sku_order;
    private String prod_name;
    private String prod_frenchName;
    private String prod_desc;
    private String prod_externalId;

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

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public int getSku_order() {
        return sku_order;
    }

    public void setSku_order(int sku_order) {
        this.sku_order = sku_order;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_frenchName() {
        return prod_frenchName;
    }

    public void setProd_frenchName(String prod_frenchName) {
        this.prod_frenchName = prod_frenchName;
    }

    public String getProd_desc() {
        return prod_desc;
    }

    public void setProd_desc(String prod_desc) {
        this.prod_desc = prod_desc;
    }

    public String getProd_externalId() {
        return prod_externalId;
    }

    public void setProd_externalId(String prod_externalId) {
        this.prod_externalId = prod_externalId;
    }
}
