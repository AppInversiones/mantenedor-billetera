package com.emunoz.inversiones.billetera.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDataResponseDTO {

    private Long id;
    private Float usd_balance;
    private LocalDateTime date_update;
    private Long user_id;
}
