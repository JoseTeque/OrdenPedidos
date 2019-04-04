package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.interfaz.InterfaceClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView OrderName, OrderPhone, OrderStautus, OrderAddress;
    public ImageView IconDelete;


    private InterfaceClickListener clickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        OrderName= itemView.findViewById(R.id.Id_Order_name);
        OrderStautus= itemView.findViewById(R.id.Id_Order_status);
        OrderPhone= itemView.findViewById(R.id.Id_Order_phone);
        OrderAddress= itemView.findViewById(R.id.Id_Order_address);
        IconDelete= itemView.findViewById(R.id.IdIconDelete);


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
