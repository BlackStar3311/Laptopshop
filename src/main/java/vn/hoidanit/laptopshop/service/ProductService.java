package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product handleSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return this.productRepository.findById(id);
    }

    public void deleteProductById(Long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            // check user có cart chưa? chưa thì tạo mới
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                // tạo mới cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);
                cart = this.cartRepository.save(otherCart);
            }
            // lưu cart detail
            // tìm product by id
            Optional<Product> p = this.productRepository.findById(productId);
            if (p.isPresent()) {
                Product realProduct = p.get();

                // check sp đã được thêm vào giỏ hàng lần nào chưa
                CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
                //
                if (oldDetail == null) {
                    CartDetail cd = new CartDetail();
                    cd.setCart(cart);
                    cd.setProduct(realProduct);
                    cd.setPrice(realProduct.getPrice());
                    cd.setQuantity(1);
                    this.cartDetailRepository.save(cd);

                    // update cart(sum)
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    oldDetail.setQuantity(oldDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oldDetail);
                }

            }
        }
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart cartCurrent = cartDetail.getCart();
            // delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailId);

            // update cart
            if (cartCurrent.getSum() > 1) {
                int s = cartCurrent.getSum() - 1;
                cartCurrent.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(cartCurrent);
            } else {
                // delete cart.getSum =1
                this.cartRepository.delete(cartCurrent);
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName, String receiverAddress,
            String receiverPhone) {
        {
            // step1: get cart by user
            Cart cart = this.cartRepository.findByUser(user);
            if (cart != null) {
                List<CartDetail> cartDetails = cart.getCartDetails();

                if (cartDetails != null) {
                    // create order
                    Order order = new Order();
                    order.setUser(user);
                    order.setReceiverAddress(receiverAddress);
                    order.setReceiverName(receiverName);
                    order.setReceiverPhone(receiverPhone);
                    order.setStatus("PENDING");
                    double sum = 0;
                    for (CartDetail cd : cartDetails) {
                        sum += cd.getPrice()*cd.getQuantity();
                    }
                    order.setTotalPrice(sum);
                    order = this.orderRepository.save(order);

                    // create orderdetail
                    for (CartDetail cd : cartDetails) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setProduct(cd.getProduct());
                        orderDetail.setPrice(cd.getPrice());
                        orderDetail.setQuantity(cd.getQuantity());
                        this.orderDetailRepository.save(orderDetail);
                    }
                    // step 2: delete cart and cartdetail
                    for (CartDetail cd : cartDetails) {
                        this.cartDetailRepository.deleteById(cd.getId());
                    }
                    this.cartRepository.deleteById(cart.getId());

                    // step 3: update session
                    session.setAttribute("sum", 0);
                }
            }
        }
    }
}
