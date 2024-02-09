package com.emunoz.inversiones.billetera.walletMapper;


import com.emunoz.inversiones.billetera.models.entity.WalletEntity;
import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletDataResponseDTO;

public class WalletMapper {

    public static WalletEntity toEntity(WalletRequestDTO walletRequestDTO) {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setUsd_balance(walletRequestDTO.getUsd_balance());
        walletEntity.setUserId(walletRequestDTO.getUser_id());

        return walletEntity;
    }

    public static WalletDataResponseDTO toResponseDTO(WalletEntity walletEntity) {
        WalletDataResponseDTO walletdataResponseDTO = new WalletDataResponseDTO();
        walletdataResponseDTO.setId(walletEntity.getId());
        walletdataResponseDTO.setUsd_balance(walletEntity.getUsd_balance());
        walletdataResponseDTO.setUser_id(walletEntity.getUserId());
        walletdataResponseDTO.setDate_update(walletEntity.getDate_create());

        return walletdataResponseDTO;
    }
}
