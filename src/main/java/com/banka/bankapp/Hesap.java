package com.banka.bankapp;

import java.math.BigDecimal;

import jakarta.persistence.Column;
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

    @Column(precision = 15, scale = 2)
    private BigDecimal bakiye;

    protected Hesap() {
    }

    public Hesap(String sahibi, int hesapNo, BigDecimal bakiye) {
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

    public BigDecimal getBakiye() {
        return bakiye;
    }

    public void paraYatir(BigDecimal tutar) {
        bakiye = bakiye.add(tutar);
    }

    public boolean paraCek(BigDecimal tutar) {
        if (tutar.compareTo(bakiye) > 0) {
            return false;
        }
        bakiye = bakiye.subtract(tutar);
        return true;
    }
}