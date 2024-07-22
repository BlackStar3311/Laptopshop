package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    // product page
    @GetMapping("/admin/product")
    public String getProduct(Model model, @RequestParam("page") int page) {
        Pageable pageable = PageRequest.of(page - 1, 2);
        Page<Product> prs = this.productService.getAllProducts(pageable);
        List<Product> listProducts = prs.getContent();
        model.addAttribute("listProducts", listProducts);
        return "admin/product/show";
    }

    // create page
    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    // handle create product
    @PostMapping("/admin/product/create")
    public String createProductPage(Model model, @ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newProductBindingResult, @RequestParam("imgProduct") MultipartFile file) {
        if (newProductBindingResult.hasErrors())
            return "admin/product/create";
        String imgProduct = this.uploadService.handleSaveUploadFile(file, "product");
        newProduct.setImage(imgProduct);
        this.productService.handleSaveProduct(newProduct);
        return "redirect:/admin/product";

    }

    // detail page
    @GetMapping("/admin/product/{id}")
    public String getDetailProductPage(Model model, @PathVariable Long id) {
        model.addAttribute("product", this.productService.getProductById(id).orElse(null));
        return "admin/product/detail";
    }

    // delete product page
    @GetMapping("/admin/product/delete/{id}")
    public String getDeleteProductPage(Model model, @PathVariable Long id) {
        model.addAttribute("product", this.productService.getProductById(id).orElse(null));
        return "admin/product/delete";
    }

    // handle delete product
    @PostMapping("/admin/product/delete/{id}")
    public String handleDeleteProduct(Model model, @PathVariable Long id) {
        this.productService.deleteProductById(id);
        return "redirect:/admin/product";
    }

    // update product page
    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProductPage(Model model, @PathVariable Long id) {
        model.addAttribute("product", this.productService.getProductById(id).orElse(null));
        return "admin/product/update";
    }

    // handle update product
    @PostMapping("/admin/product/update")
    public String handleUpdateProduct(Model model, @Valid @ModelAttribute("product") Product newValueProduct,
            BindingResult newProductBindingResult, @RequestParam("imgProduct") MultipartFile file) {
        Product p = this.productService.getProductById(newValueProduct.getId()).orElse(null);
        String imgProduct = this.uploadService.handleSaveUploadFile(file, "product");
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }
        if (imgProduct.isEmpty()) {
            imgProduct = p.getImage();
        }
        newValueProduct.setImage(imgProduct);
        this.productService.handleSaveProduct(newValueProduct);
        return "redirect:/admin/product";
    }
}
