package com.app.hateoas;

import com.app.controllers.UserController;
import com.app.controllers.ProductController;
import com.app.entities.*;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * A service component responsible for building HATEOAS-compliant representations
 * of domain entities by adding relevant hypermedia links.
 * <p>
 * This class encapsulates the logic to enrich entities like {@link User}, {@link Product},
 * {@link Category}, and {@link Order} with navigational links based on their relationships
 * and availability of associated data. It follows the HATEOAS principle to make the API
 * self-discoverable.
 * </p>
 */
@Component
public class HateoasLinkBuilder {

    /**
     * Constructs a {@link UserRepresentation} enriched with HATEOAS links.
     *
     * @param user the user entity to enrich; must not be null
     * @return a {@link UserRepresentation} with self and relationship links
     */
    public UserRepresentation buildUserRepresentation(User user) {
        UserRepresentation userRep = new UserRepresentation(user);

        // Self reference to the user resource
        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
        userRep.add(selfLink);

        // Direct link labeled "user" for consistency in client navigation
        userRep.add(selfLink.withRel("user"));

        // Conditional links to related resources only if data exists
        addIfNotEmpty(userRep, user.getAddresses(), "addresses", user.getId(), UserController.class, "addresses");
        addIfNotNull(userRep, user.getCart(), "cart", user.getId(), UserController.class, "cart");
        addIfNotEmpty(userRep, user.getOrders(), "orders", user.getId(), UserController.class, "orders");
        addIfNotEmpty(userRep, user.getProductReviews(), "reviews", user.getId(), UserController.class, "reviews");
        addIfNotEmpty(userRep, user.getWishlists(), "wishlists", user.getId(), UserController.class, "wishlists");

        return userRep;
    }

    /**
     * Constructs a {@link ProductRepresentation} enriched with HATEOAS links.
     *
     * @param product the product entity to enrich; must not be null
     * @return a {@link ProductRepresentation} with self and relationship links
     */
    public ProductRepresentation buildProductRepresentation(Product product) {
        ProductRepresentation productRep = new ProductRepresentation(product);

        // Self reference to the product resource
        Link selfLink = linkTo(methodOn(ProductController.class).getProductById(product.getId())).withSelfRel();
        productRep.add(selfLink);

        // Secondary "product" relation for consistent client-side handling
        productRep.add(selfLink.withRel("product"));

        // Category is required, so we always include the link if category exists
        addIfNotNull(productRep, product.getCategory(), "category", product.getId(), ProductController.class, "category");

        // Optional collections: only link if not empty
        addIfNotEmpty(productRep, product.getProductVariants(), "productVariants", product.getId(), ProductController.class, "productVariants");
        addIfNotEmpty(productRep, product.getProductReviews(), "productReviews", product.getId(), ProductController.class, "productReviews");
        addIfNotEmpty(productRep, product.getWishlists(), "wishlists", product.getId(), ProductController.class, "wishlists");

        return productRep;
    }

    /**
     * Constructs a {@link CategoryRepresentation} enriched with HATEOAS links.
     *
     * @param category the category entity to enrich; must not be null
     * @return a {@link CategoryRepresentation} with self and product listing links
     */
    public CategoryRepresentation buildCategoryRepresentation(Category category) {
        CategoryRepresentation categoryRep = new CategoryRepresentation(category);

        // Self link pointing to products under this category (common access pattern)
        Link selfLink = linkTo(methodOn(ProductController.class).getProductsByCategory(category.getId(), null)).withSelfRel();
        categoryRep.add(selfLink);

        // Secondary "category" relation for semantic clarity
        categoryRep.add(selfLink.withRel("category"));

        // Link to all products in this category, if any exist
        addIfNotEmpty(categoryRep, category.getProducts(), "products", "category", category.getId(), ProductController.class, "products");

        return categoryRep;
    }

    /**
     * Constructs an {@link OrderRepresentation} enriched with HATEOAS links.
     *
     * @param order the order entity to enrich; must not be null
     * @return an {@link OrderRepresentation} with self and relationship links
     */
    public OrderRepresentation buildOrderRepresentation(Order order) {
        OrderRepresentation orderRep = new OrderRepresentation(order);

        String orderId = order.getId().toString();
        String baseOrderUrl = "/api/orders/" + orderId;

        // Self link using direct URL (no controller method mapped yet?)
        Link selfLink = Link.of(baseOrderUrl).withSelfRel();
        orderRep.add(selfLink);
        orderRep.add(selfLink.withRel("order")); // Consistent relation alias

        // Link to the user who placed the order
        if (order.getUser() != null) {
            Link userLink = linkTo(methodOn(UserController.class).getUserById(order.getUser().getId())).withRel("user");
            orderRep.add(userLink);
        }

        // Link to order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            orderRep.add(Link.of(baseOrderUrl + "/items").withRel("orderItems"));
        }

        // Link to payment details
        if (order.getPayment() != null) {
            orderRep.add(Link.of(baseOrderUrl + "/payment").withRel("payment"));
        }

        // Link to discount code applied
        if (order.getDiscountCode() != null) {
            orderRep.add(Link.of(baseOrderUrl + "/discountCode").withRel("discountCode"));
        }

        return orderRep;
    }

    // === Private Helper Methods ===

    /**
     * Adds a link to the representation if the given collection is not null and not empty.
     *
     * @param representation the HATEOAS representation to add the link to
     * @param collection     the collection to check for presence
     * @param rel            the relation name (e.g., "addresses", "orders")
     * @param id             the ID of the parent resource
     * @param controller     the controller class used to build the link
     * @param path           optional path suffix (can be same as rel)
     * @param <T>            the type of the representation
     * @param <C>            the type of the collection
     */
    private <T extends org.springframework.hateoas.RepresentationModel<T>, C> void addIfNotEmpty(
            T representation, java.util.Collection<C> collection, String rel, Object id,
            Class<?> controller, String path) {
        if (collection != null && !collection.isEmpty()) {
            Link link = linkTo(controller).slash(id).slash(path).withRel(rel);
            representation.add(link);
        }
    }

    /**
     * Adds a link to the representation if the given object is not null.
     *
     * @param representation the HATEOAS representation to add the link to
     * @param obj            the object to check for presence
     * @param rel            the relation name
     * @param id             the ID of the parent resource
     * @param controller     the controller class used to build the link
     * @param path           path suffix for the link
     * @param <T>            the type of the representation
     */
    private <T extends org.springframework.hateoas.RepresentationModel<T>> void addIfNotNull(
            T representation, Object obj, String rel, Object id,
            Class<?> controller, String path) {
        if (obj != null) {
            Link link = linkTo(controller).slash(id).slash(path).withRel(rel);
            representation.add(link);
        }
    }

    /**
     * Adds a link with a nested path structure (e.g., /category/{id}/products).
     */
    private <T extends org.springframework.hateoas.RepresentationModel<T>, C> void addIfNotEmpty(
            T representation, java.util.Collection<C> collection, String rel,
            String parentPath, Object id, Class<?> controller, String path) {
        if (collection != null && !collection.isEmpty()) {
            Link link = linkTo(controller)
                    .slash(parentPath)
                    .slash(id)
                    .slash(path)
                    .withRel(rel);
            representation.add(link);
        }
    }
}