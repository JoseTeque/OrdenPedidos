package joseguerra.ordereat.viewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import joseguerra.ordereat.CartActivity;
import joseguerra.ordereat.ComidaDetallesActivity;
import joseguerra.ordereat.FavoritesActivity;
import joseguerra.ordereat.ListaComidaActivity;
import joseguerra.ordereat.R;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Favorites;
import joseguerra.ordereat.modelo.Order;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private List<Favorites> listData;
    private Context favoritos;

    public FavoritesAdapter(List<Favorites> listData, FavoritesActivity favorites) {
        this.listData = listData;
        this.favoritos = favorites;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(favoritos).inflate(R.layout.favorites_item,viewGroup,false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder favoritesViewHolder, @SuppressLint("RecyclerView") final int i) {

        Favorites favorites= listData.get(i);

        favoritesViewHolder.txtName.setText(favorites.getFoodname());

        favoritesViewHolder.txtprecio.setText(String.format("$ %s", favorites.getFoodprice()));

        Picasso.get().load(favorites.getFoodimage()).into(favoritesViewHolder.image);

        favoritesViewHolder.imageCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isxist = new Database(favoritos).checkFooidExixt(listData.get(i).getFoodId(), Common.currentUser.getPhone());
                if (!isxist) {
                    new Database(favoritos).addCart(new Order(
                            Common.currentUser.getPhone(),
                            listData.get(i).getFoodId(),
                            listData.get(i).getFoodname(),
                            "1",
                            listData.get(i).getFoodprice(),
                            listData.get(i).getFooddiscount(), listData.get(i).getFoodimage()
                    ));

                } else {
                    new Database(favoritos).incrementarCart(Common.currentUser.getPhone(), listData.get(i).getFoodId());
                }

                Toast.makeText(favoritos, "Add to Card", Toast.LENGTH_SHORT).show();
            }
        });

          favoritesViewHolder.setClickListener(new InterfaceClickListener() {
              @Override
              public void onClick(View view, int position, boolean inlongclick) {
                  // Star new activity
                  Intent intent= new Intent(favoritos, ComidaDetallesActivity.class);
                  intent.putExtra("Foods",listData.get(i).getFoodId());
                  favoritos.startActivity(intent);
              }
          });


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

    public void restoreItem(Favorites item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }

    public Favorites getItem(int position)
    {
        return listData.get(position);
    }

}
