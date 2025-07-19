package com.app.hateoas;

import com.app.controllers.UserController;
import com.app.controllers.ProductController;
import com.app.controllers.CartItemController;
import com.app.entities.*;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class HateoasLinkBuilder {

    public UserRepresentation buildUserRepresentation(User user) {
        UserRepresentation userRep = new UserRepresentation(user);

        // Self link
        userRep.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        userRep.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("user"));

        // Relationship links - only add if they exist and have data
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            userRep.add(linkTo(UserController.class).slash(user.getId()).slash("addresses").withRel("addresses"));
        }

        if (user.getCart() != null) {
            userRep.add(linkTo(UserController.class).slash(user.getId()).slash("cart").withRel("cart"));
        }

        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            userRep.add(linkTo(UserController.class).slash(user.getId()).slash("orders").withRel("orders"));
        }

        if (user.getProductReviews() != null && !user.getProductReviews().isEmpty()) {
            userRep.add(linkTo(UserController.class).slash(user.getId()).slash("reviews").withRel("reviews"));
        }

        if (user.getWishlists() != null && !user.getWishlists().isEmpty()) {
            userRep.add(linkTo(UserController.class).slash(user.getId()).slash("wishlists").withRel("wishlists"));
        }

        return userRep;
    }

    public ProductRepresentation buildProductRepresentation(Product product) {
        ProductRepresentation productRep = new ProductRepresentation(product);

        // Self link
        productRep.add(linkTo(methodOn(ProductController.class).getProductById(product.getId())).withSelfRel());
        productRep.add(linkTo(methodOn(ProductController.class).getProductById(product.getId())).withRel("product"));

        // Category link (always present since it's required)
        if (product.getCategory() != null) {
            productRep.add(linkTo(ProductController.class).slash(product.getId()).slash("category").withRel("category"));
        }

        // Product variants link
        if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
            productRep.add(linkTo(ProductController.class).slash(product.getId()).slash("productVariants").withRel("productVariants"));
        }

        // Product reviews link
        if (product.getProductReviews() != null && !product.getProductReviews().isEmpty()) {
            productRep.add(linkTo(ProductController.class).slash(product.getId()).slash("productReviews").withRel("productReviews"));
        }

        // Wishlists link
        if (product.getWishlists() != null && !product.getWishlists().isEmpty()) {
            productRep.add(linkTo(ProductController.class).slash(product.getId()).slash("wishlists").withRel("wishlists"));
        }

        return productRep;
    }

    public CategoryRepresentation buildCategoryRepresentation(Category category) {
        CategoryRepresentation categoryRep = new CategoryRepresentation(category);

        // Self link (assuming we'll have a CategoryController)
        categoryRep.add(linkTo(methodOn(ProductController.class).getProductsByCategory(category.getId(), null)).withSelfRel());
        categoryRep.add(linkTo(methodOn(ProductController.class).getProductsByCategory(category.getId(), null)).withRel("category"));

        // Products link
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            categoryRep.add(linkTo(ProductController.class).slash("category").slash(category.getId()).slash("products").withRel("products"));
        }

        return categoryRep;
    }

    public OrderRepresentation buildOrderRepresentation(Order order) {
        OrderRepresentation orderRep = new OrderRepresentation(order);

        // Self link (assuming we'll have an OrderController)
        orderRep.add(Link.of("/api/orders/" + order.getId()).withSelfRel());
        orderRep.add(Link.of("/api/orders/" + order.getId()).withRel("order"));

        // User link
        if (order.getUser() != null) {
            orderRep.add(linkTo(methodOn(UserController.class).getUserById(order.getUser().getId())).withRel("user"));
        }

        // Order items link
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            orderRep.add(Link.of("/api/orders/" + order.getId() + "/items").withRel("orderItems"));
        }

        // Payment link
        if (order.getPayment() != null) {
            orderRep.add(Link.of("/api/orders/" + order.getId() + "/payment").withRel("payment"));
        }

        // Discount code link
        if (order.getDiscountCode() != null) {
            orderRep.add(Link.of("/api/orders/" + order.getId() + "/discountCode").withRel("discountCode"));
        }

        return orderRep;
    }
}
