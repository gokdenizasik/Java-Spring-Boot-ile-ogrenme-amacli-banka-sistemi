package com.banka.bankapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class HesapServiceTest {

    private HesapRepository hesapRepository;
    private HareketRepository hareketRepository;
    private IslemAnahtariRepository islemAnahtariRepository;
    private HesapService hesapService;

    @BeforeEach
    void hazirla() {
        hesapRepository = mock(HesapRepository.class);
        hareketRepository = mock(HareketRepository.class);
        islemAnahtariRepository = mock(IslemAnahtariRepository.class);
        hesapService = new HesapService(hesapRepository, hareketRepository, islemAnahtariRepository);
    }

    @Test
    void negatifTutarYatirmaReddedilir() {
        ResponseStatusException hata = assertThrows(ResponseStatusException.class,
                () -> hesapService.paraYatir(1001, new BigDecimal("-5.00")));

        assertEquals(400, hata.getStatusCode().value());
        verify(hesapRepository, never()).save(any(Hesap.class));
    }

    @Test
    void ikidenFazlaOndalikliTutarReddedilir() {
        ResponseStatusException hata = assertThrows(ResponseStatusException.class,
                () -> hesapService.paraYatir(1001, new BigDecimal("10.555")));

        assertEquals(400, hata.getStatusCode().value());
    }

    @Test
    void olmayanHesapIcin404Doner() {
        when(hesapRepository.findWithLockByHesapNo(9999)).thenReturn(Optional.empty());

        ResponseStatusException hata = assertThrows(ResponseStatusException.class,
                () -> hesapService.paraCek(9999, new BigDecimal("10.00")));

        assertEquals(404, hata.getStatusCode().value());
    }

    @Test
    void yetersizBakiyedeCekmeReddedilirVeKayitYazilmaz() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));
        when(hesapRepository.findWithLockByHesapNo(1001)).thenReturn(Optional.of(hesap));

        ResponseStatusException hata = assertThrows(ResponseStatusException.class,
                () -> hesapService.paraCek(1001, new BigDecimal("600.00")));

        assertEquals(400, hata.getStatusCode().value());
        assertEquals(new BigDecimal("500.00"), hesap.getBakiye());
        verify(hesapRepository, never()).save(any(Hesap.class));
        verify(hareketRepository, never()).save(any(Hareket.class));
    }

    @Test
    void basariliCekmeBakiyeyiDusururVeHareketYazar() {
        Hesap hesap = new Hesap("Ayse", 1001, new BigDecimal("500.00"));
        when(hesapRepository.findWithLockByHesapNo(1001)).thenReturn(Optional.of(hesap));

        Hesap sonuc = hesapService.paraCek(1001, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("300.00"), sonuc.getBakiye());
        verify(hareketRepository, times(1)).save(any(Hareket.class));
    }

    @Test
    void transferIkiBakiyeyiDogruDegistirir() {
        Hesap ayse = new Hesap("Ayse", 1001, new BigDecimal("500.00"));
        Hesap mehmet = new Hesap("Mehmet", 1002, new BigDecimal("200.00"));
        when(hesapRepository.findWithLockByHesapNo(1001)).thenReturn(Optional.of(ayse));
        when(hesapRepository.findWithLockByHesapNo(1002)).thenReturn(Optional.of(mehmet));

        TransferSonucu sonuc = hesapService.transfer(1001, 1002, new BigDecimal("50.00"), null);

        assertEquals(new BigDecimal("450.00"), sonuc.gonderenYeniBakiye());
        assertEquals(new BigDecimal("250.00"), sonuc.aliciYeniBakiye());
        verify(hareketRepository, times(2)).save(any(Hareket.class));
    }

    @Test
    void ayniHesabaTransferReddedilir() {
        ResponseStatusException hata = assertThrows(ResponseStatusException.class,
                () -> hesapService.transfer(1001, 1001, new BigDecimal("10.00"), null));

        assertEquals(400, hata.getStatusCode().value());
        verify(hesapRepository, never()).findWithLockByHesapNo(anyInt());
    }

    @Test
    void ayniAnahtarlaGelenTransferTekrarlanmaz() {
        TransferSonucu ilkSonuc = new TransferSonucu(
                "Ayse", "Mehmet",
                new BigDecimal("50.00"),
                new BigDecimal("450.00"),
                new BigDecimal("250.00"));
        when(islemAnahtariRepository.findByAnahtar("anahtar-1"))
                .thenReturn(Optional.of(new IslemAnahtari("anahtar-1", ilkSonuc)));

        TransferSonucu sonuc = hesapService.transfer(1001, 1002, new BigDecimal("50.00"), "anahtar-1");

        assertEquals(new BigDecimal("450.00"), sonuc.gonderenYeniBakiye());
        verify(hesapRepository, never()).findWithLockByHesapNo(anyInt());
        verify(hareketRepository, never()).save(any(Hareket.class));
    }
}