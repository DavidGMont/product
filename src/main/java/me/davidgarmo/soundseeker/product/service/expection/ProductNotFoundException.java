package me.davidgarmo.soundseeker.product.service.expection;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
