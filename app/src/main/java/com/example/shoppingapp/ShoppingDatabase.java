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
    // Tăng version lên 19 để đảm bảo onUpgrade chạy
    public static final int DB_VERSION = 19;

    // (Khai báo tên bảng, tên cột không đổi)
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

    // (Các hàm getAllProducts, getProduct, getProductForSearch không đổi)
    public ArrayList<Products> getAllProducts(String tableName) {
        return getAllProducts(tableName, null);
    }
    public ArrayList<Products> getAllProducts(String tableName, String sortOrder) {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + tableName;
        if (sortOrder != null && !sortOrder.isEmpty()) {
            sql += " ORDER BY " + TB_CLM_PRICE + " " + sortOrder;
        }
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID));
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") int pieces = c.getInt(c.getColumnIndex(TB_CLM_PIECES));
                @SuppressLint("Range") String desc = c.getString(c.getColumnIndex(TB_CLM_DESCRIPTION));
                @SuppressLint("Range") double disc = c.getDouble(c.getColumnIndex(TB_CLM_DISCOUNT));
                Products p = new Products(id, image, name, price, brand, pieces, desc, disc);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
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
            return p;
        }
        c.close();
        return null;
    }
    public ArrayList<Products> getProductForSearch(String nameProduct, String tableName) {
        return getProductForSearch(nameProduct, tableName, null);
    }
    public ArrayList<Products> getProductForSearch(String nameProduct, String tableName, String sortOrder) {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + tableName + " WHERE " + TB_CLM_NAME + " LIKE ?";
        if (sortOrder != null && !sortOrder.isEmpty()) {
            sql += " ORDER BY " + TB_CLM_PRICE + " " + sortOrder;
        }
        Cursor c = db.rawQuery(sql, new String[]{"%" + nameProduct + "%"});
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID));
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") int pieces = c.getInt(c.getColumnIndex(TB_CLM_PIECES));
                @SuppressLint("Range") String desc = c.getString(c.getColumnIndex(TB_CLM_DESCRIPTION));
                @SuppressLint("Range") double disc = c.getDouble(c.getColumnIndex(TB_CLM_DISCOUNT));
                Products p = new Products(id, image, name, price, brand, pieces, desc, disc);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // (Các hàm Purchases và Users không đổi)
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
        return res != -1;
    }
    public ArrayList<Products> getAllProductsInPurchases() {
        ArrayList<Products> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TB_PURCHASES, null);
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(TB_CLM_ID));
                @SuppressLint("Range") String image = c.getString(c.getColumnIndex(TB_CLM_IMAGE));
                @SuppressLint("Range") String name = c.getString(c.getColumnIndex(TB_CLM_NAME));
                @SuppressLint("Range") double price = c.getDouble(c.getColumnIndex(TB_CLM_PRICE));
                @SuppressLint("Range") String brand = c.getString(c.getColumnIndex(TB_CLM_BRAND));
                @SuppressLint("Range") float rating = c.getFloat(c.getColumnIndex(TB_CLM_RATING));
                @SuppressLint("Range") int qty = c.getInt(c.getColumnIndex(TB_CLM_QUANTITY));
                Products p = new Products(image, name, price, brand, rating, qty);
                p.setId(id);
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
    public boolean deleteProductFromPurchases(int productId) {
        SQLiteDatabase db = getWritableDatabase();
        int res = db.delete(TB_PURCHASES, TB_CLM_ID + "=?", new String[]{String.valueOf(productId)});
        return res > 0;
    }
    public boolean updateProductInPurchases(int productId, int newQuantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_QUANTITY, newQuantity);
        int res = db.update(TB_PURCHASES, v, TB_CLM_ID + "=?", new String[]{String.valueOf(productId)});
        return res > 0;
    }
    @SuppressLint("Range")
    public double getCartTotalPrice() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + TB_CLM_PRICE + " * " + TB_CLM_QUANTITY + ") as Total FROM " + TB_PURCHASES, null);
        double total = 0.0;
        if (c.moveToFirst()) {
            total = c.getDouble(c.getColumnIndex("Total"));
        }
        c.close();
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
            return u;
        }
        c.close();
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
        return id;
    }

    // === HÀM ĐÃ CẬP NHẬT (60 SẢN PHẨM) ===
    // (Khớp với 10 ảnh cũ + 50 ảnh mới bạn vừa xác nhận)
    private void insertInitialData(SQLiteDatabase db) {
        // === 10 SẢN PHẨM CŨ (CỦA BẠN) ===
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

        // === 50 SẢN PHẨM MỚI (THEO ĐÚNG DANH SÁCH BẠN ĐÃ LƯU) ===

        // TB_SHIRT (Thêm 5)
        insertProduct(new Products("shirt_blue_polo", "Áo Polo Xanh Navy", 35.0, "Ralph Lauren", 50, "Áo polo nam thanh lịch, 100% cotton", 0.0), TB_SHIRT, db);
        insertProduct(new Products("shirt_white_formal", "Áo Sơ Mi Trắng", 45.0, "An Phước", 30, "Áo sơ mi công sở, vải không nhăn", 10.0), TB_SHIRT, db);
        insertProduct(new Products("shirt_black_tshirt", "Áo Thun Đen Basic", 15.0, "Uniqlo", 100, "Áo thun cổ tròn, vải co giãn 4 chiều", 0.0), TB_SHIRT, db);
        insertProduct(new Products("shirt_flannel_plaid", "Áo Sơ Mi Flannel", 38.0, "Levi's", 25, "Áo flannel kẻ caro, chất vải dày dặn, ấm áp", 0.0), TB_SHIRT, db);
        insertProduct(new Products("shirt_henley_long", "Áo Henley Dài Tay", 29.0, "Coolmate", 40, "Áo henley vải waffle, 3 cúc", 0.0), TB_SHIRT, db);

        // TB_PANTS (Thêm 5)
        insertProduct(new Products("pants_blue_jeans", "Quần Jeans Xanh", 55.0, "Levi's", 40, "Quần jeans nam, dáng slim-fit, 100% cotton", 0.0), TB_PANTS, db);
        insertProduct(new Products("pants_beige_chino", "Quần Chino Kaki", 42.0, "Dockers", 30, "Quần chino màu be, 98% cotton, 2% spandex", 0.0), TB_PANTS, db);
        insertProduct(new Products("pants_black_cargo", "Quần Cargo Đen", 60.0, "Dickies", 20, "Quần túi hộp phong cách đường phố, 100% cotton", 10.0), TB_PANTS, db);
        insertProduct(new Products("pants_gray_trousers", "Quần Tây Xám", 50.0, "Owen", 35, "Quần tây công sở, ống đứng, 95% poly", 0.0), TB_PANTS, db);
        insertProduct(new Products("pants_jogger_gray", "Quần Jogger Xám", 35.0, "Adidas", 50, "Quần jogger nỉ, 3 sọc", 0.0), TB_PANTS, db);

        // TB_SHORTS (Thêm 5)
        insertProduct(new Products("shorts_denim_blue", "Quần Short Jeans", 25.0, "H&M", 50, "Quần short jeans mài nhẹ, 100% cotton", 0.0), TB_SHORTS, db);
        insertProduct(new Products("shorts_sport_black", "Quần Đùi Thể Thao", 20.0, "Nike", 60, "Quần short 2 lớp, vải Dri-FIT, 100% poly", 0.0), TB_SHORTS, db);
        insertProduct(new Products("shorts_cargo_beige", "Quần Short Túi Hộp", 30.0, "Old Navy", 40, "Quần short Kaki túi hộp, vải mềm", 0.0), TB_SHORTS, db);
        insertProduct(new Products("shorts_chino_navy", "Quần Short Chino", 28.0, "Uniqlo", 50, "Quần short chino xanh navy, 98% cotton", 10.0), TB_SHORTS, db);
        insertProduct(new Products("shorts_swim_red", "Quần Bơi Đỏ", 18.0, "Speedo", 30, "Quần bơi nam, vải mau khô", 0.0), TB_SHORTS, db);

        // TB_JACKET (Thêm 5)
        insertProduct(new Products("jacket_denim_blue", "Áo Khoác Jeans", 65.0, "Zara", 30, "Áo khoác jeans basic, 100% cotton", 0.0), TB_JACKET, db);
        insertProduct(new Products("jacket_leather_black", "Áo Khoác Da Đen", 120.0, "Topman", 15, "Áo khoác da Biker, da PU cao cấp", 10.0), TB_JACKET, db);
        insertProduct(new Products("jacket_bomber_green", "Áo Bomber Xanh Rêu", 70.0, "Alpha", 25, "Áo khoác bomber MA-1, 2 lớp", 0.0), TB_JACKET, db);
        insertProduct(new Products("jacket_windbreaker_black", "Áo Khoác Gió", 45.0, "The North Face", 40, "Áo khoác gió mỏng, chống nước", 0.0), TB_JACKET, db);
        insertProduct(new Products("jacket_hoodie_zip_gray", "Áo Hoodie Khóa Kéo", 39.0, "Uniqlo", 50, "Áo hoodie nỉ, có khóa kéo, màu xám", 0.0), TB_JACKET, db);

        // TB_SHOES (Thêm 5)
        insertProduct(new Products("shoes_white_sneaker", "Giày Sneaker Trắng", 75.0, "Adidas", 50, "Giày Stan Smith, 100% da", 10.0), TB_SHOES, db);
        insertProduct(new Products("shoes_black_boots", "Giày Boots Da Đen", 110.0, "Dr. Martens", 20, "Giày boots cổ cao, 100% da thật", 0.0), TB_SHOES, db);
        insertProduct(new Products("shoes_brown_leather", "Giày Tây Da Nâu", 95.0, "Cole Haan", 30, "Giày tây nam, da bò thật, đế cao su", 0.0), TB_SHOES, db);
        insertProduct(new Products("shoes_running_blue", "Giày Chạy Bộ", 130.0, "Asics", 40, "Giày chạy bộ Gel-Kayano, siêu nhẹ", 0.0), TB_SHOES, db);
        insertProduct(new Products("shoes_sandals_brown", "Sandal Da Nâu", 30.0, "Birkenstock", 35, "Sandal 2 quai, đế trấu", 0.0), TB_SHOES, db);

        // TB_BELT (Thêm 5)
        insertProduct(new Products("belt_leather_black", "Thắt Lưng Da Đen", 25.0, "Calvin Klein", 40, "Thắt lưng da, 100% da bò, khóa kim", 0.0), TB_BELT, db);
        insertProduct(new Products("belt_leather_brown", "Thắt Lưng Da Nâu", 25.0, "Tommy", 40, "Thắt lưng da, 100% da bò, khóa kim", 0.0), TB_BELT, db);
        insertProduct(new Products("belt_canvas_web", "Thắt Lưng Vải Dù", 15.0, "Timex", 50, "Thắt lưng vải, nhiều màu", 0.0), TB_BELT, db);
        insertProduct(new Products("belt_braided_brown", "Thắt Lưng Bện", 20.0, "Polo", 30, "Thắt lưng bện co giãn", 0.0), TB_BELT, db);
        insertProduct(new Products("belt_formal_buckle", "Thắt Lưng Khóa Tự Động", 40.0, "An Phước", 20, "Thắt lưng da công sở, mặt khóa trượt", 0.0), TB_BELT, db);

        // TB_BAG (Thêm 5)
        insertProduct(new Products("bag_backpack_black", "Balo Đen", 50.0, "Herschel", 40, "Balo 20L, 100% poly, có ngăn laptop", 0.0), TB_BAG, db);
        insertProduct(new Products("bag_messenger_brown", "Túi Đeo Chéo Nâu", 70.0, "Fossil", 20, "Túi da đeo chéo, da thật", 0.0), TB_BAG, db);
        insertProduct(new Products("bag_duffle_gray", "Túi Trống Du Lịch", 45.0, "Nike", 30, "Túi du lịch 40L, 100% poly", 0.0), TB_BAG, db);
        insertProduct(new Products("bag_tote_canvas", "Túi Tote Vải", 15.0, "Ananas", 50, "Túi tote vải canvas, in hình", 0.0), TB_BAG, db);
        insertProduct(new Products("bag_laptop_briefcase", "Cặp Laptop", 55.0, "Targus", 25, "Cặp laptop 15.6 inch, chống sốc", 0.0), TB_BAG, db);

        // TB_HAT (Thêm 5)
        insertProduct(new Products("hat_baseball_black", "Mũ Lưỡi Trai Đen", 20.0, "New Era", 50, "Mũ NY Yankees, 100% cotton", 0.0), TB_HAT, db);
        insertProduct(new Products("hat_bucket_white", "Mũ Bucket Trắng", 18.0, "Kangol", 30, "Mũ tai bèo, 100% cotton", 0.0), TB_HAT, db);
        insertProduct(new Products("hat_beanie_gray", "Mũ Len Xám", 15.0, "Carhartt", 40, "Mũ len giữ ấm, 100% acrylic", 0.0), TB_HAT, db);
        insertProduct(new Products("hat_snapback_red", "Mũ Snapback Đỏ", 22.0, "Mitchell & Ness", 20, "Mũ Chicago Bulls, 100% poly", 0.0), TB_HAT, db);
        insertProduct(new Products("hat_fedora_brown", "Mũ Phớt Nâu", 35.0, "Stetson", 10, "Mũ phớt vành rộng, vải nỉ", 0.0), TB_HAT, db);

        // TB_DRESS (Thêm 5)
        insertProduct(new Products("dress_floral_midi", "Váy Hoa Nhí Midi", 45.0, "Zara", 30, "Váy hoa, 100% voan lụa, có lớp lót", 0.0), TB_DRESS, db);
        insertProduct(new Products("dress_black_little", "Váy Đen Ngắn", 50.0, "H&M", 40, "Váy đen dự tiệc, ôm body", 10.0), TB_DRESS, db);
        insertProduct(new Products("dress_white_maxi", "Váy Maxi Trắng", 60.0, "Mango", 20, "Váy maxi đi biển, 2 dây, 100% cotton", 0.0), TB_DRESS, db);
        insertProduct(new Products("dress_denim_shirt", "Váy Sơ Mi Jeans", 55.0, "Levi's", 25, "Váy jeans (denim) cộc tay", 0.0), TB_DRESS, db);
        insertProduct(new Products("dress_red_wrap", "Váy Đỏ Đắp Chéo", 65.0, "Zara", 20, "Váy lụa đỏ, dáng wrap", 10.0), TB_DRESS, db);

        // TB_ACCESSORY (Thêm 5)
        insertProduct(new Products("accessory_sunglasses_aviator", "Kính Râm", 80.0, "Ray-Ban", 30, "Kính phi công, chống UV 400", 0.0), TB_ACCESSORY, db);
        insertProduct(new Products("accessory_wallet_black", "Ví Da Đen", 40.0, "Tommy", 40, "Ví da nam, 100% da bò", 0.0), TB_ACCESSORY, db);
        insertProduct(new Products("accessory_scarf_plaid", "Khăn Choàng Kẻ", 25.0, "Burberry", 25, "Khăn len, 100% cashmere", 0.0), TB_ACCESSORY, db);
        insertProduct(new Products("accessory_tie_blue_silk", "Cà Vạt Xanh", 20.0, "Vera Wang", 35, "Cà vạt lụa 100% tơ tằm", 0.0), TB_ACCESSORY, db);
        insertProduct(new Products("accessory_watch_silver", "Đồng Hồ Bạc", 120.0, "Seiko", 15, "Đồng hồ nam, dây kim loại, máy Quartz", 0.0), TB_ACCESSORY, db);

    }
    public int checkUserByEmail(String userName, String email) {
        SQLiteDatabase db = getReadableDatabase();
        int id = 0;
        Cursor c = db.query(TB_USERS,
                new String[]{TB_CLM_USER_ID},
                TB_CLM_USER_NAME + "=? AND " + TB_CLM_USER_EMAIL + "=?",
                new String[]{userName, email},
                null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                int idx = c.getColumnIndex(TB_CLM_USER_ID);
                if (idx != -1) id = c.getInt(idx);
            }
            c.close();
        }
        return id;
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(TB_CLM_USER_PASSWORD, newPassword);
        int rows = db.update(TB_USERS, v, TB_CLM_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

}