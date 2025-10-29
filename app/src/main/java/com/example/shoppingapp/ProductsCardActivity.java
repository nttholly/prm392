package com.example.shoppingapp;

import androidx.annotation.NonNull; // <-- Thêm import
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem; // <-- Thêm import
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductsCardActivity extends AppCompatActivity {

    RecyclerView rv;
    private ProductAdapter adapter;
    public static final String PRODUCT_ID_KEY = "product_key";
    public static final String TABLE_NAME_KEY = "table_name_key";
    TextView tv_product_name;
    ShoppingDatabase db;
    private String table_name;

    // === THÊM MỚI: Biến lưu trạng thái sắp xếp ===
    // (Mặc định là không sắp xếp)
    private String currentSortOrder = null;
    // Biến lưu trạng thái tìm kiếm
    private String currentSearchQuery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_card);

        rv = findViewById(R.id.rv_products);
        tv_product_name = findViewById(R.id.tv_product_name);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoom_element);
        tv_product_name.setAnimation(animation);

        db = new ShoppingDatabase(this);

        // (Giữ nguyên logic lấy table_name từ Intent)
        Intent intent = getIntent();
        String groupName = "";
        if (MainActivity.name_data != null && MainActivity.name_data.equals("Fashion")) {
            groupName = intent.getStringExtra(MainActivity.FASHION_KEY);
            tv_product_name.setText(groupName);
            switch (groupName) {
                case "Áo sơ mi, áo thun, áo polo":
                    table_name = ShoppingDatabase.TB_SHIRT;
                    break;
                case "Quần dài, quần kaki, quần tây":
                    table_name = ShoppingDatabase.TB_PANTS;
                    break;
                case "Quần short nam/nữ, thể thao":
                    table_name = ShoppingDatabase.TB_SHORTS;
                    break;
                case "Áo khoác, áo gió, áo len, hoodie":
                    table_name = ShoppingDatabase.TB_JACKET;
                    break;
                case "Giày sneaker, giày da, boots, thể thao":
                    table_name = ShoppingDatabase.TB_SHOES;
                    break;
                case "Thắt lưng, dây nịt":
                    table_name = ShoppingDatabase.TB_BELT;
                    break;
                case "Túi xách, balo, túi da":
                    table_name = ShoppingDatabase.TB_BAG;
                    break;
                case "Mũ lưỡi trai, mũ bucket, nón rộng vành":
                    table_name = ShoppingDatabase.TB_HAT;
                    break;
                case "Váy, đầm nữ":
                    table_name = ShoppingDatabase.TB_DRESS;
                    break;
                case "Kính, đồng hồ, khăn, trang sức":
                    table_name = ShoppingDatabase.TB_ACCESSORY;
                    break;
                default:
                    table_name = ShoppingDatabase.TB_SHIRT;
                    Toast.makeText(this, "Không tìm thấy tên bảng tương ứng với: " + groupName, Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            table_name = ShoppingDatabase.TB_SHIRT;
        }
        MainActivity.name_data = "";


        // Khởi tạo Adapter (với danh sách rỗng ban đầu)
        adapter = new ProductAdapter(new ArrayList<>(), productId -> {
            Intent i = new Intent(getBaseContext(),DisplayProductsActivity.class);
            i.putExtra(PRODUCT_ID_KEY,productId);
            i.putExtra(TABLE_NAME_KEY,table_name);
            HomeActivity.flag = false;
            startActivity(i);
        });

        RecyclerView.LayoutManager lm = new GridLayoutManager(this,2);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        // === SỬA LẠI: Gọi hàm loadProducts() ===
        loadProducts();
    }

    /**
     * HÀM MỚI: Tải sản phẩm dựa trên trạng thái Search và Filter
     */
    private void loadProducts() {
        if (table_name == null) return;

        ArrayList<Products> products;

        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            // 1. Nếu đang tìm kiếm -> Ưu tiên tìm kiếm (với sắp xếp)
            products = db.getProductForSearch(currentSearchQuery, table_name, currentSortOrder);
        } else {
            // 2. Nếu không tìm kiếm -> Lấy tất cả (với sắp xếp)
            products = db.getAllProducts(table_name, currentSortOrder);
        }

        adapter.setProducts(products);
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.main_search).getActionView();
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // === SỬA LẠI: Cập nhật biến và gọi loadProducts() ===
                currentSearchQuery = query;
                loadProducts();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // === SỬA LẠI: Cập nhật biến và gọi loadProducts() ===
                currentSearchQuery = newText;
                loadProducts();
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            // === SỬA LẠI: Cập nhật biến và gọi loadProducts() ===
            currentSearchQuery = null; // Xóa trạng thái tìm kiếm
            loadProducts(); // Tải lại danh sách (vẫn giữ filter nếu có)
            return false;
        });

        return true;
    }

    /**
     * HÀM MỚI: Xử lý sự kiện click cho các item trong menu (Filter)
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_price_low_high) {
            currentSortOrder = "ASC"; // Sắp xếp tăng dần
            loadProducts(); // Tải lại danh sách
            return true;
        } else if (id == R.id.filter_price_high_low) {
            currentSortOrder = "DESC"; // Sắp xếp giảm dần
            loadProducts();
            return true;
        } else if (id == R.id.filter_none) {
            currentSortOrder = null; // Bỏ sắp xếp
            loadProducts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}