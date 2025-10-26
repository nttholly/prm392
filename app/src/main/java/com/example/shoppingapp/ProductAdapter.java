package com.example.shoppingapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    ArrayList<Products> products;
    OnRecyclerViewClickListener listener;

    public ProductAdapter(ArrayList<Products> products, OnRecyclerViewClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custome_card_products, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products p = products.get(position);

        // image is String -> resolve to drawable id
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            int imageResId = holder.itemView.getContext().getResources()
                    .getIdentifier(p.getImage().trim().toLowerCase(), "drawable",
                            holder.itemView.getContext().getPackageName());
            if (imageResId != 0) {
                holder.img.setImageResource(imageResId);
            } else {
                holder.img.setImageResource(R.drawable.products);
            }
        } else {
            holder.img.setImageResource(R.drawable.products);
        }

        holder.name.setText(p.getName());
        holder.price.setText(p.getPrice() + "$");
        holder.brand.setText("Brand: " + p.getBrand());
        holder.number_pieces.setText(String.valueOf(p.getPieces()));

        if (p.getDiscount() > 0) {
            double priceAfter = p.getPrice() - (p.getPrice() * (p.getDiscount() / 100.0));
            holder.priceAfter.setText(priceAfter + "$");
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.price.setTextColor(Color.parseColor("#BFBFBF"));
        } else {
            holder.priceAfter.setText("");
            holder.price.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setTag(p.getId());
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    // allow updating list
    public void setProducts(ArrayList<Products> newProducts) {
        this.products = newProducts;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price, brand, number_pieces, priceAfter;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_card_products);
            name = itemView.findViewById(R.id.tv_name_card_products);
            price = itemView.findViewById(R.id.tv_price_card_products);
            brand = itemView.findViewById(R.id.tv_brand_card_products);
            number_pieces = itemView.findViewById(R.id.tv_mun_pieces_card_products);
            priceAfter = itemView.findViewById(R.id.tv_priceafter_card_products);

            itemView.setOnClickListener(v -> {
                Object tag = itemView.getTag();
                if (tag instanceof Integer) {
                    listener.OnItemClick((int) tag);
                }
            });
        }
    }
}
