
package sn.wagaane.task_app.presentation.mappers;

import org.mapstruct.Mapper;
import sn.wagaane.task_app.domain.model.utilisateur.Menu;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.MenuDTO;


@Mapper
public interface IMenuMapper {
    MenuDTO menuToMenuDTO(Menu menu);

}

