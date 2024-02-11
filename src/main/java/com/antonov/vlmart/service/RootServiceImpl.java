package com.antonov.vlmart.service;

import com.antonov.vlmart.config.MainConfiguration;
import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.model.enums.SUPPLY_CALL;
import com.antonov.vlmart.model.enums.SUPPLY_VOLUME;
import com.antonov.vlmart.render.StatusWrap;
import com.antonov.vlmart.render.SupplyCallWrap;
import com.antonov.vlmart.render.SupplyPlanWrap;
import com.antonov.vlmart.render.SupplyVolumeWrap;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.business.UpgradeService;
import com.antonov.vlmart.service.buyer.BuyerService;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.product.SupplyService;
import com.antonov.vlmart.service.work.BoardService;
import com.antonov.vlmart.service.work.EmployeeService;
import com.antonov.vlmart.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RootServiceImpl implements RootService {

    private final MainConfiguration mainConfiguration;
    private final MartService martService;
    private final BoardService boardService;
    private final EmployeeService employeeService;
    private final SupplyService supplyService;
    private final BuyerService buyerService;
    private final BusinessService businessService;
    private final NotificationService notificationService;
    private final UpgradeService upgradeService;

    @Override
    public void initData() {
        martService.init();
        employeeService.init();
        boardService.init();
        supplyService.init();
        buyerService.init();
        businessService.init();
        notificationService.init();
    }

    @Override
    public StatusWrap data() {
        if (!checkGameOver() && !martService.opened()) {
            notificationService.sendOnce(NOTIFY_TYPE.WELCOME);
        }
        var wrap = martService.VLMartStatus();
        var staffFolder = employeeService.staffs();

        wrap.setGameOver(checkGameOver());
        wrap.setBusiness(businessService.businessWrap());
        wrap.setBuyers(buyerService.buyersStates());
        wrap.setSupply(supplyService.getSupplyWrap());
        wrap.setQuants(boardService.activeQuants());
        wrap.setStaffs(staffFolder.getStaffs());
        wrap.setStaffsCollapsed(staffFolder.getStaffsCollapsed());
        wrap.setFillRaising(upgradeService.fillRaising());
        wrap.setCashRaising(upgradeService.cashRaising());
        wrap.setNotify(notificationService.activeNotify());
        wrap.setAlert(notificationService.fulshAlert());
        wrap.setUpgrade(upgradeService.currentUpgrade());

        supplyService.fillWaitingSupply(wrap.getStorePlaces());
        buyerService.fillBuyerNeeds(wrap.getMediatorWraps());
        boardService.fillMovings(wrap.getMediatorWraps());
        return wrap;
    }

    @Override
    public boolean checkGameOver() {
        return businessService.dislikeCount() > businessService.likeCount() ||
                businessService.dislikeCount() > mainConfiguration.getGameover_dislike_count();
    }

    @Override
    public SupplyPlanWrap getSupplyPlans() {
        return SupplyPlanWrap.builder()
                .supplyVolumes(
                        Arrays.stream(SUPPLY_VOLUME.values())
                                .map(SupplyVolumeWrap::new)
                                .collect(Collectors.toList()))
                .supplyCalls(
                        Arrays.stream(SUPPLY_CALL.values())
                                .map(SupplyCallWrap::new)
                                .collect(Collectors.toList()))
                .build();
    }

}
