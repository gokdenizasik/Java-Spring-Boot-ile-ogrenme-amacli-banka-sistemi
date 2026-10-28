package com.banka.bankapp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MusteriRepository extends JpaRepository<Musteri, Long> {

    Optional<Musteri> findByKullaniciAdi(String kullaniciAdi);
}