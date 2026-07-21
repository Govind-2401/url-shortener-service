package com.govind.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    // First save ke waqt shortCode null hota hai, isliye nullable=false remove kiya
    @Column(unique = true, length = 10)
    private String shortCode;

    @Builder.Default
    @Column(nullable = false)
    private Long clickCount = 0L;

    private LocalDateTime createdAt;

    private LocalDateTime expiryDate;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.clickCount == null) {
            this.clickCount = 0L;
        }
    }
}