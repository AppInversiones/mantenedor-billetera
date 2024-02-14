package com.emunoz.inversiones.billetera.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "acceso", url = "http://localhost:8080")
public interface ValidationTokenClient {


    @GetMapping("/api/V1/validate-token/validate")
    boolean validateTokenAdmin(@RequestHeader(name = "Authorization") String token);
    @GetMapping("/api/V1/validate-token/validate-user-or-admin/{usuarioId}")
    boolean validateTokenUserOrAdmin(@PathVariable("usuarioId") Long userId, @RequestHeader(name = "Authorization") String token);
}
