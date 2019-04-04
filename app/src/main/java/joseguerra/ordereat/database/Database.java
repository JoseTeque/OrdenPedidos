package joseguerra.ordereat.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import joseguerra.ordereat.modelo.Favorites;
import joseguerra.ordereat.modelo.Food;
import joseguerra.ordereat.modelo.Order;

public class Database extends SQLiteAssetHelper {

    private static  final String DB_NAME= "ordenDetalleNuevo.db";
    private static  final int DB_VER= 1;

    public Database(Context context) {
        super(context, DB_NAME,null, DB_VER);
    }

    public List<Order> getCards(String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        SQLiteQueryBuilder qb= new SQLiteQueryBuilder();

        String[] sqSelect={"userPhone","ProductoId","NombreProducto","Cantidad","Precio","Descuento","Image"};
        String sqTable="ordenDetalle";
        qb.setTables(sqTable);
        Cursor cursor= qb.query(db,sqSelect,"userPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result= new ArrayList<>();

        if(cursor.moveToFirst()){

            do {
                result.add(new Order(
                        cursor.getString(cursor.getColumnIndex("userPhone")),
                        cursor.getString(cursor.getColumnIndex("ProductoId")),
                        cursor.getString(cursor.getColumnIndex("NombreProducto")),
                        cursor.getString(cursor.getColumnIndex("Cantidad")),
                        cursor.getString(cursor.getColumnIndex("Precio")),
                        cursor.getString(cursor.getColumnIndex("Descuento")),
                        cursor.getString(cursor.getColumnIndex("Image"))
                        ));
            }while (cursor.moveToNext());
        }

        return result;
    }

    public boolean checkFooidExixt(String foodId, String userPhone)
    {
        boolean flog;
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor;
        String SQLQuery= String.format("SELECT * FROM ordenDetalle WHERE userPhone='%s' AND ProductoId='%s'", userPhone,foodId);
        cursor= db.rawQuery(SQLQuery,null);
        flog = cursor.getCount() > 0;
        cursor.close();
        return flog;
    }

    public void addCart(Order order){

        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("INSERT OR REPLACE INTO ordenDetalle(userPhone,ProductoId,NombreProducto,Cantidad,Precio,Descuento,Image) VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductoId(),
                order.getNombreProducto(),
                order.getCantidad(),
                order.getPrecio(),
                order.getDescuento(),order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone){

        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM ordenDetalle WHERE userPhone='%s'", userPhone);

        db.execSQL(query);
    }

    //Favorites

    public void AddFavorites(Favorites food)
    {
           SQLiteDatabase db= getReadableDatabase();
           String query= String.format("INSERT INTO Favorites(FoodId,Foodname,Foodimage,Fooddescription,Foodprice,Fooddiscount,FoodmenuId,UserPhone)" +
                   " VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                   food.getFoodId(),
                   food.getFoodname(),
                   food.getFoodimage(),
                   food.getFooddescription(),
                   food.getFoodprice(),
                   food.getFooddiscount(),
                   food.getFoodmenuId(),
                   food.getUserPhone());
           db.execSQL(query);
    }

    public void removeFavorites(String foodId, String userphone)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM Favorites WHERE FoodId='%s' and UserPhone='%s';", foodId, userphone);
        db.execSQL(query);
    }

    public boolean isFavorites(String foodId, String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("SELECT * FROM Favorites WHERE FoodId='%s' and UserPhone='%s';", foodId,userPhone);
      Cursor cursor= db.rawQuery(query,null);
      if (cursor.getCount()<=0)
      {
          cursor.close();
          return false;
      }else
          cursor.close();
      return true;
    }

    public int getCounter(String userPhone) {
        int count= 0;

        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("SELECT COUNT(*) FROM ordenDetalle Where userPhone='%s'", userPhone);
        @SuppressLint("Recycle") Cursor cursor= db.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                count= cursor.getInt(0);
            }while (cursor.moveToNext());
        }
           return count;
    }

    public void updateCart(Order order) {

        SQLiteDatabase db= getReadableDatabase();
        @SuppressLint("DefaultLocale") String query= String.format("UPDATE ordenDetalle SET Cantidad= '%s' WHERE userPhone='%s' AND ProductoId ='%s'", order.getCantidad(),order.getUserPhone(), order.getProductoId());
        db.execSQL(query);
    }

    public void incrementarCart(String userphone, String FoodId) {

        SQLiteDatabase db= getReadableDatabase();
        @SuppressLint("DefaultLocale") String query= String.format("UPDATE ordenDetalle SET Cantidad= Cantidad+1 WHERE userPhone='%s' AND ProductoId ='%s'", userphone,FoodId);
        db.execSQL(query);
    }

    public void removefromCart(String productoId, String phone) {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM ordenDetalle WHERE userPhone='%s' AND ProductoId='%s'",phone,productoId);

        db.execSQL(query);
    }

    public void removefromFavorites(String foodId, String phone) {
        SQLiteDatabase db= getReadableDatabase();
        String query= String.format("DELETE FROM Favorites WHERE userPhone='%s' AND FoodId='%s'",phone,foodId);

        db.execSQL(query);
    }

    public List<Favorites> getAllFavorites(String userPhone)
    {
        SQLiteDatabase db= getReadableDatabase();
        SQLiteQueryBuilder qb= new SQLiteQueryBuilder();

        String[] sqSelect={"FoodId","Foodname","Foodimage","Fooddescription","Foodprice","Fooddiscount","FoodmenuId","UserPhone"};
        String sqTable="Favorites";
        qb.setTables(sqTable);
        Cursor cursor= qb.query(db,sqSelect,"userPhone=?",new String[]{userPhone},null,null,null);

        final List<Favorites> result= new ArrayList<>();

        if(cursor.moveToFirst()){

            do {
                result.add(new Favorites(
                        cursor.getString(cursor.getColumnIndex("FoodId")),
                        cursor.getString(cursor.getColumnIndex("Foodname")),
                        cursor.getString(cursor.getColumnIndex("Foodimage")),
                        cursor.getString(cursor.getColumnIndex("Fooddescription")),
                        cursor.getString(cursor.getColumnIndex("Foodprice")),
                        cursor.getString(cursor.getColumnIndex("Fooddiscount")),
                        cursor.getString(cursor.getColumnIndex("FoodmenuId")),
                        cursor.getString(cursor.getColumnIndex("UserPhone"))
                ));
            }while (cursor.moveToNext());
        }

        return result;
    }
}
