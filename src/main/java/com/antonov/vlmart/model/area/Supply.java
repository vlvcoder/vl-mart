package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.enums.SUPPLY_CALL;
import com.antonov.vlmart.model.enums.SUPPLY_VOLUME;
import com.antonov.vlmart.model.supply.SupplyBlock;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Supply implements Area {
    private List<SupplyBlock> blocks = new ArrayList<>();
    private SUPPLY_VOLUME supplyVolume = SUPPLY_VOLUME.PROVIDER_VOLUME;

    @Override
    public String toString() {
        return "Поставка";
    }

    @Override
    public String statusText() {
        var builder = statusHeader("#00fc88");
        Utils.statusSupplyBlockList(blocks, builder);
        return builder.line().toString();
    }

    public String briefStatus() {
        var builder = new AnsiStringBuilder();
        Utils.statusSupplyBlockList(blocks, builder, true);
        return builder.line().toString();
    }

}
