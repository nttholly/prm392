package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    private String table_name; // Biến table_name sẽ được gán trong onCreate

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

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_element);
        tv_product_name.setAnimation(animation);

        db = new ShoppingDatabase(this);

        // === BẮT ĐẦU SỬA LỖI ===

        // Bước 1: Lấy Intent và tên danh mục từ KEY đã chuẩn hóa
        Intent intent = getIntent();
        String groupName = intent.getStringExtra(MainActivity.CATEGORY_KEY);
        if (groupName == null) {
            groupName = ""; // Tránh lỗi NullPointerException
        }

        tv_product_name.setText(groupName);

        // Bước 2: Dùng switch-case để map tên (từ layout) với tên Bảng (Table)
        // (Tôi đã xem file XML của bạn để lấy đúng tên)
        switch (groupName) {
            case "Shirt":
                table_name = ShoppingDatabase.TB_SHIRT;
                break;
            case "Pants":
                // (layout của bạn đang map "electronics_card" với text "Pants")
                table_name = ShoppingDatabase.TB_PANTS;
                break;
            case "Shorts":
                // (layout của bạn đang map "mobiles_card" với text "Shorts")
                table_name = ShoppingDatabase.TB_SHORTS;
                break;
            case "Jacket":
                // (layout của bạn đang map "laptop_card" với text "Jacket")
                table_name = ShoppingDatabase.TB_JACKET;
                break;
            case "Shoes":
                // (layout của bạn đang map "games_card" với text "Shoes")
                table_name = ShoppingDatabase.TB_SHOES;
                break;
            case "Belt":
                // (layout của bạn đang map "sports_card" với text "Belt")
                table_name = ShoppingDatabase.TB_BELT;
                break;
            case "Bag":
                // (layout của bạn đang map "book_card" với text "Bag")
                table_name = ShoppingDatabase.TB_BAG;
                break;
            case "Hat":
                // (layout của bạn đang map "home_cooker_card" với text "Hat")
                table_name = ShoppingDatabase.TB_HAT;
                break;
            case "Dress":
                // (layout của bạn đang map "beauty_card" với text "Dress")
                table_name = ShoppingDatabase.TB_DRESS;
                break;
            case "Accessory":
                // (layout của bạn đang map "car_card" với text "Accessory")
                table_name = ShoppingDatabase.TB_ACCESSORY;
                break;
            default:
                // Đặt một giá trị mặc định an toàn và thông báo
                table_name = ShoppingDatabase.TB_SHIRT;
                Toast.makeText(this, "Không tìm thấy bảng cho: " + groupName, Toast.LENGTH_LONG).show();
                break;
        }

        // === KẾT THÚC SỬA LỖI ===

        // Khởi tạo Adapter (với danh sách rỗng ban đầu)
        adapter = new ProductAdapter(new ArrayList<>(), productId -> {
            Intent i = new Intent(getBaseContext(),DisplayProductsActivity.class);
            i.putExtra(PRODUCT_ID_KEY,productId);
            i.putExtra(TABLE_NAME_KEY,table_name); // Dùng biến table_name đã được gán
            HomeActivity.flag = false;
            startActivity(i);
        });

        RecyclerView.LayoutManager lm = new GridLayoutManager(this,2);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        // Gọi hàm loadProducts()
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