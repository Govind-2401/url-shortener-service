package com.govind.urlshortener.controller;

import com.govind.urlshortener.dto.ShortenRequestDto;
import com.govind.urlshortener.dto.ShortenResponseDto;
import com.govind.urlshortener.dto.UrlAnalyticsDto;
import com.govind.urlshortener.entity.UrlMapping;
import com.govind.urlshortener.service.UrlMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "URL Shortener API", description = "Endpoints for creating, redirecting, and analyzing short URLs")
public class UrlMappingController {

    private final UrlMappingService urlMappingService;

    public UrlMappingController(UrlMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @PostMapping("/api/shorten")
    @Operation(summary = "Create Short URL", description = "Generates a unique Base62 short code for a long URL with optional TTL expiration.")
    public ResponseEntity<ShortenResponseDto> createShortUrl(
            @Valid @RequestBody ShortenRequestDto request,
            HttpServletRequest httpServletRequest) {

        UrlMapping mapping = urlMappingService.createShortUrl(request.getOriginalUrl(), request.getDaysToExpire());

        String domainUrl = httpServletRequest.getRequestURL().toString()
                .replace(httpServletRequest.getRequestURI(), "");

        String fullShortUrl = domainUrl + "/" + mapping.getShortCode();

        ShortenResponseDto response = new ShortenResponseDto(
                mapping.getOriginalUrl(),
                fullShortUrl,
                mapping.getShortCode(),
                mapping.getClickCount(),
                mapping.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect Short URL", description = "Redirects to original URL. Returns 200 OK JSON for API clients/Swagger, or 302 Redirect for browsers.")
    public ResponseEntity<?> redirectToOriginalUrl(
            @PathVariable String shortCode,
            @RequestHeader(value = "referer", defaultValue = "") String referer,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
            @RequestHeader(value = "accept", defaultValue = "") String acceptHeader) {

        // Swagger UI, static resources, aur favicon ko shortCode lookup se bypass karein
        if (shortCode.equals("swagger-ui.html") ||
                shortCode.equals("favicon.ico") ||
                shortCode.startsWith("swagger-ui") ||
                shortCode.startsWith("v3")) {
            return ResponseEntity.notFound().build();
        }

        UrlMapping mapping = urlMappingService.getByShortCode(shortCode);

        // Client Type Inspection
        boolean isSwagger = referer.contains("swagger-ui") || userAgent.contains("Swagger");
        boolean isApiClient = acceptHeader.contains("application/json") || acceptHeader.contains("*/*");

        // Agar Swagger/Postman/API Client hai toh JSON 200 return karo
        if (isSwagger || (isApiClient && !userAgent.contains("Mozilla"))) {
            return ResponseEntity.ok(Map.of(
                    "status", "Redirect Success",
                    "shortCode", mapping.getShortCode(),
                    "targetUrl", mapping.getOriginalUrl(),
                    "totalClicks", mapping.getClickCount()
            ));
        }

        // Real Browser Visit -> 302 Redirect to actual website
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(mapping.getOriginalUrl()))
                .build();
    }

    @GetMapping("/api/analytics/{shortCode}")
    @Operation(summary = "Get URL Analytics", description = "Retrieves click count, creation date, expiry date, and active status for a short code.")
    public ResponseEntity<UrlAnalyticsDto> getAnalytics(
            @PathVariable String shortCode,
            HttpServletRequest httpServletRequest) {

        UrlMapping mapping = urlMappingService.getAnalyticsByShortCode(shortCode);

        String domainUrl = httpServletRequest.getRequestURL().toString()
                .replace(httpServletRequest.getRequestURI(), "");
        String fullShortUrl = domainUrl + "/" + mapping.getShortCode();

        boolean isExpired = mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now());

        UrlAnalyticsDto analytics = UrlAnalyticsDto.builder()
                .shortCode(mapping.getShortCode())
                .originalUrl(mapping.getOriginalUrl())
                .shortUrl(fullShortUrl)
                .clickCount(mapping.getClickCount())
                .createdAt(mapping.getCreatedAt())
                .expiryDate(mapping.getExpiryDate())
                .isExpired(isExpired)
                .build();

        return ResponseEntity.ok(analytics);
    }
}