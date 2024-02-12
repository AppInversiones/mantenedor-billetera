package com.emunoz.inversiones.billetera.controlers;

import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;
import com.emunoz.inversiones.billetera.services.WalletService;
import com.emunoz.inversiones.billetera.util.JWTUtil;
import com.emunoz.inversiones.billetera.validation.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "api/V1/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private JWTUtil jwtUtil;


    @Operation(summary = "Servicio que lista todas las billeteras")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Billeteras encontrados", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
                    }),
                    @ApiResponse(responseCode = "204", description = "No se encontraron billeteras", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
            }
    )
    @GetMapping
    public ResponseEntity<WalletResponseDTO> getAllWallet(@RequestHeader(name = "Authorization") String token) {

        if(jwtUtil.getPermission(token) != 2) {
            WalletResponseDTO res = new WalletResponseDTO();
            res.setMessage("Usuario no autorizado.");
            res.setCode(0);
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }
        WalletResponseDTO res = walletService.getAllWallet();

        if (res.getCode() == 1){
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     //--------------

    @Operation(summary = "Servicio que lista la billetera de usuario por su user_id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "billetera encontrada.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
                    }),
                    @ApiResponse(responseCode = "204", description = "No se encontro la billetera.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Error de autorización.", content = @Content),
            }
    )
    @GetMapping (path = "{userId}")
    public ResponseEntity<WalletResponseDTO>getWalletByUserId(@PathVariable("userId") Long id) {

        WalletResponseDTO res = walletService.getWalletByUser(id);

        if (res.getCode() == 1){
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else if (res.getCode() == 2){
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //--------------
     @Operation(summary = "Agregar o restar saldo a la billetera")
     @ApiResponses(
             value = {
                     @ApiResponse(responseCode = "201", description = "Agregado o retirado saldo con exito", content = @Content),
                     @ApiResponse(responseCode = "400", description = "Solicitud no válida", content = @Content),
                     @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content),
             }
     )
     @PutMapping
     public ResponseEntity<WalletResponseDTO>  walletBalanceManager(@Validated @RequestBody WalletRequestDTO walletRequestDTO, BindingResult bindingResult, @RequestParam String operation, @RequestHeader(name = "Authorization") String token) {

         ResponseEntity<WalletResponseDTO> validationError = validationUtils.handleValidationErrors (bindingResult);
         if (validationError != null) {
             return validationError;
         }

         if (!"add".equals(operation) && !"subtract".equals(operation)) {
             WalletResponseDTO walletResponseDTO = new WalletResponseDTO();
             walletResponseDTO.setMessage("Operación no valida");
             walletResponseDTO.setCode(0);
             return new ResponseEntity<>(walletResponseDTO, HttpStatus.BAD_REQUEST);
         }

         WalletResponseDTO res = walletService.walletBalanceManager(walletRequestDTO, operation);

         if (res.getCode() == 2) {
             return new ResponseEntity<>(res, HttpStatus.OK);
         } else if (res.getCode() == 1) {
             return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
         }else if (res.getCode() == 0) {
             return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
         }

         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
     }
}
