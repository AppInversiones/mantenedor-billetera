package com.emunoz.inversiones.billetera.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequestDTO {

    private Long id;

    @NotNull(message = "El Monto no puede estar en blanco o en cero (0).")
    @Positive(message = "El saldo debe ser mayor que cero")
    private Float usd_balance;

    private LocalDateTime date_update;

    private Long user_id;

}
