
package sn.ods.starterkit_spring.domain.model.utilisateur;

import jakarta.persistence.*;
import lombok.*;


import java.util.Objects;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "TP_Menu")
@SequenceGenerator(name = "seq_menu", initialValue = 100, allocationSize = 1, sequenceName = "seq_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_menu")
    @Column(name = "menu_id", nullable = false, updatable = false, unique = true)
    private Long menId;

    @Column(name = "men_path")
    private String menPath;

    @Column(name = "men_ytitle")
    private String menTitle;

    @Column(name = "men_type")
    private String menType;

    @Column(name = "men_iconType", columnDefinition = "TEXT")
    private String menIconType;






    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Menu menu = (Menu) o;
        return getMenId().equals(menu.getMenId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getMenId());
    }

}
