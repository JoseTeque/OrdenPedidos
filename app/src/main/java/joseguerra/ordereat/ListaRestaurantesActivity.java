package joseguerra.ordereat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Categoria;
import joseguerra.ordereat.modelo.Restaurantes;
import joseguerra.ordereat.viewHolder.MenuViewHolder;
import joseguerra.ordereat.viewHolder.RestaurantesViewHolder;

public class ListaRestaurantesActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseRecyclerOptions<Restaurantes> options;
    private FirebaseRecyclerAdapter<Restaurantes, RestaurantesViewHolder> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_restaurantes);

        database= FirebaseDatabase.getInstance();
        reference= database.getReference();

        swipeRefreshLayout= findViewById(R.id.IdSwhipLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConectInter(getBaseContext()))
                    loadListaRestaurant();
                else
                {
                    Toast.makeText(getBaseContext(), "Check la conection a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //default, load for first time

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConectInter(getBaseContext()))
                    loadListaRestaurant();
                else
                {
                    Toast.makeText(getBaseContext(), "Check la conection a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //cargar menu
        recyclerView= findViewById(R.id.IdRecyclerRestaurantes);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        options= new FirebaseRecyclerOptions.Builder<Restaurantes>()
                .setQuery(reference.child("Restaurantes"),Restaurantes.class).build();

        adapter= new FirebaseRecyclerAdapter<Restaurantes, RestaurantesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RestaurantesViewHolder holder, int position, @NonNull Restaurantes model) {
                holder.txtNameRestau.setText(model.getName());
                Picasso.get().load(model.getImagen()).into(holder.imageResta);

                holder.setClickListener(new InterfaceClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean inlongclick) {
                        Intent intentLista= new Intent(ListaRestaurantesActivity.this,HomeActivity.class);
                        //because categoryId is key, so we just get key of this item
                        Common.restaurantSelectId= adapter.getRef(position).getKey();
                        startActivity(intentLista);
                    }
                });
            }

            @NonNull
            @Override
            public RestaurantesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurantes_item,viewGroup,false);
                return new RestaurantesViewHolder(view);
            }
        };

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void loadListaRestaurant() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation

        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();


    }
}
