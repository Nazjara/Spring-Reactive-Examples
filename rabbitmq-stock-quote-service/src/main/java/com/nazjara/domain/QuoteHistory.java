package com.nazjara.domain;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteHistory {

    @Id
    private String id;
    private String ticker;
    private BigDecimal price;
    private Instant instant;
}
