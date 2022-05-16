package com.example.cursappconvertasynctaskloader;

import androidx.annotation.NonNull;

public class Currency {

    private String ccy;

    private String base_ccy;

    private double buy;

    private double sale;


    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBase_ccy() {
        return base_ccy;
    }

    public void setBase_ccy(String base_ccy) {
        this.base_ccy = base_ccy;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    @NonNull
    @Override
    public String toString() {
        return  ccy;
    }



}