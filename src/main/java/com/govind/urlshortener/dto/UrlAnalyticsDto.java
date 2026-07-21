package com.govind.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlAnalyticsDto {
    private String shortCode;
    private String originalUrl;
    private String shortUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private boolean isExpired;
}