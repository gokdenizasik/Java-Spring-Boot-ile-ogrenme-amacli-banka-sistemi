package com.banka.bankapp;

public record TransferSonucu(
        String gonderen,
        String alici,
        double tutar,
        double gonderenYeniBakiye,
        double aliciYeniBakiye) {
}