package uz.greenstar.jolybell.exception.product;

import lombok.Getter;

@Getter
public class ProductAlreadySoldException extends RuntimeException {
    private final String message = "This product has already been sold";
}
