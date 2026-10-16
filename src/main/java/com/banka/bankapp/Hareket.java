package com.banka.bankapp;

import java.time.LocalDateTime;

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
    private double tutar;
    private double sonrakiBakiye;
    private LocalDateTime zaman;

    protected Hareket() {
    }

    public Hareket(int hesapNo, String tur, double tutar, double sonrakiBakiye, LocalDateTime zaman) {
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

    public double getTutar() {
        return tutar;
    }

    public double getSonrakiBakiye() {
        return sonrakiBakiye;
    }

    public LocalDateTime getZaman() {
        return zaman;
    }
}