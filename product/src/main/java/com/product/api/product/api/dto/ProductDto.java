package com.product.api.product.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private int quantity;
    private String uniqueStoreIdentifier;




}
