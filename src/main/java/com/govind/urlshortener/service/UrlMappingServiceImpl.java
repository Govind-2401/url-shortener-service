package com.govind.urlshortener.service;

import com.govind.urlshortener.entity.UrlMapping;
import com.govind.urlshortener.exception.UrlNotFoundException;
import com.govind.urlshortener.repository.UrlMappingRepository;
import com.govind.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlMappingServiceImpl implements UrlMappingService {

    private final UrlMappingRepository repository;

    @Override
    @Transactional
    public UrlMapping createShortUrl(String originalUrl) {

        // Check if URL already exists
        return repository.findByOriginalUrl(originalUrl)
                .orElseGet(() -> {
                    // Step 1: Save without shortCode
                    UrlMapping url = UrlMapping.builder()
                            .originalUrl(originalUrl)
                            .clickCount(0L) // Initializing clickCount explicitly
                            .build();

                    UrlMapping saved = repository.save(url);

                    // Step 2: Generate Base62 using ID
                    String shortCode = Base62Encoder.encode(saved.getId());
                    saved.setShortCode(shortCode);

                    // Step 3: Save again with updated shortCode
                    return repository.save(saved);
                });
    }

    @Override
    @Transactional
    public UrlMapping getByShortCode(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found for code: " + shortCode));

        // Increment click count on every access/redirect
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