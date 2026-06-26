package com.project.settlement_batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String storeName;
    private Integer amount;
    private LocalDate orderDate;

    public static Orders create(Long id, String customerName, String storeName, Integer amount, LocalDate orderDate) {
        return new Orders(id, customerName, storeName, amount, orderDate);
    }
}