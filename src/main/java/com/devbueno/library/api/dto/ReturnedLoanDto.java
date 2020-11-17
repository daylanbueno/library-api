package com.devbueno.library.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnedLoanDto {

    private String isbn;
    private String customer;
    private Boolean returned;

}
