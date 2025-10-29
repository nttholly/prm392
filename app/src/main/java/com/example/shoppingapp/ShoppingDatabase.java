package com.example.shoppingapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ShoppingDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "shopping_db";
    public static final int DB_VERSION = 18; // Giữ nguyên version của bạn

    // tables
    public static final String TB_SHIRT = "shirt";
    public static final String TB_PANTS = "pants";
    public static final String TB_SHORTS = "shorts";
    public static final String TB_JACKET = "jacket";
    public static final String TB_SHOES = "shoes";
    public static final String TB_BELT = "belt";
    public static final String TB_BAG = "bag";
    public static final String TB_HAT = "hat";
    public static final String TB_DRESS = "dress";
    public static final String TB_ACCESSORY = "accessory";

    public static final String TB_USERS = "users";
    public static final String TB_PURCHASES = "purchases";
    public static final String TB_PRODUCT_DISCOUNT = "product_discount";

    // columns
    public static final String TB_CLM_ID = "id";
    public static final String TB_CLM_IMAGE = "image";
    public static final String TB_CLM_NAME = "name";
    public static final String TB_CLM_PRICE = "price";
    public static final String TB_CLM_BRAND = "brand";
    public static final String TB_CLM_PIECES = "pieces";
    public static final String TB_CLM_DESCRIPTION = "description";
    public static final String TB_CLM_DISCOUNT = "discount";
    public static final String TB_CLM_RATING = "rating";
    public static final String TB_CLM_QUANTITY = "quantity";

    // user columns
    public static final String TB_CLM_USER_ID = "user_id";
    public static final String TB_CLM_USER_NAME = "user_name";
    public static final String TB_CLM_USER_FULL_NAME = "full_name";
    public static final String TB_CLM_USER_PASSWORD = "user_password";
    public static final String TB_CLM_USER_EMAIL = "user_email";
    public static final String TB_CLM_USER_PHONE = "user_phone";
    public static final String TB_CLM_USER_IMAGE = "user_image";

    private final Context mContext;

    public ShoppingDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTables(TB_SHIRT));
        db.execSQL(createTables(TB_PANTS));
        db.execSQL(createTables(TB_SHORTS));
        db.execSQL(createTables(TB_JACKET));
        db.execSQL(createTables(TB_SHOES));
        db.execSQL(createTables(TB_BELT));
        db.execSQL(createTables(TB_BAG));
        db.execSQL(createTables(TB_HAT));
        db.execSQL(createTables(TB_DRESS));
        db.execSQL(createTables(TB_ACCESSORY));

        db.execSQL(createTables(TB_PRODUCT_DISCOUNT));

        db.execSQL("CREATE TABLE " + TB_USERS + " (" +
                TB_CLM_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TB_CLM_USER_NAME + " TEXT UNIQUE, " +
                TB_CLM_USER_FULL_NAME + " TEXT, " +
                TB_CLM_USER_PASSWORD + " TEXT, " +
                TB_CLM_USER_EMAIL + " TEXT UNIQUE, " +
                TB_CLM_USER_PHONE + " TEXT, " +
                TB_CLM_USER_IMAGE + " TEXT);");

        db.execSQL("CREATE TABLE " + TB_PURCHASES + " (" +
                TB_CLM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TB_CLM_IMAGE + " TEXT, " +
                TB_CLM_NAME + " TEXT, " +
                TB_CLM_PRICE + " REAL, " +
                TB_CLM_BRAND + " TEXT, " +
                TB_CLM_RATING + " REAL, " +
                TB_CLM_QUANTITY + " INTEGER);");

        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        String[] tables = {
                TB_SHIRT, TB_PANTS, TB_SHORTS, TB_JACKET, TB_SHOES, TB_BELT, TB_BAG,
                TB_HAT, TB_DRESS, TB_ACCESSORY, TB_USERS, TB_PURCHASES, TB_PRODUCT_DISCOUNT
        };
        for (String t : tables) db.execSQL("DROP TABLE IF EXISTS " + t);
        onCreate(db);
    }

    private String createTables(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                TB_CLM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TB_CLM_IMAGE + " TEXT, " +
                TB_CLM_NAME + " TEXT, " +
                TB_CLM_PRICE + " REAL, " +
                TB_CLM_BRAND + " TEXT, " +
                TB_CLM_PIECES + " INTEGER, " +
                TB_CLM_DESCRIPTION + " TEXT, " +
                TB_CLM_DISCOUNT + " REAL);";
    }

    // --------- Products ---------
    public boolean insertProduct(Products p, String tableName, SQLiteDatabase db) {
        ContentValues v = new ContentValues();
        v.put(TB_CLM_IMAGE, p.getImage());
        v.put(TB_CLM_NAME, p.getName());
        v.put(TB_CLM_PRICE, p.getPrice());
        v.put(TB_CLM_BRAND, p.getBrand());
        v.put(TB_CLM_PIECES, p.getPieces());
        v.put(TB_CLM_DESCRIPTION, p.getDescription());
        v.put(TB_CLM_DISCOUNT, p.getDiscount());
        long res = db.insert(tableName, null, v);
        if (p.getDiscount() > 0) insertProductDiscount(p, db);
        return res != -1;
    }

    public boolean insertProductDiscount(Products p, SQLiteDatabase db) {
        ContentValues v = new ContentValues();
        v.put(TB_CLM_IMAGE, p.getImage());
        v.put(TB_CLM_NAME, p.getName());
        v.put(TB_CLM_PRICE, p.getPrice());
        v.put(TB_CLM_BRAND, p.getBrand());
        v.put(TB_CLM_PIECES, p.getPieces());
        v.put(TB_CLM_DESCRIPTION, p.getDescription());
        v.put(TB_CLM_DISCOUNT, p.getDiscount());
        long res = db.insert(TB_PRODUCT_DISCOUNT, null, v);
        return res != -1;
    }


    /**
     * SỬA LỖI: Lấy danh sách sản phẩm
     * Cần lấy ID để Adapter có thể gửi ID đúng
     */
    public ArrayList<Products> getAllProducts(String tableName) {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID)); // <-- LẤY ID
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") int pieces = c.getInt(c.getColumnIndex(TB_CLM_PIECES));
                @SuppressLint("Range") String desc = c.getString(c.getColumnIndex(TB_CLM_DESCRIPTION));
                @SuppressLint("Range") double disc = c.getDouble(c.getColumnIndex(TB_CLM_DISCOUNT));

                // Dùng constructor CÓ ID
                Products p = new Products(id, image, name, price, brand, pieces, desc, disc);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public Products getProduct(int id, String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + TB_CLM_ID + "=?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
            @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
            @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
            @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
            @SuppressLint("Range") int pieces = c.getInt(c.getColumnIndex(TB_CLM_PIECES));
            @SuppressLint("Range") String desc = c.getString(c.getColumnIndex(TB_CLM_DESCRIPTION));
            @SuppressLint("Range") double disc = c.getDouble(c.getColumnIndex(TB_CLM_DISCOUNT));
            Products p = new Products(id, image, name, price, brand, pieces, desc, disc);
            c.close();
            db.close();
            return p;
        }
        c.close();
        db.close();
        return null;
    }

    /**
     * SỬA LỖI: Lấy danh sách sản phẩm khi tìm kiếm
     * Cũng cần lấy ID giống như hàm getAllProducts
     */
    public ArrayList<Products> getProductForSearch(String nameProduct, String tableName) {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + TB_CLM_NAME + " LIKE ?", new String[]{"%" + nameProduct + "%"});
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID)); // <-- LẤY ID
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") int pieces = c.getInt(c.getColumnIndex(TB_CLM_PIECES));
                @SuppressLint("Range") String desc = c.getString(c.getColumnIndex(TB_CLM_DESCRIPTION));
                @SuppressLint("Range") double disc = c.getDouble(c.getColumnIndex(TB_CLM_DISCOUNT));

                // Dùng constructor CÓ ID
                Products p = new Products(id, image, name, price, brand, pieces, desc, disc);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // --------- Purchases ---------
    public boolean insertProductInPurchases(Products p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_IMAGE, p.getImage());
        v.put(TB_CLM_NAME, p.getName());
        v.put(TB_CLM_PRICE, p.getPrice());
        v.put(TB_CLM_BRAND, p.getBrand());
        v.put(TB_CLM_RATING, p.getRating());
        v.put(TB_CLM_QUANTITY, p.getQuantity());
        long res = db.insert(TB_PURCHASES, null, v);
        db.close();
        return res != -1;
    }

    /**
     * SỬA LỖI: Lấy tất cả sản phẩm trong giỏ hàng
     * Cần lấy cả ID để dùng cho việc Sửa/Xóa
     */
    public ArrayList<Products> getAllProductsInPurchases() {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TB_PURCHASES, null);
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID)); // <-- LẤY ID
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") float rating = c.getFloat(c.getColumnIndex(TB_CLM_RATING));
                @SuppressLint("Range") int qty = c.getInt(c.getColumnIndex(TB_CLM_QUANTITY));

                Products p = new Products(image, name, price, brand, rating, qty);
                p.setId(id); // <-- SET ID CHO SẢN PHẨM

                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    /**
     * HÀM MỚI: Xóa một sản phẩm khỏi giỏ hàng (Delete)
     */
    public boolean deleteProductFromPurchases(int productId) {
        SQLiteDatabase db = getWritableDatabase();
        int res = db.delete(TB_PURCHASES, TB_CLM_ID + "=?", new String[]{String.valueOf(productId)});
        db.close();
        return res > 0;
    }

    /**
     * HÀM MỚI: Cập nhật số lượng sản phẩm trong giỏ hàng (Update)
     */
    public boolean updateProductInPurchases(int productId, int newQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_QUANTITY, newQuantity);
        int res = db.update(TB_PURCHASES, v, TB_CLM_ID + "=?", new String[]{String.valueOf(productId)});
        db.close();
        return res > 0;
    }

    /**
     * HÀM MỚI: Tính tổng tiền của tất cả sản phẩm trong giỏ hàng
     */
    @SuppressLint("Range")
    public double getCartTotalPrice() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + TB_CLM_PRICE + " * " + TB_CLM_QUANTITY + ") as Total FROM " + TB_PURCHASES, null);
        double total = 0.0;
        if (c.moveToFirst()) {
            total = c.getDouble(c.getColumnIndex("Total"));
        }
        c.close();
        db.close();
        return total;
    }


    // --------- Users ---------
    public boolean insertUser(Users user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_USER_NAME, user.getUserName());
        v.put(TB_CLM_USER_FULL_NAME, user.getFullName());
        v.put(TB_CLM_USER_PASSWORD, user.getUserPassword());
        v.put(TB_CLM_USER_EMAIL, user.getEmail());
        v.put(TB_CLM_USER_PHONE, user.getPhone());
        v.put(TB_CLM_USER_IMAGE, user.getUserImage());
        long res = db.insert(TB_USERS, null, v);
        db.close();
        return res != -1;
    }

    public Users getUser(int user_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TB_USERS + " WHERE " + TB_CLM_USER_ID + "=?", new String[]{String.valueOf(user_id)});
        if (c.moveToFirst()) {
            @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_USER_ID));
            @SuppressLint("Range") String userName = c.getString(c.getColumnIndex(TB_CLM_USER_NAME));
            @SuppressLint("Range") String fullName = c.getString(c.getColumnIndex(TB_CLM_USER_FULL_NAME));
            @SuppressLint("Range") String password = c.getString(c.getColumnIndex(TB_CLM_USER_PASSWORD));
            @SuppressLint("Range") String email = c.getString(c.getColumnIndex(TB_CLM_USER_EMAIL));
            @SuppressLint("Range") String phone = c.getString(c.getColumnIndex(TB_CLM_USER_PHONE));
            @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_USER_IMAGE));
            Users u = new Users(id, userName, fullName, image, password, email, phone);
            c.close();
            db.close();
            return u;
        }
        c.close();
        db.close();
        return null;
    }

    public boolean upDataUser(Users user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_USER_FULL_NAME, user.getFullName());
        v.put(TB_CLM_USER_EMAIL, user.getEmail());
        v.put(TB_CLM_USER_PHONE, user.getPhone());
        v.put(TB_CLM_USER_IMAGE, user.getUserImage());
        int res = db.update(TB_USERS, v, TB_CLM_USER_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
        return res > 0;
    }

    @SuppressLint("Range")
    public int checkUser(String user_name, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TB_USERS, new String[]{TB_CLM_USER_ID}, TB_CLM_USER_NAME + "=? AND " + TB_CLM_USER_PASSWORD + "=?", new String[]{user_name, password}, null, null, null);
        int id = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(TB_CLM_USER_ID));
            }
            c.close();
        }
        db.close();
        return id;
    }

    // ---------- initial data ----------
    private void insertInitialData(SQLiteDatabase db) {
        insertProduct(new Products("tshirt_red", "Áo thun đỏ", 28.0, "Coolmate", 20, "Áo thun đỏ basic", 5.0), TB_SHIRT, db);
        insertProduct(new Products("pants1", "Quần kaki nam", 40.0, "Uniqlo", 35, "Quần kaki thoải mái", 0.0), TB_PANTS, db);
        insertProduct(new Products("shorts1", "Quần short thể thao", 32.0, "Puma", 40, "Quần nhẹ và thoáng khí", 0.0), TB_SHORTS, db);
        insertProduct(new Products("jacket1", "Áo khoác gió nam", 65.0, "The North Face", 22, "Chống gió, chống nước nhẹ", 5.0), TB_JACKET, db);
        insertProduct(new Products("shoes1", "Giày sneaker trắng", 150.0, "Adidas", 18, "Sneaker phong cách tối giản", 10.0), TB_SHOES, db);
        insertProduct(new Products("belt1", "Thắt lưng da nam", 120.0, "Gucci", 10, "Thắt lưng da cao cấp", 0.0), TB_BELT, db);
        insertProduct(new Products("bag1", "Túi xách thời trang nữ", 350.0, "Chanel", 5, "Túi cao cấp", 0.0), TB_BAG, db);
        insertProduct(new Products("hat1", "Mũ lưỡi trai thể thao", 45.0, "Nike", 30, "Mũ nhẹ, che nắng tốt", 10.0), TB_HAT, db);
        insertProduct(new Products("dress1", "Váy xòe dạo phố", 95.0, "Zara", 25, "Váy trẻ trung năng động", 15.0), TB_DRESS, db);
        insertProduct(new Products("watch1", "Đồng hồ nam", 500.0, "Casio", 12, "Chống nước, dây kim loại", 20.0), TB_ACCESSORY, db);
    }
}