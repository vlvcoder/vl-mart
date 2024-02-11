package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.BUYER_STATUS;
import com.antonov.vlmart.rest.SimpleResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class StatusWrap {
    private boolean gameOver;
    private boolean opened;
    private boolean staffFolderExpanded;
    private long currentMillis;
    private BusinessWrap business;
    private SupplyWrap supply;
    private String addGoodPrice;
    private Map<BUYER_STATUS, Long> buyers;
    private List<PlaceWrap> shopPlaces;
    private List<PlaceWrap> storePlaces;
    private List<MediatorWrap> mediatorWraps;
    private List<QuantWrap> quants;
    private List<StaffWrap> staffs;
    private List<StaffCollapsedWrap> staffsCollapsed;
    private List<GoodWrap> goodsForAdd;
    private List<RaiseWrap> fillRaising;
    private List<RaiseWrap> cashRaising;
    private NotifyWrap notify;
    private SimpleResponse alert;
    private UpgradeWrap upgrade;
}
