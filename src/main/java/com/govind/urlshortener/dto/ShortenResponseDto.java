package com.govind.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShortenResponseDto {
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime createdAt;
}