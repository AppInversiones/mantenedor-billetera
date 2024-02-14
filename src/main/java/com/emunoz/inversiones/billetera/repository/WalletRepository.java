package com.emunoz.inversiones.billetera.repository;

import com.emunoz.inversiones.billetera.models.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Optional<WalletEntity> findByUserId(Long userId);
    Optional<WalletEntity> findById(Long id);

}
