package vn.hoidanit.laptopshop.domain.dto;

import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class ProductCriteriaDTO {
    private Optional<String> page;
    private Optional<List<String>> factory;
    private Optional<List<String>> target;
    private Optional<List<String>> price;
    private Optional<String> sort;

}
