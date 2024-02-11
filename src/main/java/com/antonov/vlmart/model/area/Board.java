package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.quant.Quant;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Board implements Area {
    //    private List<Quant> quants = new ArrayList<>();
    private List<Quant> quants = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String toString() {
        return "Задания";
    }

    @Override
    public String statusText() {
        var builder = statusHeader("#aadddd");
        Utils.statusQuantList(quants, builder);
        return builder.line().toString();
    }

}
