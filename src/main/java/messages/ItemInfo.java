package messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ItemInfo {

    private final String itemType;
    private final String skuId;
    private final int quantity;
    private final BigDecimal price;

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


    public String getSkuId() {
        return skuId;
    }


    public int getQuantity() {
        return quantity;
    }


    public BigDecimal getPrice() {
        return price;
    }

}
