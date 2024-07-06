package vn.hoidanit.laptopshop.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.hoidanit.laptopshop.service.validator.RegisterChecked;

@RegisterChecked
@Data
public class RegisterDTO {
    @Size(min = 3, message = "First must be at least 3 characters")
    private String firstName;

    private String lastName;

    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Size(min = 3, message = "First must be at least 3 characters")
    private String password;
    private String confirmPassword;

}
