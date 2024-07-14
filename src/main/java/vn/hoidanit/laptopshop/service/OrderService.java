package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Order> findAllOrders() {
        return this.orderRepository.findAll();
    }

    public List<OrderDetail> findAllOrderDetailsById(Long id) {
        return this.orderDetailRepository.findAllByOrderId(id);
    }

    public Optional<Order> findOrderById(Long id) {
        return this.orderRepository.findById(id);
    }

    public void deleteOrderById(Long id) {
        // delete order detail
        Optional<Order> orderOptional = this.findOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) {
                this.orderDetailRepository.deleteById(orderDetail.getId());
            }
        }
        // delete order
        this.orderRepository.deleteById(id);
    }

    public Optional<OrderDetail> findOrderDetailById(long id) {
        return this.orderDetailRepository.findById(id);
    }

    public void handleUpdateOrder(Order order) {
        Optional<Order> orderOptional = this.findOrderById(order.getId());
        if (orderOptional.isPresent()) {
            Order currentOrder = orderOptional.get();
            currentOrder.setStatus(order.getStatus());
            this.orderRepository.save(currentOrder);
        }
    }
}
