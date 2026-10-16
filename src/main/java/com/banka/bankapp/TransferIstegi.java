package com.banka.bankapp;

import java.math.BigDecimal;

public record TransferIstegi(int kaynakHesapNo, int hedefHesapNo, BigDecimal tutar) {
}