package com.banka.bankapp;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long gecerlilikSaniye;

    public JwtService(@Value("${jwt.secret}") String gizliAnahtar,
                      @Value("${jwt.gecerlilik-dakika}") long gecerlilikDakika) {
        byte[] bayt = gizliAnahtar.getBytes(StandardCharsets.UTF_8);
        if (bayt.length < 32) {
            throw new IllegalStateException("jwt.secret en az 32 karakter olmali");
        }
        SecretKey anahtar = new SecretKeySpec(bayt, "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(anahtar));
        this.decoder = NimbusJwtDecoder.withSecretKey(anahtar).macAlgorithm(MacAlgorithm.HS256).build();
        this.gecerlilikSaniye = gecerlilikDakika * 60;
    }

    public long getGecerlilikSaniye() {
        return gecerlilikSaniye;
    }

    public String tokenUret(MusteriOzet musteri) {
        Instant simdi = Instant.now();
        JwtClaimsSet talepler = JwtClaimsSet.builder()
                .subject(musteri.kullaniciAdi())
                .claim("musteriId", musteri.id())
                .claim("rol", musteri.rol())
                .issuedAt(simdi)
                .expiresAt(simdi.plusSeconds(gecerlilikSaniye))
                .build();
        JwsHeader baslik = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(baslik, talepler)).getTokenValue();
    }

    public Jwt dogrula(String token) {
        try {
            return decoder.decode(token);
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gecersiz ya da suresi dolmus token");
        }
    }
}