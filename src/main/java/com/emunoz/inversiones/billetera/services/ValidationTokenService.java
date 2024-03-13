package com.emunoz.inversiones.billetera.services;

import com.emunoz.inversiones.billetera.feignClient.ValidationTokenClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ValidationTokenService {


    private final ValidationTokenClient validationTokenClient;

    public ValidationTokenService(ValidationTokenClient validationTokenClient) {
        this.validationTokenClient = validationTokenClient;
    }

    public boolean validateTokenUserOrAdmin(String token, Long userId) {

        return validationTokenClient.validateTokenUserOrAdmin(userId, token);

    }

    public boolean validateTokenAdmin(String token) {

        return validationTokenClient.validateTokenAdmin(token);

    }
}
