package com.banka.bankapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Hareket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int hesapNo;
    private String tur;

    @Column(precision = 15, scale = 2)
    private BigDecimal tutar;

    @Column(precision = 15, scale = 2)
    private BigDecimal sonrakiBakiye;

    private LocalDateTime zaman;

    protected Hareket() {
    }

    public Hareket(int hesapNo, String tur, BigDecimal tutar, BigDecimal sonrakiBakiye, LocalDateTime zaman) {
        this.hesapNo = hesapNo;
        this.tur = tur;
        this.tutar = tutar;
        this.sonrakiBakiye = sonrakiBakiye;
        this.zaman = zaman;
    }

    public int getHesapNo() {
        return hesapNo;
    }

    public String getTur() {
        return tur;
    }

    public BigDecimal getTutar() {
        return tutar;
    }

    public BigDecimal getSonrakiBakiye() {
        return sonrakiBakiye;
    }

    public LocalDateTime getZaman() {
        return zaman;
    }
}