package com.example.shoppingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayProductsActivity extends AppCompatActivity {

    RatingBar rb;
    ImageView product_img;
    TextView tv_rating, product_name, Product_price, Product_discount, Product_brand, Product_pieces, Product_description;
    Spinner product_quantity;
    Button add_to_cart;
    double priceAfter;

    ShoppingDatabase db;
    Products p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);

        rb = findViewById(R.id.ratingBar);
        product_img = findViewById(R.id.display_iv_product);
        product_name = findViewById(R.id.display_tv_name);
        Product_price = findViewById(R.id.display_tv_price);
        Product_discount = findViewById(R.id.display_tv_discount);
        Product_brand = findViewById(R.id.display_tv_brand);
        Product_pieces = findViewById(R.id.display_tv_pieces);
        Product_description = findViewById(R.id.display_tv_description);
        product_quantity = findViewById(R.id.display_get_quantity);
        add_to_cart = findViewById(R.id.display_btn_cart);
        tv_rating = findViewById(R.id.display_rating_number);

        rb.setOnRatingBarChangeListener((ratingBar, v, fromUser) -> tv_rating.setText(String.valueOf(v)));

        Intent intent = getIntent();
        int product_id;
        String table_name;

        if (HomeActivity.flag) {
            product_id = intent.getIntExtra(HomeActivity.PRODUCT_KEY, -1);
            table_name = intent.getStringExtra(HomeActivity.TABLE_NAME_KEY);
        } else {
            product_id = intent.getIntExtra(ProductsCardActivity.PRODUCT_ID_KEY, -1);
            table_name = intent.getStringExtra(ProductsCardActivity.TABLE_NAME_KEY);
        }

        db = new ShoppingDatabase(this);
        p = db.getProduct(product_id, table_name);

        if (p != null) {
            if (p.getImage() != null && !p.getImage().isEmpty()) {
                int resId = getResources().getIdentifier(p.getImage().trim().toLowerCase(), "drawable", getPackageName());
                if (resId != 0) product_img.setImageResource(resId);
                else product_img.setImageResource(R.drawable.products);
            } else {
                product_img.setImageResource(R.drawable.products);
            }

            product_name.setText(p.getName());

            if (p.getDiscount() > 0) {
                priceAfter = p.getPrice() - (p.getPrice() * (p.getDiscount() / 100.0));
                Product_discount.setText(priceAfter + "$");
                Product_price.setText(p.getPrice() + "$");
                Product_price.setPaintFlags(Product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Product_price.setTextColor(Color.parseColor("#BFBFBF"));
            } else {
                priceAfter = p.getPrice();
                Product_discount.setText("");
                Product_price.setText(priceAfter + "$");
                Product_price.setTextColor(Color.parseColor("#000000"));
            }

            Product_brand.setText(p.getBrand());
            Product_pieces.setText(String.valueOf(p.getPieces()));
            Product_description.setText(p.getDescription());
        }

        add_to_cart.setOnClickListener(view -> {
            if (p == null) {
                Toast.makeText(this, "Không thể thêm sản phẩm.", Toast.LENGTH_SHORT).show();
                return;
            }

            String image = p.getImage();
            String name = p.getName();
            String brand = p.getBrand();
            int quantity = Integer.parseInt(product_quantity.getSelectedItem().toString());
            float rating = Float.parseFloat(tv_rating.getText().toString());

            Products productToAdd = new Products(image, name, priceAfter, brand, rating, quantity);

            AlertDialog alertDialog = new AlertDialog.Builder(DisplayProductsActivity.this)
                    .setTitle(name)
                    .setMessage("Click (Ok) để thêm sản phẩm vào giỏ hàng")
                    .setNeutralButton("Ok", (dialogInterface, i) -> {
                        db.insertProductInPurchases(productToAdd);
                        Toast.makeText(DisplayProductsActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .create();

            alertDialog.show();
        });
    }
}
