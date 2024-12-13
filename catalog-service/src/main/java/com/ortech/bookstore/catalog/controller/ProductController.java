package com.ortech.bookstore.catalog.controller;

import com.ortech.bookstore.catalog.domain.PagedResult;
import com.ortech.bookstore.catalog.domain.Product;
import com.ortech.bookstore.catalog.exception.ProductNotFoundException;
import com.ortech.bookstore.catalog.service.ProductService;
import com.ortech.bookstore.catalog.service.impl.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;
    private final GeminiChatService geminiChatService;

    @GetMapping("/all")
    ResponseEntity<PagedResult<Product>> getProductsList(
            @RequestParam(name = "pageNumber", required = false) int pageNumber) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber));
    }

    @GetMapping
    ResponseEntity<Product> getProductByCode(@RequestParam(name = "code") String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }
    @GetMapping("/gemini-query")
    ResponseEntity<String> getGemini(String query) throws IOException {
        return ResponseEntity.ok(geminiChatService.callGemini(query));
    }
}
