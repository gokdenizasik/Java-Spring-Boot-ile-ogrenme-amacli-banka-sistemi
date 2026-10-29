package com.banka.bankapp;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    public static final String MUSTERI_ID = "musteriId";
    public static final String KULLANICI_ADI = "kullaniciAdi";
    public static final String ROL = "rol";

    private final JwtService jwtService;

    public JwtInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String baslik = request.getHeader("Authorization");
        if (baslik == null || !baslik.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Giris yapmaniz gerekiyor");
        }

        Jwt jwt = jwtService.dogrula(baslik.substring(7).trim());

        Number musteriId = jwt.getClaim("musteriId");
        if (musteriId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gecersiz token");
        }

        request.setAttribute(MUSTERI_ID, musteriId.longValue());
        request.setAttribute(KULLANICI_ADI, jwt.getSubject());
        request.setAttribute(ROL, jwt.getClaimAsString("rol"));
        return true;
    }
}