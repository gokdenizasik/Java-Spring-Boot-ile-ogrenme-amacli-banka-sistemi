package com.banka.bankapp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface HesapRepository extends JpaRepository<Hesap, Long> {

    Optional<Hesap> findByHesapNo(int hesapNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Hesap> findWithLockByHesapNo(int hesapNo);
}