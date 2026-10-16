package com.banka.bankapp;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HesapController {

    private final HesapService hesapService;

    public HesapController(HesapService hesapService) {
        this.hesapService = hesapService;
    }

    @GetMapping("/hesaplar")
    public List<Hesap> tumHesaplar() {
        return hesapService.tumHesaplar();
    }

    @GetMapping("/hesaplar/{hesapNo}")
    public Hesap hesapGetir(@PathVariable int hesapNo) {
        return hesapService.hesapGetir(hesapNo);
    }

    @GetMapping("/hesaplar/{hesapNo}/hareketler")
    public List<Hareket> hesapHareketleri(@PathVariable int hesapNo) {
        return hesapService.hareketler(hesapNo);
    }

    @PostMapping("/hesaplar")
    public Hesap hesapAc(@RequestBody HesapOlusturIstegi istek) {
        return hesapService.hesapAc(istek.sahibi(), istek.bakiye());
    }

    @PostMapping("/hesaplar/{hesapNo}/yatir")
    public Hesap paraYatir(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        return hesapService.paraYatir(hesapNo, istek.tutar());
    }

    @PostMapping("/hesaplar/{hesapNo}/cek")
    public Hesap paraCek(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        return hesapService.paraCek(hesapNo, istek.tutar());
    }

    @PostMapping("/transferler")
    public TransferSonucu transfer(@RequestBody TransferIstegi istek) {
        return hesapService.transfer(istek.kaynakHesapNo(), istek.hedefHesapNo(), istek.tutar());
    }
}