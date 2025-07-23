package com.javamastery.ecommerce.product.controller;

import com.javamastery.ecommerce.shared.dto.BaseResponse;
import com.javamastery.ecommerce.product.dto.ProductDto;
import com.javamastery.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "CRUD operations for products and inventory management")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<BaseResponse<Page<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(BaseResponse.success(products));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products by name or description")
    public ResponseEntity<BaseResponse<Page<ProductDto>>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(BaseResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<BaseResponse<Page<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(BaseResponse.success(products));
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Get products with low stock")
    public ResponseEntity<BaseResponse<List<ProductDto>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        List<ProductDto> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(BaseResponse.success(products));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<BaseResponse<ProductDto>> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(BaseResponse.success(product));
    }
    
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<BaseResponse<ProductDto>> getProductBySku(@PathVariable String sku) {
        ProductDto product = productService.getProductBySku(sku);
        return ResponseEntity.ok(BaseResponse.success(product));
    }
    
    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<BaseResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductDto productDto) {
        
        ProductDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Product created successfully", createdProduct));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<BaseResponse<ProductDto>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(BaseResponse.success("Product updated successfully", updatedProduct));
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock quantity")
    public ResponseEntity<BaseResponse<ProductDto>> updateStock(
            @PathVariable Long id, @RequestParam Integer quantity) {
        
        ProductDto updatedProduct = productService.updateStock(id, quantity);
        return ResponseEntity.ok(BaseResponse.success("Stock updated successfully", updatedProduct));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product (soft delete)")
    public ResponseEntity<BaseResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(BaseResponse.success("Product deleted successfully", null));
    }
}