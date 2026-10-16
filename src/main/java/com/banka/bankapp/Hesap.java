package com.banka.bankapp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Hesap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sahibi;
    private int hesapNo;
    private double bakiye;

    protected Hesap() {
    }

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