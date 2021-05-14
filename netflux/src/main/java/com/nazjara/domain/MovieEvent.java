package com.nazjara.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MovieEvent {
    private String movieId;
    private Date movieDate;
}
