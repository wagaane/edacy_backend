package sn.wagaane.task_app.domain.model.task_app;


import jakarta.persistence.*;
import lombok.*;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "TD_TASK")
@SequenceGenerator(name = "seq_task", initialValue = 100, allocationSize = 2, sequenceName = "seq_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_task")
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;
    @Column(name = "task_title", nullable = false, length = 50)
    private String title;
    @Column(name = "task_description", nullable = false, length = 500)
    private String description;
    @Column(name = "task_deleted", nullable = false)
    private boolean deleted = false;
    @ManyToOne(fetch = FetchType.LAZY)
    private Utilisateur utilisateur;
}
