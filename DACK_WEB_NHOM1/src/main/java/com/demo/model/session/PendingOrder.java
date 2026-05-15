package com.demo.model.session;

import com.demo.model.cart.GioHangItem;
import java.io.Serializable;
import java.util.List;

public class PendingOrder implements Serializable {

    private String txnRef;
    private long userId;
    private List<GioHangItem> cartItems;
    private long totalAmount;

    // Constructor
    public PendingOrder(String txnRef, long userId, List<GioHangItem> cartItems, long totalAmount) {
        this.txnRef = txnRef;
        this.userId = userId;
        this.cartItems = cartItems;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<GioHangItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<GioHangItem> cartItems) {
        this.cartItems = cartItems;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
