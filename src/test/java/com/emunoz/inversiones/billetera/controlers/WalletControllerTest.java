package com.emunoz.inversiones.billetera.controlers;

import com.emunoz.inversiones.billetera.Validation.ValidationUtils;
import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;
import com.emunoz.inversiones.billetera.services.ValidationTokenService;
import com.emunoz.inversiones.billetera.services.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private ValidationUtils validationUtils;

    @MockBean
    private ValidationTokenService validationTokenService;

    @Test
    void getAllWallet_Unauthorized() throws Exception {

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(false);

        mockMvc.perform(get("/api/V1/wallet").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no válido."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllWallet_NotFound() throws Exception {

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("No hay billeteras creadas.")
                .code(1)
                .build();


        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.getAllWallet()).thenReturn(walletResponseDTO);

        mockMvc.perform(get("/api/V1/wallet").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("No hay billeteras creadas."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllWallet_Success() throws Exception {
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("Billeteras encontradas.")
                .code(2)
                .build();


        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.getAllWallet()).thenReturn(walletResponseDTO);

        mockMvc.perform(get("/api/V1/wallet").header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Billeteras encontradas."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }


    @Test
    void getWalletByUserId_getAllWallet_Unauthorized() throws Exception {

        Long userId = 1L;

        when(validationTokenService.validateTokenUserOrAdmin("0", userId)).thenReturn(false);

        mockMvc.perform(get("/api/V1/wallet/{userId}", userId).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no válido."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWalletByUserId_Success() throws Exception {
        Long userId = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("Billetera encontrada.")
                .code(2)
                .build();


        when(validationTokenService.validateTokenUserOrAdmin("0", userId)).thenReturn(true);
        when(walletService.getWalletByUser(userId)).thenReturn(walletResponseDTO);

        mockMvc.perform(get("/api/V1/wallet/{userId}", userId).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Billetera encontrada."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }



    @Test
    void walletBalanceManager_Unauthorized() throws Exception {

        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(false);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no válido."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void walletBalanceManager_InvalidOption() throws Exception {

        String operation = "operation invalid";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Operación no valida."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isBadRequest());
    }

    @Test
    void walletBalanceManager_WalletNotFound() throws Exception {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera no existe.")
                .code(0)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);
        when(walletService.walletBalanceManager(walletRequestDTO, operation)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera no existe."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isNotFound());
    }

    @Test
    void walletBalanceManager_WalletInactive() throws Exception {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera se encuentra inactiva.")
                .code(1)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);
        when(walletService.walletBalanceManager(walletRequestDTO, operation)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera se encuentra inactiva."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void walletBalanceManager_AddBalance() throws Exception {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("Se ha agregado saldo a la billetera.")
                .code(2)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);
        when(walletService.walletBalanceManager(walletRequestDTO, operation)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Se ha agregado saldo a la billetera."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    @Test
    void walletBalanceManager_SubtractBalance() throws Exception {
        String operation = "subtract";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("Saldo restado con éxito.")
                .code(2)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);
        when(walletService.walletBalanceManager(walletRequestDTO, operation)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Saldo restado con éxito."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    @Test
    void walletBalanceManager_insufficientFunds() throws Exception {
        String operation = "subtract";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(1F)
                .user_id(1L)
                .build();

        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("Saldo insuficiente en la billetera.")
                .code(1)
                .build();

        when(validationTokenService.validateTokenUserOrAdmin("0", walletRequestDTO.getUser_id())).thenReturn(true);
        when(walletService.walletBalanceManager(walletRequestDTO, operation)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(walletRequestDTO)).param("operation", operation).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente en la billetera."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void walletDisabling_Unauthorized() throws Exception {
        Long id = 1L;
        when(validationTokenService.validateTokenAdmin("0")).thenReturn(false);

        mockMvc.perform(put("/api/V1/wallet/disable/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no válido."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void walletDisabling_NotFound() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera no existe.")
                .code(0)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletDisabling(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/disable/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera no existe."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isNotFound());
    }

    @Test
    void walletDisabling_Conflict() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera ya se encuentra inactiva.")
                .code(1)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletDisabling(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/disable/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera ya se encuentra inactiva."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());

    }

    @Test
    void walletDisabling_Success() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera a sido desactivada.")
                .code(2)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletDisabling(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/disable/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera a sido desactivada."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }

    @Test
    void walletActivating_Unauthorized() throws Exception {
        Long id = 1L;
        when(validationTokenService.validateTokenAdmin("0")).thenReturn(false);

        mockMvc.perform(put("/api/V1/wallet/activate/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("Usuario no válido."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void walletActivating_NotFound() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera no existe.")
                .code(0)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletActivating(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/activate/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera no existe."))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(status().isNotFound());
    }

    @Test
    void walletActivating_Conflict() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera ya se encuentra activa.")
                .code(1)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletActivating(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/activate/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera ya se encuentra activa."))
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(status().isConflict());
    }

    @Test
    void walletActivating_Success() throws Exception {
        Long id = 1L;
        WalletResponseDTO walletResponseDTO = WalletResponseDTO.builder()
                .message("La billetera se a activado.")
                .code(2)
                .build();

        when(validationTokenService.validateTokenAdmin("0")).thenReturn(true);
        when(walletService.walletDisabling(id)).thenReturn(walletResponseDTO);

        mockMvc.perform(put("/api/V1/wallet/disable/{id}", id ).header("Authorization","0"))
                .andExpect(jsonPath("$.message").value("La billetera se a activado."))
                .andExpect(jsonPath("$.code").value(2))
                .andExpect(status().isOk());
    }
}