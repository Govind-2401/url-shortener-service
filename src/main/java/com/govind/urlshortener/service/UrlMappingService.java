package com.govind.urlshortener.service;

import com.govind.urlshortener.entity.UrlMapping;

public interface UrlMappingService {

    UrlMapping createShortUrl(String originalUrl);

    UrlMapping getByShortCode(String shortCode);

    UrlMapping getAnalyticsByShortCode(String shortCode);
}