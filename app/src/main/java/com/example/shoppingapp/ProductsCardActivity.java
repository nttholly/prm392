package com.example.shoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_card);

        rv = findViewById(R.id.rv_products);
        tv_product_name = findViewById(R.id.tv_product_name);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoom_element);
        tv_product_name.setAnimation(animation);

        db = new ShoppingDatabase(this);

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

        ArrayList<Products> products = new ArrayList<>();
        if (table_name != null) products = db.getAllProducts(table_name);
        else Toast.makeText(this, "Lỗi: Tên bảng không hợp lệ.", Toast.LENGTH_SHORT).show();

        adapter = new ProductAdapter(products, productId -> {
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
    }

    @SuppressLint("ResourceAsColor")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.main_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (table_name != null) {
                    ArrayList<Products> product = db.getProductForSearch(query,table_name);
                    adapter.setProducts(product);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (table_name != null) {
                    ArrayList<Products> product = db.getProductForSearch(newText,table_name);
                    adapter.setProducts(product);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            if (table_name != null) {
                ArrayList<Products> product = db.getAllProducts(table_name);
                adapter.setProducts(product);
                adapter.notifyDataSetChanged();
            }
            return false;
        });
        return true;
    }
}
