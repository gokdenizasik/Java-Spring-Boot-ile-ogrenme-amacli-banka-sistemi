package com.banka.bankapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class HesapTest {

    @Test
    void paraYatirBakiyeyiArtirir() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));

        hesap.paraYatir(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("600.00"), hesap.getBakiye());
    }

    @Test
    void yeterliBakiyedeParaCekmeBasarili() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));

        boolean sonuc = hesap.paraCek(new BigDecimal("200.00"));

        assertTrue(sonuc);
        assertEquals(new BigDecimal("300.00"), hesap.getBakiye());
    }

    @Test
    void yetersizBakiyedeParaCekmeReddedilirVeBakiyeDegismez() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));

        boolean sonuc = hesap.paraCek(new BigDecimal("600.00"));

        assertFalse(sonuc);
        assertEquals(new BigDecimal("500.00"), hesap.getBakiye());
    }

    @Test
    void tumBakiyeyiCekmekMumkun() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));

        boolean sonuc = hesap.paraCek(new BigDecimal("500.00"));

        assertTrue(sonuc);
        assertEquals(new BigDecimal("0.00"), hesap.getBakiye());
    }
}