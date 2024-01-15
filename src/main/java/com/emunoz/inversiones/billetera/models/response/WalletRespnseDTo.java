package com.emunoz.inversiones.billetera.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletRespnseDTo {

    private String message;
    private Object data;
    private Integer code;

}
