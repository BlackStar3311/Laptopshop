package vn.hoidanit.laptopshop.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // regex không đúng
    // @Email(message = "Email is not valid", regexp
    // ="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[azA-Z0-9.-]+$")

    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    // @NotNull
    // @Size(min = 2, message = "Password must be at least 2 characters")
    private String password;

    @NotNull
    @Size(min = 2, message = "Fullname must be at least 2 characters")
    @Column(columnDefinition = "NVARCHAR(50)")  
    private String fullName;

    private String address;
    private String phone;
    private String avatar;

    // users many-> to one -> role
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    List<Order> orders;
}
