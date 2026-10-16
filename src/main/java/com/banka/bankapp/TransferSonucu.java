package com.banka.bankapp;

import java.math.BigDecimal;

public record TransferSonucu(
        String gonderen,
        String alici,
        BigDecimal tutar,
        BigDecimal gonderenYeniBakiye,
        BigDecimal aliciYeniBakiye) {
}