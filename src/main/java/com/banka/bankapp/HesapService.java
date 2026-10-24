package com.banka.bankapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HesapService {

    private final HesapRepository hesapRepository;
    private final HareketRepository hareketRepository;
    private final IslemAnahtariRepository islemAnahtariRepository;

    public HesapService(HesapRepository hesapRepository,
                        HareketRepository hareketRepository,
                        IslemAnahtariRepository islemAnahtariRepository) {
        this.hesapRepository = hesapRepository;
        this.hareketRepository = hareketRepository;
        this.islemAnahtariRepository = islemAnahtariRepository;
    }

    private void tutarKontrol(BigDecimal tutar) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar sifirdan buyuk olmali");
        }
        if (tutar.stripTrailingZeros().scale() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutar en fazla 2 ondalik basamak icerebilir");
        }
    }

    private void hareketKaydet(Hesap hesap, String tur, BigDecimal tutar) {
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

    private Hesap hesapBulKilitli(int hesapNo) {
        return hesapRepository.findWithLockByHesapNo(hesapNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hesap bulunamadi"));
    }

    public List<Hesap> tumHesaplar() {
        return hesapRepository.findAll();
    }

    public Hesap hesapGetir(int hesapNo) {
        return hesapBul(hesapNo);
    }

    public List<Hareket> hareketler(int hesapNo) {
        hesapBul(hesapNo);
        return hareketRepository.findByHesapNoOrderByIdAsc(hesapNo);
    }

    @Transactional
    public Hesap hesapAc(String sahibi, BigDecimal bakiye) {
        if (sahibi == null || sahibi.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hesap sahibi bos olamaz");
        }
        if (bakiye == null || bakiye.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acilis bakiyesi negatif olamaz");
        }
        int yeniHesapNo = 1001 + (int) hesapRepository.count();
        Hesap yeniHesap = hesapRepository.save(new Hesap(sahibi, yeniHesapNo, bakiye));
        hareketKaydet(yeniHesap, "ACILIS", bakiye);
        return yeniHesap;
    }

    @Transactional
    public Hesap paraYatir(int hesapNo, BigDecimal tutar) {
        tutarKontrol(tutar);
        Hesap hesap = hesapBulKilitli(hesapNo);
        hesap.paraYatir(tutar);
        hesapRepository.save(hesap);
        hareketKaydet(hesap, "YATIRMA", tutar);
        return hesap;
    }

    @Transactional
    public Hesap paraCek(int hesapNo, BigDecimal tutar) {
        tutarKontrol(tutar);
        Hesap hesap = hesapBulKilitli(hesapNo);
        boolean basarili = hesap.paraCek(tutar);
        if (!basarili) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yetersiz bakiye");
        }
        hesapRepository.save(hesap);
        hareketKaydet(hesap, "CEKME", tutar);
        return hesap;
    }

    @Transactional
    public TransferSonucu transfer(int kaynakHesapNo, int hedefHesapNo, BigDecimal tutar, String anahtar) {
        boolean anahtarVar = anahtar != null && !anahtar.isBlank();

        if (anahtarVar && anahtar.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency-Key en fazla 100 karakter olabilir");
        }

        // Ayni anahtar daha once islendiyse transferi tekrarlama, ilk sonucu don
        if (anahtarVar) {
            Optional<IslemAnahtari> kayit = islemAnahtariRepository.findByAnahtar(anahtar);
            if (kayit.isPresent()) {
                return kayit.get().sonucuGetir();
            }
        }

        TransferSonucu sonuc = transferUygula(kaynakHesapNo, hedefHesapNo, tutar);

        if (anahtarVar) {
            islemAnahtariRepository.save(new IslemAnahtari(anahtar, sonuc));
        }
        return sonuc;
    }

    private TransferSonucu transferUygula(int kaynakHesapNo, int hedefHesapNo, BigDecimal tutar) {
        tutarKontrol(tutar);
        if (kaynakHesapNo == hedefHesapNo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ayni hesaba transfer yapilamaz");
        }

        // Kilitleri her zaman kucuk hesap numarasindan baslayarak al (kilitlenme/deadlock onlemi)
        int kucukNo = Math.min(kaynakHesapNo, hedefHesapNo);
        int buyukNo = Math.max(kaynakHesapNo, hedefHesapNo);
        Hesap kucukHesap = hesapBulKilitli(kucukNo);
        Hesap buyukHesap = hesapBulKilitli(buyukNo);

        Hesap kaynak = (kaynakHesapNo == kucukNo) ? kucukHesap : buyukHesap;
        Hesap hedef = (hedefHesapNo == kucukNo) ? kucukHesap : buyukHesap;

        boolean basarili = kaynak.paraCek(tutar);
        if (!basarili) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yetersiz bakiye");
        }
        hesapRepository.save(kaynak);
        hareketKaydet(kaynak, "TRANSFER_GIDEN", tutar);

        hedef.paraYatir(tutar);
        hesapRepository.save(hedef);
        hareketKaydet(hedef, "TRANSFER_GELEN", tutar);

        return new TransferSonucu(
                kaynak.getSahibi(),
                hedef.getSahibi(),
                tutar,
                kaynak.getBakiye(),
                hedef.getBakiye());
    }
}