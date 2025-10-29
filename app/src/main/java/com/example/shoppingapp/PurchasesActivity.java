package com.example.shoppingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView; // <-- Thêm import TextView

import java.util.ArrayList;

// === SỬA LẠI BƯỚC 1: Implement interface ===
public class PurchasesActivity extends AppCompatActivity implements PurchasesAdapter.OnCartChangedListener {

    ListView lv;
    PurchasesAdapter pa;
    ShoppingDatabase db;
    TextView tv_total_price; // <-- THÊM MỚI: Biến cho TextView tổng tiền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);

        lv = findViewById(R.id.lv_purchases);
        tv_total_price = findViewById(R.id.tv_total_price); // <-- THÊM MỚI: Ánh xạ view
        db = new ShoppingDatabase(this);

        // (Code ActionBar của bạn)
        // ActionBar actionBar = getSupportActionBar();
        // if (actionBar != null) {
        //     actionBar.setTitle("Purchases");
        // }

        ArrayList<Products> p = db.getAllProductsInPurchases();

        // === SỬA LẠI BƯỚC 2: Cập nhật hàm tạo Adapter ===
        // Truyền 'this' làm listener
        pa = new PurchasesAdapter(p, this, db, this);

        lv.setAdapter(pa);

        // === THÊM MỚI BƯỚC 3: Cập nhật tổng tiền khi mới mở Activity (Read) ===
        updateTotal();
    }

    /**
     * HÀM MỚI: Cập nhật tổng tiền
     */
    private void updateTotal() {
        double total = db.getCartTotalPrice();
        // Định dạng chuỗi
        String totalText = String.format("Tổng cộng: %.2f$", total);
        tv_total_price.setText(totalText);
    }

    /**
     * HÀM MỚI (Từ Interface): Được gọi mỗi khi Sửa/Xóa (Update/Delete)
     */
    @Override
    public void onCartChanged() {
        updateTotal(); // Gọi hàm cập nhật tổng tiền
    }
}