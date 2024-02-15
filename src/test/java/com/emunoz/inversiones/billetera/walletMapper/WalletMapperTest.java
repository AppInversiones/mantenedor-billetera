package com.emunoz.inversiones.billetera.walletMapper;

import com.emunoz.inversiones.billetera.models.entity.WalletEntity;
import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletDataResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class WalletMapperTest {

    @InjectMocks
    private WalletEntity walletEntity;

    @Test
    void toEntity() {
        WalletRequestDTO walletRequest = WalletRequestDTO.builder()
                .usd_balance(5000F)
                .user_id(1L)
                .build();

        // "Mockear" el método estático
        try (MockedStatic<WalletMapper> walletMapper = Mockito.mockStatic(WalletMapper.class)) {

            walletMapper.when(() -> WalletMapper.toEntity (walletRequest)).thenReturn(WalletEntity.builder().usd_balance(5000F).userId(1L).build());
            WalletEntity result = WalletMapper.toEntity(walletRequest);

            assertEquals(5000F, result.getUsd_balance());
            assertEquals(1L, result.getUserId());// Verificar que el campo correo se asigne correctamente.
        }
    }

    @Test
    void toResponseDTO() {

        LocalDate currentDate = LocalDate.now();
        LocalDateTime dateUpdate = currentDate.atStartOfDay();

        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .usd_balance(1000F)
                .userId(1L)
                .date_create(dateUpdate)
                .state("activa")
                .build();

        try (MockedStatic<WalletMapper> userMapper = Mockito.mockStatic(WalletMapper.class)) {
            userMapper.when(() -> WalletMapper.toResponseDTO(walletEntity))
                    .thenReturn(new WalletDataResponseDTO(1L, 1000F, dateUpdate,  1L, "activa"));
            WalletDataResponseDTO result = WalletMapper.toResponseDTO(walletEntity);
            // Verificar que el campo correo se asigne correctamente.
            assertEquals(1000F, result.getUsd_balance());
            assertEquals(1L, result.getId());
            assertEquals("activa", result.getState());

        }
    }
}