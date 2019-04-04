package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.interfaz.InterfaceClickListener;

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     TextView txtprecio,txtName;
     ImageView image,imageCart;

     private RelativeLayout view_background;
     public LinearLayout view_foreground;

    private InterfaceClickListener clickListener;

     FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName= itemView.findViewById(R.id.IdTxtNamelistaitem);
        txtprecio= itemView.findViewById(R.id.IdTxtFoodPrice);
        image= itemView.findViewById(R.id.IdImagelista);
        imageCart= itemView.findViewById(R.id.IdBtn_quick_cart);
        view_background= itemView.findViewById(R.id.IdRowBackgroundFavorites);
        view_foreground= itemView.findViewById(R.id.Id_view_background_favorites);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v,getAdapterPosition(),false);
    }


    public void setClickListener(InterfaceClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
