package com.banka.bankapp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Musteri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kullaniciAdi;
    private String sifreHash;
    private String rol;

    protected Musteri() {
    }

    public Musteri(String kullaniciAdi, String sifreHash, String rol) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifreHash = sifreHash;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public String getSifreHash() {
        return sifreHash;
    }

    public String getRol() {
        return rol;
    }
}