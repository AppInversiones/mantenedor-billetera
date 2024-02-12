package com.emunoz.inversiones.billetera.services;

import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;

public interface WalletService {

    WalletResponseDTO getWalletByUser(Long user_id);

    WalletResponseDTO withdrawBalanceWallet(WalletRequestDTO walletRequestDTO);

    WalletResponseDTO getAllWallet();

    WalletResponseDTO  walletBalanceManager(WalletRequestDTO walletRequestDTO, String operation);

}
