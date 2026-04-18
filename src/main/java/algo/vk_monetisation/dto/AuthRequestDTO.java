package algo.vk_monetisation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Data
public class AuthRequestDTO {
    String email;

    String password;
}
