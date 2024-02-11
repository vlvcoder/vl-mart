package com.antonov.vlmart.service.schedule;

import com.antonov.vlmart.service.RootService;
import com.antonov.vlmart.service.buyer.BuyerService;
import com.antonov.vlmart.service.product.SupplyService;
import com.antonov.vlmart.service.work.BoardService;
import com.antonov.vlmart.service.work.EmployeeService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class ScheduleService {

    private final EmployeeService employeeService;
    private final BuyerService buyerService;
    private final BoardService boardService;
    private final RootService rootService;
    private final SupplyService supplyService;

    public ScheduleService(
            EmployeeService employeeService,
            BuyerService buyerService,
            BoardService boardService,
            RootService rootService,
            SupplyService supplyService) {
        this.employeeService = employeeService;
        this.buyerService = buyerService;
        this.boardService = boardService;
        this.rootService = rootService;
        this.supplyService = supplyService;
    }

    @Scheduled(fixedRate = 1000)
    public void loopStaffs() {
        if (rootService.checkGameOver()) {
            return;
        }
        employeeService.loopStaffs();
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanBusyStaffs() {
        if (rootService.checkGameOver()) {
            return;
        }
        employeeService.cleanBusyStaffs();
    }

    @Scheduled(fixedRate = 1000)
    public void loopBuyers() {
        if (rootService.checkGameOver()) {
            return;
        }
        buyerService.loopBuyers();
    }

    @Scheduled(fixedRate = 1000)
    public void loopSupply() {
        if (rootService.checkGameOver()) {
            return;
        }
        supplyService.loopUnload();
    }

    @Scheduled(fixedRate = 10_000)
    public void cancelLongObjects() {
        if (rootService.checkGameOver()) {
            return;
        }
        boardService.cancelLongQuants();
        buyerService.cancelLongBuyers();
    }

}
