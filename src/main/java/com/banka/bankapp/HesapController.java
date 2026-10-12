package com.banka.bankapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HesapController {

    private final List<Hesap> hesaplar = new ArrayList<>();
    private final List<Hareket> hareketler = new ArrayList<>();

    public HesapController() {
        hesapEkle("Ayse", 1001, 500.0);
        hesapEkle("Mehmet", 1002, 200.0);
        hesapEkle("Zeynep", 1003, 1000.0);
    }

    private Hesap hesapEkle(String sahibi, int hesapNo, double bakiye) {
        Hesap hesap = new Hesap(sahibi, hesapNo, bakiye);
        hesaplar.add(hesap);
        hareketKaydet(hesap, "ACILIS", bakiye);
        return hesap;
    }

    private void hareketKaydet(Hesap hesap, String tur, double tutar) {
        Hareket hareket = new Hareket(
                hesap.getHesapNo(),
                tur,
                tutar,
                hesap.getBakiye(),
                LocalDateTime.now().toString());
        hareketler.add(hareket);
    }

    private Hesap hesapBul(int hesapNo) {
        for (Hesap hesap : hesaplar) {
            if (hesap.getHesapNo() == hesapNo) {
                return hesap;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hesap bulunamadi");
    }

    @GetMapping("/hesaplar")
    public List<Hesap> tumHesaplar() {
        return hesaplar;
    }

    @GetMapping("/hesaplar/{hesapNo}")
    public Hesap hesapGetir(@PathVariable int hesapNo) {
        return hesapBul(hesapNo);
    }

    @GetMapping("/hesaplar/{hesapNo}/hareketler")
    public List<Hareket> hesapHareketleri(@PathVariable int hesapNo) {
        hesapBul(hesapNo);
        List<Hareket> sonuc = new ArrayList<>();
        for (Hareket hareket : hareketler) {
            if (hareket.hesapNo() == hesapNo) {
                sonuc.add(hareket);
            }
        }
        return sonuc;
    }

    @PostMapping("/hesaplar")
    public Hesap hesapAc(@RequestBody HesapOlusturIstegi istek) {
        int yeniHesapNo = 1001 + hesaplar.size();
        return hesapEkle(istek.sahibi(), yeniHesapNo, istek.bakiye());
    }

    @PostMapping("/hesaplar/{hesapNo}/yatir")
    public Hesap paraYatir(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        if (istek.tutar() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        Hesap hesap = hesapBul(hesapNo);
        hesap.paraYatir(istek.tutar());
        hareketKaydet(hesap, "YATIRMA", istek.tutar());
        return hesap;
    }

    @PostMapping("/hesaplar/{hesapNo}/cek")
    public Hesap paraCek(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        if (istek.tutar() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        Hesap hesap = hesapBul(hesapNo);
        boolean basarili = hesap.paraCek(istek.tutar());
        if (!basarili) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yetersiz bakiye");
        }
        hareketKaydet(hesap, "CEKME", istek.tutar());
        return hesap;
    }

    @PostMapping("/transferler")
    public TransferSonucu transfer(@RequestBody TransferIstegi istek) {
        if (istek.tutar() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        if (istek.kaynakHesapNo() == istek.hedefHesapNo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ayni hesaba transfer yapilamaz");
        }

        Hesap kaynak = hesapBul(istek.kaynakHesapNo());
        Hesap hedef = hesapBul(istek.hedefHesapNo());

        boolean basarili = kaynak.paraCek(istek.tutar());
        if (!basarili) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yetersiz bakiye");
        }
        hareketKaydet(kaynak, "TRANSFER_GIDEN", istek.tutar());

        hedef.paraYatir(istek.tutar());
        hareketKaydet(hedef, "TRANSFER_GELEN", istek.tutar());

        return new TransferSonucu(
                kaynak.getSahibi(),
                hedef.getSahibi(),
                istek.tutar(),
                kaynak.getBakiye(),
                hedef.getBakiye());
    }
}