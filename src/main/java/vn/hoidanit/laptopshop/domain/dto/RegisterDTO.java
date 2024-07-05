package vn.hoidanit.laptopshop.domain.dto;

import lombok.Data;
import vn.hoidanit.laptopshop.service.validator.RegisterChecked;

@RegisterChecked    
@Data
public class RegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    
}
