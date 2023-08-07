package solux.baco.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordForm {

    @NotEmpty
    private String newPassword;

    @NotEmpty
    private String newPasswordConfirm;

    @NotEmpty
    private String newNickname;

}
