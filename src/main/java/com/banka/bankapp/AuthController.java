package com.banka.bankapp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final MusteriService musteriService;
    private final JwtService jwtService;

    public AuthController(MusteriService musteriService, JwtService jwtService) {
        this.musteriService = musteriService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/kayit")
    public MusteriOzet kayit(@RequestBody KimlikIstegi istek) {
        return musteriService.kayit(istek.kullaniciAdi(), istek.sifre());
    }

    @PostMapping("/auth/giris")
    public GirisSonucu giris(@RequestBody KimlikIstegi istek) {
        MusteriOzet musteri = musteriService.giris(istek.kullaniciAdi(), istek.sifre());
        String token = jwtService.tokenUret(musteri);
        return new GirisSonucu(token, "Bearer", jwtService.getGecerlilikSaniye(), musteri);
    }
}