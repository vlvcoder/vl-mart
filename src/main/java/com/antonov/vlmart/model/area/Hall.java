package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.buyer.Buyer;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Hall implements Area {
    private List<Buyer> buyers = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String toString() {
        return "Покупатели";
    }

    @Override
    public String statusText() {
        var builder = statusHeader("#ddaadd");
        Utils.statusBuyerList(buyers, builder);
        return builder.line().toString();
    }

}
