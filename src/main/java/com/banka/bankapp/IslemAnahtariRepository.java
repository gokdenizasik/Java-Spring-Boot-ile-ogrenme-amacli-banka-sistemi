package com.banka.bankapp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IslemAnahtariRepository extends JpaRepository<IslemAnahtari, Long> {

    Optional<IslemAnahtari> findByAnahtar(String anahtar);
}