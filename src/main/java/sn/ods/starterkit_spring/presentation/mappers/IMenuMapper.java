
package sn.ods.starterkit_spring.presentation.mappers;

import org.mapstruct.Mapper;
import sn.ods.starterkit_spring.domain.model.utilisateur.Menu;
import sn.ods.starterkit_spring.presentation.dto.requests.utilisateur.MenuDTO;


@Mapper
public interface IMenuMapper {
    MenuDTO menuToMenuDTO(Menu menu);

}

