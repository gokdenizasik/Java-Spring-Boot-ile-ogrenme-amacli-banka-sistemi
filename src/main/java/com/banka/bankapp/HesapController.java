package com.banka.bankapp;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HesapController {

    private final HesapRepository hesapRepository;
    private final HareketRepository hareketRepository;

    public HesapController(HesapRepository hesapRepository, HareketRepository hareketRepository) {
        this.hesapRepository = hesapRepository;
        this.hareketRepository = hareketRepository;
    }

    private void hareketKaydet(Hesap hesap, String tur, double tutar) {
        Hareket hareket = new Hareket(
                hesap.getHesapNo(),
                tur,
                tutar,
                hesap.getBakiye(),
                LocalDateTime.now());
        hareketRepository.save(hareket);
    }

    private Hesap hesapBul(int hesapNo) {
        return hesapRepository.findByHesapNo(hesapNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hesap bulunamadi"));
    }

    @GetMapping("/hesaplar")
    public List<Hesap> tumHesaplar() {
        return hesapRepository.findAll();
    }

    @GetMapping("/hesaplar/{hesapNo}")
    public Hesap hesapGetir(@PathVariable int hesapNo) {
        return hesapBul(hesapNo);
    }

    @GetMapping("/hesaplar/{hesapNo}/hareketler")
    public List<Hareket> hesapHareketleri(@PathVariable int hesapNo) {
        hesapBul(hesapNo);
        return hareketRepository.findByHesapNoOrderByIdAsc(hesapNo);
    }

    @PostMapping("/hesaplar")
    @Transactional
    public Hesap hesapAc(@RequestBody HesapOlusturIstegi istek) {
        int yeniHesapNo = 1001 + (int) hesapRepository.count();
        Hesap yeniHesap = hesapRepository.save(new Hesap(istek.sahibi(), yeniHesapNo, istek.bakiye()));
        hareketKaydet(yeniHesap, "ACILIS", istek.bakiye());
        return yeniHesap;
    }

    @PostMapping("/hesaplar/{hesapNo}/yatir")
    @Transactional
    public Hesap paraYatir(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        if (istek.tutar() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        Hesap hesap = hesapBul(hesapNo);
        hesap.paraYatir(istek.tutar());
        hesapRepository.save(hesap);
        hareketKaydet(hesap, "YATIRMA", istek.tutar());
        return hesap;
    }

    @PostMapping("/hesaplar/{hesapNo}/cek")
    @Transactional
    public Hesap paraCek(@PathVariable int hesapNo, @RequestBody TutarIstegi istek) {
        if (istek.tutar() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        Hesap hesap = hesapBul(hesapNo);
        boolean basarili = hesap.paraCek(istek.tutar());
        if (!basarili) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yetersiz bakiye");
        }
        hesapRepository.save(hesap);
        hareketKaydet(hesap, "CEKME", istek.tutar());
        return hesap;
    }

    @PostMapping("/transferler")
    @Transactional
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
        hesapRepository.save(kaynak);
        hareketKaydet(kaynak, "TRANSFER_GIDEN", istek.tutar());

        

        hedef.paraYatir(istek.tutar());
        hesapRepository.save(hedef);
        hareketKaydet(hedef, "TRANSFER_GELEN", istek.tutar());

        return new TransferSonucu(
                kaynak.getSahibi(),
                hedef.getSahibi(),
                istek.tutar(),
                kaynak.getBakiye(),
                hedef.getBakiye());
    }
}