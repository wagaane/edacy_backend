package sn.wagaane.task_app.domain.model.utilisateur;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import sn.wagaane.task_app.domain.model.audit.Auditable;
import sn.wagaane.task_app.domain.model.task_app.Task;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "TD_UTILISATEUR")
@SequenceGenerator(name = "seq_user", initialValue = 100, allocationSize = 2, sequenceName = "seq_user")
public  class Utilisateur extends Auditable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user")
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;
    @Column(name = "user_prenom", length = 50, nullable = false)
    private String prenom;
    @Column(name = "user_nom", nullable = false, length = 50)
    private String nom;
    @Column(name = "user_email", nullable = false, length = 50, unique = true)
    private String email;
    @Column(name = "user_password", nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(name = "Uti_FirstLog", columnDefinition = "boolean default true")
    private Boolean firstLog = true;
    @Column(name = "user_status", columnDefinition = "boolean default true")
    private Boolean status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TR_UTILISATEUR_PROFILE",joinColumns = @JoinColumn(name = "compte__id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
    protected Set<Profile> profiles = new HashSet<>();

    @Column(name = "user_deleted", nullable = false)
    private boolean deleted = false;
    @OneToMany(fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();
}
