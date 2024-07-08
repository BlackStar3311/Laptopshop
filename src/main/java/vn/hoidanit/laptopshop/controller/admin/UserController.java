package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class UserController {
    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("11@gmal.com.vn.vn.vn");
        model.addAttribute("test", "test");
        return "hello";
    }

    // list Users
    @GetMapping("/admin/user")
    public String getUserPage(Model model) {
        model.addAttribute("listUser", this.userService.getAllUsers());
        return "admin/user/show";
    }

    // User detail
    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("user", this.userService.getUserByID(id).orElse(null));
        return "admin/user/detail";
    }

    // form create
    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    // handle create user
    @PostMapping("/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") @Valid User newUser,
            BindingResult newUserBindingResult, @RequestParam("imgUserFile") MultipartFile file) {

        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        // validate
        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }
        //
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setAvatar(avatar);
        newUser.setPassword(hashPassword);
        newUser.setRole(this.userService.getRoleByName(newUser.getRole().getName()));
        userService.handleSaveUser(newUser);
        return "redirect:/admin/user";
    }

    // form update user
    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable Long id) {
        User newUser = this.userService.getUserByID(id).orElse(null);
        model.addAttribute("newUser", newUser);
        return "admin/user/update";
    }

    // handle update user
    @PostMapping("/admin/user/update")
    public String postUpdateUser(Model model, @ModelAttribute("newUser") User newUser,
            @RequestParam("imgUserFile") MultipartFile file) {
        User currentUser = this.userService.getUserByID(newUser.getId()).orElse(null);
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        if (avatar.equals("")) {
            avatar = currentUser.getAvatar();
        }
        if (currentUser != null) {
            currentUser.setAddress(newUser.getAddress());
            currentUser.setFullName(newUser.getFullName());
            currentUser.setPhone(newUser.getPhone());
            currentUser.setAvatar(avatar);
            currentUser.setRole(this.userService.getRoleByName(newUser.getRole().getName()));
            // update
            userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    // form delete user
    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable Long id) {
        model.addAttribute("id", id);
        return "admin/user/delete";
    }

    // handle delete user
    @PostMapping("/admin/user/delete/{id}")
    public String postDeleteUser(Model model, @PathVariable Long id) {
        this.userService.deleteAUser(id);
        return "redirect:/admin/user";
    }

}
