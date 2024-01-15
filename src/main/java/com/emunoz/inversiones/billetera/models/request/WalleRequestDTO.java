package com.emunoz.inversiones.billetera.models.request;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class WalleRequestDTO {

    private Long id;

    @NotBlank(message = "El Monto no puede estar en blanco o en cero (0).")
    @Size(max = 30)
    private Float usd_balance;

    private LocalDateTime date_update;

    private Long user_id;

}
