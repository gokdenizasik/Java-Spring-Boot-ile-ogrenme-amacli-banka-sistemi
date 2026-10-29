package com.banka.bankapp;

public record GirisSonucu(String token, String tip, long gecerlilikSaniye, MusteriOzet musteri) {
}