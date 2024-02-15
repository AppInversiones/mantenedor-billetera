package com.emunoz.inversiones.billetera.services;

import com.emunoz.inversiones.billetera.models.entity.WalletEntity;
import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;
import com.emunoz.inversiones.billetera.repository.WalletRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletService;
    @Mock
    private WalletRepository walletRepository;

    @Test
    public void getAllWallet() {

        when(walletRepository.findAll()).thenReturn(Lists.newArrayList());
        WalletResponseDTO rsp = walletService.getAllWallet();

        assertEquals("No hay billeteras creadas.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);
    }

    @Test
    void getAllWallet_WalletFound() {

        when(walletRepository.findAll()).thenReturn(Lists.newArrayList(WalletEntity.builder().build()));
        WalletResponseDTO rsp = walletService.getAllWallet();

        assertEquals("Billeteras encontradas.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);
    }


    @Test
    void getWalletByUser_Success() {
        long userId = 1L;

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(WalletEntity.builder().build()));
        WalletResponseDTO rsp = walletService.getWalletByUser(userId);

        assertEquals("Billetera encontrada.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);
    }

    @Test
    void getWalletByUser_CreateAndSuccess() {
        long userId = 1L;

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        WalletResponseDTO rsp = walletService.getWalletByUser(userId);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));

        assertEquals("Billetera encontrada.", rsp.getMessage());
        assertEquals(2, rsp.getCode());
    }

    @Test
    void walletBalanceManager_NotFound() {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        when(walletRepository.findByUserId(walletRequestDTO.getUser_id())).thenReturn(Optional.empty());
        WalletResponseDTO rsp = walletService.walletBalanceManager(walletRequestDTO, operation);

        assertEquals("La billetera no existe.", rsp.getMessage());
        assertEquals(rsp.getCode(), 0);
    }

    @Test
    void walletBalanceManager_WalletInactive() {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(1000F)
                .state("inactiva")
                .userId(1L)
                .build();

        when(walletRepository.findByUserId(walletRequestDTO.getUser_id())).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletBalanceManager(walletRequestDTO, operation);

        assertEquals("La billetera se encuentra inactiva.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);
    }

    @Test
    void walletDisabling_AddSuccess() {
        String operation = "add";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(1000F)
                .state("activa")
                .userId(1L)
                .build();

        when(walletRepository.findByUserId(walletRequestDTO.getUser_id())).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletBalanceManager(walletRequestDTO, operation);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));

        assertEquals("Se ha agregado saldo a la billetera.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);
    }

    @Test
    void walletActivating_SubtractSuccess() {
        String operation = "subtract";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(10000F)
                .state("activa")
                .userId(1L)
                .build();

        when(walletRepository.findByUserId(walletRequestDTO.getUser_id())).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletBalanceManager(walletRequestDTO, operation);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));

        assertEquals("Saldo restado con éxito.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);
    }

    @Test
    void walletActivating_insufficientFunds() {
        String operation = "subtract";
        WalletRequestDTO walletRequestDTO = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(0F)
                .state("activa")
                .userId(1L)
                .build();

        when(walletRepository.findByUserId(walletRequestDTO.getUser_id())).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletBalanceManager(walletRequestDTO, operation);
        verify(walletRepository, never()).save(any());

        assertEquals("Saldo insuficiente en la billetera.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);

    }

    @Test
    void walletDisabling_NotFound() {
        long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
        WalletResponseDTO rsp = walletService.walletDisabling(walletId);

        assertEquals("La billetera no existe.", rsp.getMessage());
        assertEquals(rsp.getCode(), 0);
    }

    @Test
    void walletDisabling_WalletInactive() {

        long walletId = 1L;
        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(0F)
                .state("inactiva")
                .userId(1L)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletDisabling(walletId);

        assertEquals("La billetera ya se encuentra inactiva.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);

    }

    @Test
    void walletDisabling_Success() {
        long walletId = 1L;
        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(0F)
                .state("activa")
                .userId(1L)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletDisabling(walletId);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));

        assertEquals("La billetera a sido desactivada.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);

    }

    @Test
    void walletActivating_NotFound(){
        long walletId = 1L;

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
        WalletResponseDTO rsp = walletService.walletDisabling(walletId);

        assertEquals("La billetera no existe.", rsp.getMessage());
        assertEquals(rsp.getCode(), 0);

    }

    @Test
    void walletActivating_WalletActive(){
        long walletId = 1L;
        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(0F)
                .state("activa")
                .userId(1L)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletActivating(walletId);

        assertEquals("La billetera ya se encuentra activa.", rsp.getMessage());
        assertEquals(rsp.getCode(), 1);

    }

    @Test
    void walletActivating_Success(){
        long walletId = 1L;
        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(0F)
                .state("inactiva")
                .userId(1L)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(walletEntity));
        WalletResponseDTO rsp = walletService.walletActivating(walletId);
        verify(walletRepository, times(1)).save(any(WalletEntity.class));

        assertEquals("La billetera se a activado.", rsp.getMessage());
        assertEquals(rsp.getCode(), 2);

    }
}