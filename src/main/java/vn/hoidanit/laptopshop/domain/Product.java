package vn.hoidanit.laptopshop.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 2, message = "Name of the product must at least 2 characters")
    private String name;

    @DecimalMin(value = "10", message = "Price must be at least 10")
    private double price;

    private String image;

    @NotBlank(message = "Detail description can't be empty")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String detailDesc;

    @NotBlank(message = "Short description can't be empty")
    private String shortDesc;

    @DecimalMin(value = "1.0", message = "Quantity must be at least 1")
    private long quantity;
    private long sold;
    private String factory;
    private String target;

}
