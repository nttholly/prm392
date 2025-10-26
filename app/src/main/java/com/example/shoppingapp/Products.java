package com.example.shoppingapp;

public class Products {
    private int id;
    private String image;        // tên drawable hoặc URL (không có phần mở rộng)
    private String name;
    private double price;
    private String brand;
    private int pieces;
    private String description;
    private double discount;
    private float rating;
    private int quantity;

    // Constructor for list (no id)
    public Products(String image, String name, double price, String brand, int pieces, String description, double discount) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.pieces = pieces;
        this.description = description;
        this.discount = discount;
    }

    // Constructor with id (single product)
    public Products(int id, String image, String name, double price, String brand, int pieces, String description, double discount) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.pieces = pieces;
        this.description = description;
        this.discount = discount;
    }

    // Constructor for purchases (image, name, price, brand, rating, qty)
    public Products(String image, String name, double price, String brand, float rating, int quantity) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.rating = rating;
        this.quantity = quantity;
    }

    // minimal constructor by id
    public Products(int id) { this.id = id; }

    // getters
    public int getId() { return id; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getBrand() { return brand; }
    public int getPieces() { return pieces; }
    public String getDescription() { return description; }
    public double getDiscount() { return discount; }
    public float getRating() { return rating; }
    public int getQuantity() { return quantity; }

    // setters
    public void setId(int id) { this.id = id; }
    public void setImage(String image) { this.image = image; }   // important: String
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setPieces(int pieces) { this.pieces = pieces; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setRating(float rating) { this.rating = rating; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
