package vn.hoidanit.laptopshop.controller.client;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ItemController {
    private final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("product", this.productService.getProductById(id).orElse(null));
        return "client/product/detail";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductToCart(@PathVariable long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long productId = id;
        String email = (String) session.getAttribute("email");
        this.productService.handleAddProductToCart(email, productId, session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model, HttpServletRequest request) {
        // nếu có session hiện tại của người dùng thì lấy ra, nếu không có thì thôi, nếu
        // set là true thì không có sẽ tạo mới session
        HttpSession session = request.getSession(false);
        User user = new User();
        long id = (long) session.getAttribute("id");
        user.setId(id);
        Cart cart = this.productService.findCartByUser(user);

        List<CartDetail> cartDetails = cart.getCartDetails();

        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice();
        }
        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        return "client/cart/show";
    }

}
