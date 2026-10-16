package com.banka.bankapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HareketRepository extends JpaRepository<Hareket, Long> {

    List<Hareket> findByHesapNoOrderByIdAsc(int hesapNo);
}