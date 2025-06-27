package com.sg.obs.dto.inventory;

public interface InventorySummary {

    Long getItemId();
    String getItemName();
    default Integer getRemainingStock() {
        return getTotalTopUp() - getTotalWithdraw();
    }
    Integer getTotalTopUp();
    Integer getTotalWithdraw();

    Integer getWithdrawCount();
    Integer getTopUpCount();
}
