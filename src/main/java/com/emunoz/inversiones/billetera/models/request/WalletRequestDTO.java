package com.emunoz.inversiones.billetera.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequestDTO {

    private Long id;

    @DecimalMin(value = "0.01", message = "El Monto no puede estar en blanco o en cero (0).")
    @NotNull
    private Float usd_balance;

    private LocalDateTime date_update;

    private Long user_id;

}
