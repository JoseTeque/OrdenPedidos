package joseguerra.ordereat.viewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import joseguerra.ordereat.CartActivity;
import joseguerra.ordereat.R;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Order;



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData;
    private CartActivity cart;

    public CartAdapter(List<Order> listData, CartActivity cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(cart).inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, @SuppressLint("RecyclerView") final int i) {

      /*  TextDrawable drawable= TextDrawable.builder()
                .buildRound(""+listData.get(i).getCantidad(), Color.RED);

        cartViewHolder.img_cart_count.setImageDrawable(drawable); */

      cartViewHolder.btn_quantity.setNumber(listData.get(i).getCantidad());

      cartViewHolder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
          @Override
          public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
              Order order= listData.get(i);
              order.setCantidad(String.valueOf(newValue));
              new Database(cart).updateCart(order);

              //update txtprice total

              //calcuando total precio

              int total= 0;

              List<Order> orders= new Database(cart).getCards(Common.currentUser.getPhone());

              for (Order item:orders) {
                  total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));

                  Locale locale = new Locale("en", "US");
                  NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

                  cart.txtPrecio.setText(numberFormat.format(total));
              }


          }
      });

        Locale locale= new Locale("en", "US");
        NumberFormat numberFormat= NumberFormat.getCurrencyInstance(locale);
        int precio=(Integer.parseInt(listData.get(i).getPrecio()))*(Integer.parseInt(listData.get(i).getCantidad()));

        cartViewHolder.txt_PrecioCart.setText(numberFormat.format(precio));

        cartViewHolder.txt_nameCart.setText(listData.get(i).getNombreProducto());

        Picasso.get().load(listData.get(i).getImage()).resize(70,70).into(cartViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }

    public Order getItem(int position)
    {
        return listData.get(position);
    }


}
