package com.banka.bankapp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HesapRepository extends JpaRepository<Hesap, Long> {

    Optional<Hesap> findByHesapNo(int hesapNo);
}