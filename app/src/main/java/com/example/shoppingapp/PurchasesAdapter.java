package com.example.shoppingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PurchasesAdapter extends BaseAdapter {

    ArrayList<Products> purchases;
    Context context;
    ShoppingDatabase db;

    // === THÊM MỚI BƯỚC 1: Tạo Interface ===
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged(); // Hàm sẽ được gọi khi có Sửa hoặc Xóa
    }
    // === KẾT THÚC BƯỚC 1 ===


    // === SỬA LẠI BƯỚC 2: Cập nhật Constructor ===
    public PurchasesAdapter(ArrayList<Products> purchases, Context context, ShoppingDatabase db, OnCartChangedListener listener) {
        this.purchases = purchases;
        this.context = context;
        this.db = db;
        this.listener = listener; // <-- Thêm dòng này
    }

    @Override
    public int getCount() {
        return purchases == null ? 0 : purchases.size();
    }

    @Override
    public Products getItem(int i) {
        return purchases.get(i);
    }

    @Override
    public long getItemId(int i) {
        // Trả về ID sản phẩm từ database
        return getItem(i).getId();
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ViewHolder holder; // Sử dụng ViewHolder Pattern

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.custome_purchases_products, null, false);
            holder = new ViewHolder();
            holder.img = v.findViewById(R.id.img_products_purchases);
            holder.tv_name = v.findViewById(R.id.tv_name_purchases);
            holder.tv_price = v.findViewById(R.id.tv_price_purchases);
            holder.tv_brand = v.findViewById(R.id.tv_brand_purchases);
            holder.rating = v.findViewById(R.id.rating_purchases);
            holder.tv_quantity = v.findViewById(R.id.tv_quantity);
            // Ánh xạ các nút mới
            holder.iv_delete = v.findViewById(R.id.iv_delete_item);
            holder.iv_plus = v.findViewById(R.id.iv_plus_quantity);
            holder.iv_minus = v.findViewById(R.id.iv_minus_quantity);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // Lấy sản phẩm ở vị trí 'i'
        Products p = getItem(i);

        // Set dữ liệu (Giữ nguyên code của bạn)
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            int resId = context.getResources().getIdentifier(p.getImage().trim().toLowerCase(), "drawable", context.getPackageName());
            if (resId != 0) holder.img.setImageResource(resId);
            else holder.img.setImageResource(R.drawable.products);
        } else {
            holder.img.setImageResource(R.drawable.products);
        }
        holder.tv_name.setText(p.getName());
        holder.tv_price.setText(p.getPrice() + "$");
        holder.tv_brand.setText(p.getBrand());
        holder.rating.setRating(p.getRating());
        holder.tv_quantity.setText(String.valueOf(p.getQuantity()));

        // === SỬA LẠI BƯỚC 3: Thêm listener vào các hàm ===

        // 1. Logic nút XÓA (Delete)
        holder.iv_delete.setOnClickListener(v_delete -> {
            boolean isDeleted = db.deleteProductFromPurchases(p.getId());
            if (isDeleted) {
                purchases.remove(i); // Xóa khỏi danh sách hiện tại
                notifyDataSetChanged(); // Cập nhật lại ListView
                Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                listener.onCartChanged(); // <-- GỌI INTERFACE
            } else {
                Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Logic nút TĂNG (Update +)
        holder.iv_plus.setOnClickListener(v_plus -> {
            int newQuantity = p.getQuantity() + 1;
            boolean isUpdated = db.updateProductInPurchases(p.getId(), newQuantity);
            if (isUpdated) {
                p.setQuantity(newQuantity); // Cập nhật số lượng trong danh sách
                notifyDataSetChanged(); // Cập nhật lại ListView
                listener.onCartChanged(); // <-- GỌI INTERFACE
            }
        });

        // 3. Logic nút GIẢM (Update -)
        holder.iv_minus.setOnClickListener(v_minus -> {
            int newQuantity = p.getQuantity() - 1;
            if (newQuantity > 0) {
                // Nếu > 0, chỉ cập nhật
                boolean isUpdated = db.updateProductInPurchases(p.getId(), newQuantity);
                if (isUpdated) {
                    p.setQuantity(newQuantity);
                    notifyDataSetChanged();
                    listener.onCartChanged(); // <-- GỌI INTERFACE
                }
            } else {
                // Nếu = 0, chạy logic XÓA
                boolean isDeleted = db.deleteProductFromPurchases(p.getId());
                if (isDeleted) {
                    purchases.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    listener.onCartChanged(); // <-- GỌI INTERFACE
                }
            }
        });

        return v;
    }

    // Class ViewHolder để tối ưu hiệu năng
    static class ViewHolder {
        ImageView img, iv_delete, iv_plus, iv_minus;
        TextView tv_name, tv_price, tv_brand, tv_quantity;
        RatingBar rating;
    }
}