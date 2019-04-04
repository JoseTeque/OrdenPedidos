package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.interfaz.InterfaceClickListener;

public class RestaurantesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtNameRestau;
    public ImageView imageResta;

    private InterfaceClickListener clickListener;

    public RestaurantesViewHolder(@NonNull View itemView) {
        super(itemView);

        txtNameRestau= itemView.findViewById(R.id. IdTxtNameRestaurantes);
        imageResta= itemView.findViewById(R.id. IdImageRestaurantes);
        itemView.setOnClickListener(this);
    }

    public void setClickListener(InterfaceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {

        clickListener.onClick(v,getAdapterPosition(),false);

    }
}
