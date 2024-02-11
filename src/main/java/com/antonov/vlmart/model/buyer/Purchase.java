package com.antonov.vlmart.model.buyer;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Purchase {
    private final GOOD good;
    private final int plan;
    private int fact;
    private int tryCount;

    public Purchase(GOOD good, int plan, int tryCount) {
        this.good = good;
        this.plan = plan;
        this.tryCount = tryCount;
    }

    @Override
    public String toString() {
        var res = new AnsiStringBuilder();
        if (fact == plan) {
            res.color(AnsiStringBuilder.ForegroundColor.GREEN);
        }
        else if (fact > 0) {
            res.color(AnsiStringBuilder.ForegroundColor.YELLOW);
        }
        res.append(String.format("%s(%d/%d)", good.title(), fact, plan)).reset();
        return res.toString();
    }
}
