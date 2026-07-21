package com.govind.urlshortener.controller;

import com.govind.urlshortener.dto.ShortenRequestDto;
import com.govind.urlshortener.dto.ShortenResponseDto;
import com.govind.urlshortener.entity.UrlMapping;
import com.govind.urlshortener.service.UrlMappingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlMappingController {

    private final UrlMappingService urlMappingService;

    public UrlMappingController(UrlMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenResponseDto> createShortUrl(
            @Valid @RequestBody ShortenRequestDto request,
            HttpServletRequest httpServletRequest) {

        UrlMapping mapping = urlMappingService.createShortUrl(request.getOriginalUrl());

        // Base URL dynamic extract karne ke liye (e.g., http://localhost:8080/)
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
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        UrlMapping mapping = urlMappingService.getByShortCode(shortCode);

        // HTTP 302 Found redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(mapping.getOriginalUrl()))
                .build();
    }
}