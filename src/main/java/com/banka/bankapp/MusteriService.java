package com.banka.bankapp;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MusteriService {

    private final MusteriRepository musteriRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public MusteriService(MusteriRepository musteriRepository) {
        this.musteriRepository = musteriRepository;
    }

    private MusteriOzet ozet(Musteri musteri) {
        return new MusteriOzet(musteri.getId(), musteri.getKullaniciAdi(), musteri.getRol());
    }

    @Transactional
    public MusteriOzet kayit(String kullaniciAdi, String sifre) {
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kullanici adi bos olamaz");
        }
        String ad = kullaniciAdi.trim();
        if (ad.length() < 3 || ad.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kullanici adi 3-50 karakter olmali");
        }
        if (sifre == null || sifre.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sifre en az 8 karakter olmali");
        }
        if (sifre.length() > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sifre en fazla 72 karakter olabilir");
        }
        if (musteriRepository.findByKullaniciAdi(ad).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu kullanici adi zaten kayitli");
        }

        String hash = encoder.encode(sifre);
        try {
            Musteri musteri = musteriRepository.saveAndFlush(new Musteri(ad, hash, "MUSTERI"));
            return ozet(musteri);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu kullanici adi zaten kayitli");
        }
    }

    public MusteriOzet giris(String kullaniciAdi, String sifre) {
        if (kullaniciAdi == null || sifre == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanici adi veya sifre hatali");
        }
        Optional<Musteri> kayit = musteriRepository.findByKullaniciAdi(kullaniciAdi.trim());
        if (kayit.isEmpty() || !encoder.matches(sifre, kayit.get().getSifreHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanici adi veya sifre hatali");
        }
        return ozet(kayit.get());
    }
}