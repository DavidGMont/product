package me.davidgarmo.soundseeker.product.web.servlet;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import me.davidgarmo.soundseeker.product.persistence.impl.ProductDaoH2;
import me.davidgarmo.soundseeker.product.service.impl.ProductService;

@WebServlet(urlPatterns = "/api/v1/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductService productService;
    private final Gson gson;

    public ProductServlet() {
        this.productService = new ProductService(new ProductDaoH2());
        this.gson = new Gson();
    }
}
