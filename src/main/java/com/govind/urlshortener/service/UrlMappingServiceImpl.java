package com.govind.urlshortener.service;

import com.govind.urlshortener.entity.UrlMapping;
import com.govind.urlshortener.exception.UrlExpiredException;
import com.govind.urlshortener.exception.UrlNotFoundException;
import com.govind.urlshortener.repository.UrlMappingRepository;
import com.govind.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService {

    private final UrlMappingRepository repository;

    @Override
    @Transactional
    public UrlMapping createShortUrl(String originalUrl, Integer daysToExpire) {
        return repository.findByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                    // Default 30 days if not provided
                    int expiryDays = (daysToExpire != null && daysToExpire > 0) ? daysToExpire : 30;
                    LocalDateTime expiryDate = LocalDateTime.now().plusDays(expiryDays);

                    UrlMapping url = UrlMapping.builder()
                            .originalUrl(originalUrl)
                            .clickCount(0L)
                            .expiryDate(expiryDate)
                            .build();

                    UrlMapping saved = repository.save(url);

                    String shortCode = Base62Encoder.encode(saved.getId());
                    saved.setShortCode(shortCode);

                    return repository.save(saved);
                });
    }

    // Overloaded method calling primary implementation with null (triggering default 30 days)
    @Override
    @Transactional
    public UrlMapping createShortUrl(String originalUrl) {
        return createShortUrl(originalUrl, null);
    }

    @Override
    @Transactional
    public UrlMapping getByShortCode(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found for code: " + shortCode));

        // Check for URL expiration
        if (mapping.getExpiryDate() != null && mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Short URL with code '" + shortCode + "' has expired");
        }

        // Increment click count only if URL is active
        mapping.setClickCount(mapping.getClickCount() + 1);
        return repository.save(mapping);
    }

    @Override
    @Transactional(readOnly = true)
    public UrlMapping getAnalyticsByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found for code: " + shortCode));
    }
}