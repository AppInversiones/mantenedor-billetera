package com.emunoz.inversiones.billetera.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponseDTO {

    private String message;
    private Object data;
    private Integer code;

}
