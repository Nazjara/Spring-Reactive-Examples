package com.nazjara.domain;

import com.nazjara.dto.BeerStyleEnum;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beer {

    @Id
    private Integer id;

    private Long version;

    private String beerName;
    private BeerStyleEnum beerStyle;
    private String upc;

    private Integer quantityOnHand;
    private BigDecimal price;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
