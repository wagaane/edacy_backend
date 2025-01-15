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

    private String serviceName;       // Nom du service où l'erreur s'est produite
    private String methodName;        // Nom de la méthode où l'erreur s'est produite


    @Column(columnDefinition = "TEXT")
    private String errorMessage;      // Message d'erreur

    //private String stackTrace;
    // Stack trace de l'erreur
    private LocalDateTime timestamp;  // Date et heure de l'erreur

    public ErrorLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorLog(String serviceName, String methodName, String errorMessage) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.errorMessage = errorMessage;

        this.timestamp = LocalDateTime.now();
    }
}