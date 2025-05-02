package sn.wagaane.task_app.presentation.dto.responses.utilisateur;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.wagaane.task_app.presentation.dto.requests.utilisateur.UserReqForUserDTO;


@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResForUserDTO extends UserReqForUserDTO {

    private Long id;
}
