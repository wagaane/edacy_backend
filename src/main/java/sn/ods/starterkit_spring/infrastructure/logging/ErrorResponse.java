package sn.ods.starterkit_spring.infrastructure.logging;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @param path Getters
 */

public record ErrorResponse(String path, String message, int statusCode, LocalDateTime localDateTime) {
    // Constructeur

}