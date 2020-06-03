package com.ahmed.homeservices.models;

import com.ahmed.homeservices.models.orders.OrderRequest;

public class MsgEvtEditOrder {

    private boolean edit;
    private OrderRequest orderRequest;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public MsgEvtEditOrder(boolean edit, OrderRequest orderRequest) {
        this.edit = edit;
        this.orderRequest = orderRequest;
    }


    //
//    public MsgEvtEditOrder(OrderRequest orderRequest) {
//        this.orderRequest = orderRequest;
//    }

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }
}
