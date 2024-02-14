package com.emunoz.inversiones.billetera.services;

import com.emunoz.inversiones.billetera.dolarApi.DollarApiResponse;
import com.emunoz.inversiones.billetera.models.entity.WalletEntity;
import com.emunoz.inversiones.billetera.models.request.WalletRequestDTO;
import com.emunoz.inversiones.billetera.models.response.WalletDataResponseDTO;
import com.emunoz.inversiones.billetera.models.response.WalletResponseDTO;
import com.emunoz.inversiones.billetera.repository.WalletRepository;
import com.emunoz.inversiones.billetera.walletMapper.WalletMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {


    @Autowired
    private WalletRepository walletRepository;


    @Override
    public WalletResponseDTO getAllWallet() {
        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        List<WalletEntity> walletEntities = walletRepository.findAll();
        List<WalletDataResponseDTO> walletDataResponseDTOS = walletEntities.stream()
                .map(WalletMapper::toResponseDTO)
                .collect(Collectors.toList());

        if (walletEntities.isEmpty()) {
            walletResponseDTO.setMessage("No hay billeteras creadas.");
            walletResponseDTO.setCode(1);
            return walletResponseDTO;
        }

        walletResponseDTO.setMessage("Billeteras encontradas.");
        walletResponseDTO.setData(walletDataResponseDTOS);
        walletResponseDTO.setCode(2);

        return walletResponseDTO;
    }

    //-------------------

    @Override
    public WalletResponseDTO getWalletByUser(Long user_id) {

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        WalletEntity existingWallet = walletRepository.findByUserId(user_id).orElse(null);

        if (existingWallet != null) {
            WalletDataResponseDTO walletDataResponseDTO = WalletMapper.toResponseDTO(existingWallet);
            walletResponseDTO.setMessage("Billetera encontrada.");
            walletResponseDTO.setData(walletDataResponseDTO);
            walletResponseDTO.setCode(2);

            return walletResponseDTO;

        }

        WalletRequestDTO walletRequestDTO = new WalletRequestDTO();
        walletRequestDTO.setUsd_balance(0F);
        walletRequestDTO.setUser_id(user_id);
        walletRequestDTO.setState("activa.");

        WalletEntity newWalletEntity = WalletMapper.toEntity(walletRequestDTO);
        walletRepository.save(newWalletEntity);

        WalletDataResponseDTO walletDataResponseDTO = WalletMapper.toResponseDTO(newWalletEntity);
        walletResponseDTO.setMessage("Billetera encontrada.");
        walletResponseDTO.setData(walletDataResponseDTO);
        walletResponseDTO.setCode(2);

        return walletResponseDTO;

    }

    @Override
    public WalletResponseDTO walletBalanceManager(WalletRequestDTO walletRequestDTO, String operation) {

        // Obtener la fecha actual en el formato dd-mm-yyyy
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Construir la URL con la fecha actual
        String apiUrl = "https://mindicador.cl/api/dolar/" + formattedDate;

        // Llamada a la API para obtener el valor del dólar actual
        RestTemplate restTemplate = new RestTemplate();
        DollarApiResponse response = restTemplate.getForObject(apiUrl, DollarApiResponse.class);
        Float usdExchangeRate = response.getSerie().get(0).getValor(); // Valor del dólar en CLP

        // Convertir el saldo en CLP a USD
        Float usdBalance = walletRequestDTO.getUsd_balance() / usdExchangeRate;

        WalletEntity existingWallet = walletRepository.findByUserId(walletRequestDTO.getUser_id()).orElse(null);

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        if (existingWallet == null) {
            walletResponseDTO.setMessage("La billetera no existe.");
            walletResponseDTO.setCode(0);
            return walletResponseDTO;
        }

        if (existingWallet.getState().equals("inactiva")) {
            walletResponseDTO.setMessage("La billetera se encuentra inactiva.");
            walletResponseDTO.setData(existingWallet);
            walletResponseDTO.setCode(1);
            return walletResponseDTO;
        }

        if ("add".equals(operation)) {
            // Si la billetera existe y está activa, actualizar el saldo
            existingWallet.setUsd_balance(existingWallet.getUsd_balance() + usdBalance);
            walletRepository.save(existingWallet);

            WalletDataResponseDTO walletDataResponseDTO = WalletMapper.toResponseDTO(existingWallet);

            walletResponseDTO.setMessage("Se ha agregado saldo a la billetera.");
            walletResponseDTO.setData(walletDataResponseDTO);
            walletResponseDTO.setCode(2);
        } else if ("subtract".equals(operation)) {
            if (existingWallet.getUsd_balance() >= usdBalance) {
                // Si hay saldo suficiente, actualizar el saldo
                existingWallet.setUsd_balance(existingWallet.getUsd_balance() - usdBalance);
                walletRepository.save(existingWallet);

                WalletDataResponseDTO walletDataResponseDTO = WalletMapper.toResponseDTO(existingWallet);

                walletResponseDTO.setMessage("Saldo restado con éxito.");
                walletResponseDTO.setData(walletDataResponseDTO);
                walletResponseDTO.setCode(2);
            } else {
                // Si no hay saldo suficiente, devolver un mensaje de error
                walletResponseDTO.setMessage("Saldo insuficiente en la billetera.");
                walletResponseDTO.setData(existingWallet);
                walletResponseDTO.setCode(1);
            }
        }

        return walletResponseDTO;

    }

    //-------------------
    @Override
    public WalletResponseDTO walletDisabling(Long id) {

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();
        WalletEntity existingWallet = walletRepository.findById(id).orElse(null);


        if (existingWallet == null) {
            walletResponseDTO.setMessage("La billetera no existe.");
            walletResponseDTO.setData(null);
            walletResponseDTO.setCode(0);
            return walletResponseDTO;
        } else if (existingWallet.getState().equals("inactiva")) {
            walletResponseDTO.setMessage("La billetera ya se encuentra inactiva.");
            walletResponseDTO.setData(existingWallet);
            walletResponseDTO.setCode(1);
            return walletResponseDTO;
        }

        existingWallet.setState("inactiva");
        walletRepository.save(existingWallet);

        walletResponseDTO.setMessage("La billetera a sido desactivada.");
        walletResponseDTO.setData(existingWallet);
        walletResponseDTO.setCode(2);

        return walletResponseDTO;
    }

    @Override
    public WalletResponseDTO walletActivating(Long id) {
        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();
        WalletEntity existingWallet = walletRepository.findById(id).orElse(null);


        if (existingWallet == null) {
            walletResponseDTO.setMessage("La billetera no existe.");
            walletResponseDTO.setData(null);
            walletResponseDTO.setCode(0);
            return walletResponseDTO;
        } else if (existingWallet.getState().equals("activa")) {
            walletResponseDTO.setMessage("La billetera ya se encuentra activa.");
            walletResponseDTO.setData(existingWallet);
            walletResponseDTO.setCode(1);
            return walletResponseDTO;
        }

        existingWallet.setState("activa");
        walletRepository.save(existingWallet);

        walletResponseDTO.setMessage("La billetera se a activado.");
        walletResponseDTO.setData(existingWallet);
        walletResponseDTO.setCode(2);

        return walletResponseDTO;
    }

}
