package com.banka.bankapp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final MusteriService musteriService;

    public AuthController(MusteriService musteriService) {
        this.musteriService = musteriService;
    }

    @PostMapping("/auth/kayit")
    public MusteriOzet kayit(@RequestBody KimlikIstegi istek) {
        return musteriService.kayit(istek.kullaniciAdi(), istek.sifre());
    }

    @PostMapping("/auth/giris")
    public MusteriOzet giris(@RequestBody KimlikIstegi istek) {
        return musteriService.giris(istek.kullaniciAdi(), istek.sifre());
    }
}