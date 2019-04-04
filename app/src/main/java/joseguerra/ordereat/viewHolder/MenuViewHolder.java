package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.interfaz.InterfaceClickListener;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNameMenu;
    public ImageView imageView;

    private InterfaceClickListener clickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtNameMenu=itemView.findViewById(R.id.IdTxtNameMenu);
        imageView=itemView.findViewById(R.id.IdImage);

       itemView.setOnClickListener(this);
    }

    public void setClickListener(InterfaceClickListener clickListener) {
        this.clickListener = clickListener;

    }

    @Override
    public void onClick(View view) {

        clickListener.onClick(view,getAdapterPosition(),false);
    }
}
