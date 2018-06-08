package util;

import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import messages.ItemInfo;

import java.nio.ByteBuffer;

public class ActivityListCodec extends TypeCodec<ItemInfo> {

    private final TypeCodec<UDTValue> innerCodec;

    private final UserType userType;

    public ActivityListCodec(TypeCodec<UDTValue> innerCodec, Class<ItemInfo> javaType) {
        super(innerCodec.getCqlType(), javaType);
        this.innerCodec = innerCodec;
        this.userType = (UserType) innerCodec.getCqlType();
    }

    @Override
    public ByteBuffer serialize(ItemInfo value, ProtocolVersion protocolVersion) throws InvalidTypeException {
        return innerCodec.serialize(toUDTValue(value), protocolVersion);
    }

    @Override
    public ItemInfo deserialize(ByteBuffer bytes, ProtocolVersion protocolVersion) throws InvalidTypeException {
        return toActivity(innerCodec.deserialize(bytes, protocolVersion));
    }

    @Override
    public ItemInfo parse(String value) throws InvalidTypeException {
        return value == null || value.isEmpty() || value.equals(null) ? null : toActivity(innerCodec.parse(value));
    }

    @Override
    public String format(ItemInfo value) throws InvalidTypeException {
        return value == null ? null : innerCodec.format(toUDTValue(value));
    }

    protected ItemInfo toActivity(UDTValue value) {

        return value == null ? null : new ItemInfo(
                value.getString("item_type"),value.getString("sku_id"),value.getInt("quantity"),value.getDecimal("price"));
    }

    protected UDTValue toUDTValue(ItemInfo value) {
        return value == null ? null : userType.newValue()
                .setString("item_type", value.getItemType())
                .setString("sku_id", value.getSkuId())
                .setInt("quantity", value.getQuantity())
                .setDecimal("price", value.getPrice());
    }
}
