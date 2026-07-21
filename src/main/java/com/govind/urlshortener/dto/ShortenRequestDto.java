package com.govind.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

@Data
public class ShortenRequestDto {
    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Please provide a valid URL format")
    private String originalUrl;
}