package com.example.shoppingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PurchasesActivity extends AppCompatActivity implements PurchasesAdapter.OnCartChangedListener {

    ListView lv;
    PurchasesAdapter pa;
    ShoppingDatabase db;
    TextView tv_total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);

        lv = findViewById(R.id.lv_purchases);
        tv_total_price = findViewById(R.id.tv_total_price); // Ánh xạ view trước khi dùng
        db = new ShoppingDatabase(this);

        // Nếu muốn đặt tiêu đề:
        // ActionBar actionBar = getSupportActionBar();
        // if (actionBar != null) actionBar.setTitle("Purchases");

        ArrayList<Products> p = db.getAllProductsInPurchases();

        // Truyền listener vào adapter
        pa = new PurchasesAdapter(p, this, db, this);
        lv.setAdapter(pa);

        // Cập nhật tổng tiền ban đầu
        updateTotal();

        // Biến TextView tổng tiền thành “nút” thanh toán
        tv_total_price.setOnClickListener(v -> showCheckoutDialog());
    }

    // Cập nhật tổng tiền
    private void updateTotal() {
        double total = db.getCartTotalPrice();
        // Hiển thị theo định dạng
        String totalText = String.format("Thanh toán: %.2f$", total);
        tv_total_price.setText(totalText);

        // Tùy chọn: vô hiệu hóa nếu tổng = 0 để tránh bấm nhầm
        tv_total_price.setEnabled(total > 0);
        tv_total_price.setAlpha(total > 0 ? 1f : 0.5f);
    }

    // Dialog xác nhận thanh toán
    private void showCheckoutDialog() {
        double total = db.getCartTotalPrice();
        String totalText = String.format("Thanh toán: %.2f$", total);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có muốn thanh toán hóa đơn với số tiền: " + totalText + "?")
                .setPositiveButton("Yes", (d, w) -> performCheckout())
                .setNegativeButton("No", (d, w) -> d.dismiss())
                .show();
    }

    // Xóa toàn bộ giỏ và làm mới UI
    private void performCheckout() {
        // 1) Xóa tất cả mặt hàng trong purchases
        SQLiteDatabase writable = db.getWritableDatabase();
        writable.delete(ShoppingDatabase.TB_PURCHASES, null, null);

        // 2) Làm mới danh sách adapter
        ArrayList<Products> refreshed = db.getAllProductsInPurchases();
        pa.purchases.clear();
        pa.purchases.addAll(refreshed);
        pa.notifyDataSetChanged();

        // 3) Cập nhật tổng tiền về 0
        updateTotal();

        android.widget.Toast.makeText(this, "Thanh toán thành công!", android.widget.Toast.LENGTH_SHORT).show();
    }

    // Adapter callback khi có sửa/xóa số lượng
    @Override
    public void onCartChanged() {
        updateTotal();
    }
}
