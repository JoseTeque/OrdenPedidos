package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.interfaz.InterfaceClickListener;


public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNameFoodh, txtFoodPrice;
    public ImageView imageViewListaItem, ImageFavo, ImageShare,ImageQuickSho;

    private InterfaceClickListener clickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        txtNameFoodh=itemView.findViewById(R.id.IdTxtNamelistaitem);
        txtFoodPrice=itemView.findViewById(R.id.IdTxtFoodPrice);
        imageViewListaItem=itemView.findViewById(R.id.IdImagelista);
        ImageFavo= itemView.findViewById(R.id.IdFav);
        ImageShare= itemView.findViewById(R.id.IdShare);
        ImageQuickSho= itemView.findViewById(R.id.IdBtn_quick_cart);

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
