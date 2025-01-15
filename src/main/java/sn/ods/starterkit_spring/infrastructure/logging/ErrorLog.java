package sn.ods.starterkit_spring.infrastructure.logging;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;          // Chemin de l'API (ex: "/api-v1/account/logout")
    private String message;       // Message d'erreur (ex: "Required request header 'Authorization'...")
    private int statusCode;       // Code de statut HTTP (ex: 500)
    private LocalDateTime localDateTime; // Date et heure de l'erreur

    // Constructeurs
    public ErrorLog() {
        this.localDateTime = LocalDateTime.now();
    }

    public ErrorLog(String path, String message, int statusCode, LocalDateTime localDateTime) {
        this.path = path;
        this.message = message;
        this.statusCode = statusCode;
        this.localDateTime = localDateTime;
    }
}