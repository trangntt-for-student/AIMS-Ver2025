package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.Product;

import java.util.List;

public class ProductController {
    private static final int PAGE_SIZE = 20;

    public ProductController() {
        Database.initDatabase();
    }

    public int countProducts() {
        return Database.countProducts();
    }

    public List<Product> getPage(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        return Database.getProducts(offset, PAGE_SIZE);
    }

    public int getPageSize() { return PAGE_SIZE; }
}
