package com.antonov.vlmart.service.work;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.quant.Quant;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.render.MediatorWrap;
import com.antonov.vlmart.render.QuantWrap;
import com.antonov.vlmart.wrap.QuantCompleter;

import java.util.List;

public interface BoardService {
    void init();

    Quant createPlacementQuant(Staff staff, GOOD good);
    Quant createCashCheckoutQuant(Staff staff);

    void setQuantCompleter(QuantCompleter completer);

    int lockedQuant(GOOD good);

    void cancelLongQuants();

    void fillMovings(List<MediatorWrap> mediatorWraps);

    List<QuantWrap> activeQuants();

    long quantCount();
    List<Quant> quants();
    boolean unloadStart(boolean includeManager);

}
