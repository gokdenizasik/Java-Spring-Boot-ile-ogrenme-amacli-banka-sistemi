package com.banka.bankapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IslemAnahtari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String anahtar;
    private String gonderen;
    private String alici;

    @Column(precision = 15, scale = 2)
    private BigDecimal tutar;

    @Column(precision = 15, scale = 2)
    private BigDecimal gonderenYeniBakiye;

    @Column(precision = 15, scale = 2)
    private BigDecimal aliciYeniBakiye;

    private LocalDateTime zaman;

    protected IslemAnahtari() {
    }

    public IslemAnahtari(String anahtar, TransferSonucu sonuc) {
        this.anahtar = anahtar;
        this.gonderen = sonuc.gonderen();
        this.alici = sonuc.alici();
        this.tutar = sonuc.tutar();
        this.gonderenYeniBakiye = sonuc.gonderenYeniBakiye();
        this.aliciYeniBakiye = sonuc.aliciYeniBakiye();
        this.zaman = LocalDateTime.now();
    }

    public TransferSonucu sonucuGetir() {
        return new TransferSonucu(gonderen, alici, tutar, gonderenYeniBakiye, aliciYeniBakiye);
    }
}