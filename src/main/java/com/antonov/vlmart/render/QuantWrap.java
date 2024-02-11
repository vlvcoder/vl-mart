package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.quant.PlacementQuant;
import com.antonov.vlmart.model.quant.Quant;
import lombok.Data;

@Data
public class QuantWrap {
    public QuantWrap(Quant quant) {
        this.startMillis = quant.startMillis();
        this.quantType = quant.quantType();
        this.staff = new StaffWrap(quant.staff());
        this.durationSec = quant.durationSec();
        if (quant instanceof PlacementQuant) {
            var placementQuant = (PlacementQuant) quant;
            good = placementQuant.good();
            goodTitle = good.title();
        }
    }

    private final QUANT_TYPE quantType;
    private final long startMillis;
    private final int durationSec;
    private StaffWrap staff;
    private GOOD good;
    private String goodTitle;

}
