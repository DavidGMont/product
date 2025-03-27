package me.davidgarmo.soundseeker.product.web.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.persistence.impl.ProductDaoH2;
import me.davidgarmo.soundseeker.product.service.expection.ProductNotFoundException;
import me.davidgarmo.soundseeker.product.service.impl.ProductService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/api/v1/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductService productService;
    private final Gson gson;

    public ProductServlet() {
        this.productService = new ProductService(new ProductDaoH2());
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Product> products = productService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(products));
            } else {
                Long id = Long.parseLong(pathInfo.substring(1));
                try {
                    Product product = productService.findById(id);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(product));
                } catch (ProductNotFoundException e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Product not found.\"}");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid product ID.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }

        out.flush();
    }
}
