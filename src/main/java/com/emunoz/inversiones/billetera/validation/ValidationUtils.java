package com.emunoz.inversiones.billetera.validation;

import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class ValidationUtils {

    public ResponseEntity<WalletResponseDTO> handleValidationErrors(BindingResult bindingResult) {
        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }

            for (ObjectError error : bindingResult.getGlobalErrors()) {
                errors.add(error.getDefaultMessage());
            }
            walletResponseDTO.setMessage("Campos vacios");
            walletResponseDTO.setData(errors);
            walletResponseDTO.setCode(0);
            return new ResponseEntity<>(walletResponseDTO, HttpStatus.BAD_REQUEST);
        }

        return null; // No hay errores de validación
    }

}
