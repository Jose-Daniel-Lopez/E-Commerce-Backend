package com.app.DTO;

import com.app.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewlyAddedProductDTO {

    private Long id;
    private String imageUrl;
    private String name;
    private Category category;
    private BigDecimal basePrice;
}
