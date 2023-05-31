package com.product.api.product.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDto {
    private StoreDto store;
    private List<ProductDto> product;
}
