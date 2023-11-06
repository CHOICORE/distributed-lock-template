package me.choicore.example.domain.product;

import me.choicore.common.distributedlock.DistributedLock;
import me.choicore.common.distributedlock.DistributedLockTemplate;
import me.choicore.example.domain.exception.ProductNotFoundException;
import me.choicore.example.domain.order.OrderProductOperations;
import me.choicore.example.domain.order.command.OrderProduct;
import me.choicore.example.domain.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService implements OrderProductOperations {

    private final static String LOCK_NAME_PREFIX = "PRODUCT";

    private final ProductRepository productRepository;

    private final DistributedLockTemplate distributedLockTemplate;

    public ProductService(ProductRepository productRepository, final DistributedLockTemplate distributedLockTemplate) {
        this.productRepository = productRepository;
        this.distributedLockTemplate = distributedLockTemplate;
    }

    @Transactional(readOnly = true)
    public Product findProductById(final Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }

    @Transactional(readOnly = true)
    public List<Product> findProductByIds(final Map<Long, Integer> productConsumeQuantities) {
        return productRepository.findByIdIn(productConsumeQuantities.keySet());
    }

    @Override
    @DistributedLock(prefix = LOCK_NAME_PREFIX, key = "#productId")
    public void consumeStock(final Long productId, final int quantity) {
        Product product = findProductById(productId);
        product.decreaseStock(quantity);
    }

    @Override
    public List<Product> consumeStock(final List<OrderProduct> orderProducts) {
        return executeStockOperation(orderProducts, this::decreaseToStock);
    }

    @Override
    public List<Product> restock(final List<OrderProduct> orderProducts) {
        return executeStockOperation(orderProducts, this::increaseToStock);
    }

    private List<Product> executeStockOperation(List<OrderProduct> orderProducts, StockOperations stockOperations) {
        Map<Long, Integer> productConsumeQuantities = calculateProductConsumeQuantities(orderProducts);

        return distributedLockTemplate.execute(LOCK_NAME_PREFIX, productConsumeQuantities.keySet(), () -> {
            List<Product> products = findProductByIds(productConsumeQuantities);
            validateProductExistence(products, productConsumeQuantities);
            stockOperations.apply(products, productConsumeQuantities);
            return products;
        });
    }

    private Map<Long, Integer> calculateProductConsumeQuantities(final List<OrderProduct> orderProducts) {
        return orderProducts
                .stream()
                .collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity, Integer::sum));
    }

    private void decreaseToStock(final List<Product> products, final Map<Long, Integer> productConsumeQuantities) {
        products.forEach(product -> {
            int quantityToDecrease = productConsumeQuantities.get(product.getId());
            product.decreaseStock(quantityToDecrease);
        });
    }

    private void increaseToStock(final List<Product> products, final Map<Long, Integer> productConsumeQuantities) {
        products.forEach(product -> {
            int quantityToDecrease = productConsumeQuantities.get(product.getId());
            product.increaseStock(quantityToDecrease);
        });
    }

    private void validateProductExistence(final List<Product> products, final Map<Long, Integer> productConsumeQuantities) {
        Set<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toSet());

        Set<Long> missingProductIds = productConsumeQuantities
                .keySet()
                .stream()
                .filter(productId -> !productIds.contains(productId))
                .collect(Collectors.toSet());

        if (!missingProductIds.isEmpty()) {
            throw new ProductNotFoundException("Product not found: " + missingProductIds);
        }
    }

    private interface StockOperations {
        void apply(List<Product> products, Map<Long, Integer> quantities);
    }
}
