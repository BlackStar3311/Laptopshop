package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;
import java.util.List;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts () {
        return this.productRepository.findAll();
    }

    public Product handleCreateProduct (Product product) {
        return this.productRepository.save(product);
    }
}
