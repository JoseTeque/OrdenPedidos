package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import joseguerra.ordereat.R;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.interfaz.InterfaceClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    TextView txt_nameCart, txt_PrecioCart;
    ElegantNumberButton btn_quantity;
    ImageView imageView;

  private   RelativeLayout view_background;
  public   LinearLayout view_foreground;

    private InterfaceClickListener clickListener;

    public void setTxt_nameCart(TextView txt_nameCart) {
        this.txt_nameCart = txt_nameCart;
    }

     CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_nameCart=itemView.findViewById(R.id.Id_txtitemnombreCart);
        txt_PrecioCart=itemView.findViewById(R.id.Id_txtitemPrecioCart);
        btn_quantity=itemView.findViewById(R.id.Id_btn_quantity);
        imageView=itemView.findViewById(R.id.Id_Image_cart);
        view_background= itemView.findViewById(R.id.IdRowBackground);
        view_foreground= itemView.findViewById(R.id.IdForeground);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

        clickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(" Select the action ");


        menu.add(0,0,getAdapterPosition(), Common.Delete);
    }
}
