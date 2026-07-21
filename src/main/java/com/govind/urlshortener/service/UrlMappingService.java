package com.govind.urlshortener.service;

import com.govind.urlshortener.entity.UrlMapping;

public interface UrlMappingService {

    UrlMapping createShortUrl(String originalUrl, Integer daysToExpire);

    UrlMapping createShortUrl(String originalUrl); // Overloaded convenience method

    UrlMapping getByShortCode(String shortCode);

    UrlMapping getAnalyticsByShortCode(String shortCode);
}