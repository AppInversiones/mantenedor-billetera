package com.emunoz.inversiones.billetera.models.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usd_balance")
    private Float usd_balance;

    @Column(name = "date_update")
    private LocalDateTime date_create;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "state")
    private String state;

    @PrePersist
    protected void onCreate() {
        date_create = LocalDateTime.now();
    }


}
