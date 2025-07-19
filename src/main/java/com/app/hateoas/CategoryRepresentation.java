package com.app.hateoas;

import com.app.entities.Category;
import org.springframework.hateoas.RepresentationModel;

public class CategoryRepresentation extends RepresentationModel<CategoryRepresentation> {

    private Long id;
    private String name;
    private String icon;

    public CategoryRepresentation() {}

    public CategoryRepresentation(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.icon = category.getIcon();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
