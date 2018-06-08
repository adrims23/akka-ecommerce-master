package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ItemInfo {

    private String itemType;
    private String skuId;
    private int quantity;
    private BigDecimal price;

    public ItemInfo(@JsonProperty("itemType")String itemType, @JsonProperty("skuId") String skuId, @JsonProperty("quantity")
            int quantity, @JsonProperty("price") BigDecimal price) {
        this.itemType = itemType;
        this.skuId = skuId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
