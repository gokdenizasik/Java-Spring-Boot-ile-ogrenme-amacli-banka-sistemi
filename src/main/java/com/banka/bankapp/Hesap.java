package com.banka.bankapp;

public class Hesap {

    private String sahibi;
    private int hesapNo;
    private double bakiye;

    public Hesap(String sahibi, int hesapNo, double bakiye) {
        this.sahibi = sahibi;
        this.hesapNo = hesapNo;
        this.bakiye = bakiye;
    }

    public String getSahibi() {
        return sahibi;
    }

    public int getHesapNo() {
        return hesapNo;
    }

    public double getBakiye() {
        return bakiye;
    }

    public void paraYatir(double tutar) {
        bakiye = bakiye + tutar;
    }

    public boolean paraCek(double tutar) {
        if (tutar > bakiye) {
            return false;
        }
        bakiye = bakiye - tutar;
        return true;
    }
}