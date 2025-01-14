package sn.ods.starterkit_spring.infrastructure.logging;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level; // Niveau de log (INFO, WARN, ERROR, etc.)
    private String message; // Message de log
    private LocalDateTime timestamp; // Date et heure du log

    // Constructeur par défaut
    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructeur avec paramètres
    public LogEntry(String level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
