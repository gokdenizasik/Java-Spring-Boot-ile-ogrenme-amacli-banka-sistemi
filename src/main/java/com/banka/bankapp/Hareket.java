package com.banka.bankapp;

public record Hareket(
        int hesapNo,
        String tur,
        double tutar,
        double sonrakiBakiye,
        String zaman) {
}