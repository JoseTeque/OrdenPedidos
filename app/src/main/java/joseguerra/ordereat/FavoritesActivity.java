package joseguerra.ordereat;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import joseguerra.ordereat.Helper.RecyclerItemTouchHelper;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.RecyclerItemTouchListener;
import joseguerra.ordereat.modelo.Favorites;
import joseguerra.ordereat.modelo.Order;
import joseguerra.ordereat.viewHolder.CartAdapter;
import joseguerra.ordereat.viewHolder.CartViewHolder;
import joseguerra.ordereat.viewHolder.FavoritesAdapter;
import joseguerra.ordereat.viewHolder.FavoritesViewHolder;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchListener {


    private RecyclerView recyclerView_Cart;
    private RecyclerView.LayoutManager layoutManager;


    RelativeLayout rootLayout;

    List<Favorites> listCart= new ArrayList<>();
    FavoritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView_Cart= findViewById(R.id.Id_recyclerFavorites);
        recyclerView_Cart.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView_Cart.setLayoutManager(layoutManager);
        rootLayout= findViewById(R.id.IdrootLayoutFavorites);

        //swipe to delete

        ItemTouchHelper.SimpleCallback itemTouch= new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT,this);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView_Cart);

        lodaListFoodFavorites();
    }

    private void lodaListFoodFavorites() {

        listCart= new Database(this).getAllFavorites(Common.currentUser.getPhone());
        adapter= new FavoritesAdapter(listCart,this);
        adapter.notifyDataSetChanged();
        recyclerView_Cart.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesViewHolder)
        {
            String name= ((FavoritesAdapter) Objects.requireNonNull(recyclerView_Cart.getAdapter())).getItem(viewHolder.getAdapterPosition()).getFoodname();

            final Favorites deleteItem= ((FavoritesAdapter)recyclerView_Cart.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex= viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removefromFavorites(deleteItem.getFoodId(),Common.currentUser.getPhone());

            //snackbar

            Snackbar snackbar= Snackbar.make(rootLayout, name + "Removed Item Favorites" ,Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).AddFavorites(deleteItem);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
