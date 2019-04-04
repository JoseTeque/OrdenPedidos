package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Favorites;
import joseguerra.ordereat.modelo.Food;
import joseguerra.ordereat.modelo.Order;
import joseguerra.ordereat.viewHolder.FoodViewHolder;

public class SearchActivity extends AppCompatActivity {

    private FirebaseDatabase databaseLista;
    private DatabaseReference categoryLista;
    private FirebaseRecyclerOptions<Food> optionsLista;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapterLista;

    private RecyclerView recycler_food_lista;
    private RecyclerView.LayoutManager layoutManager;

    //swiplayout

    private SwipeRefreshLayout swipeRefreshLayout;

    //favorites
    Database dblocal;

    //Facebook share

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //create target from Picasso

    Target target= new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //create photo from bitmap
            SharePhoto photo= new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content= new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    // SearchBar funcionality
    private FirebaseRecyclerOptions<Food> SearchOptionsLista;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> SearchAdapterLista;
    private List<String> suggertList= new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //  firebase

        databaseLista= FirebaseDatabase.getInstance();
        categoryLista=databaseLista.getReference("Foods");

        //Init Facebook
        callbackManager= CallbackManager.Factory.create();
        shareDialog= new ShareDialog(SearchActivity.this);

        //local DB

        dblocal= new Database(this);

        // iniciar recyclerview

        recycler_food_lista=findViewById(R.id.IdListaFood);
        recycler_food_lista.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_food_lista.setLayoutManager(layoutManager);

        //because search function need category so we need paste code here
        //after getIntent categoriId
        //SearchBar

        materialSearchBar= findViewById(R.id.Id_SearchBarList);
        materialSearchBar.setHint("Enter your food");
        loadSuggest();//write function to load suggest from firebase
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //when user type their text, we will change suggest list

                List<String> suggest= new ArrayList<>();
                for (String search:suggertList){
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar to close
                // Restore original adapter
                if(!enabled)
                    recycler_food_lista.setAdapter(adapterLista);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result of search adapter

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        //LoadList
        loadListFood();
    }

    private void loadListFood() {

        optionsLista = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(categoryLista, Food.class).build();

        adapterLista= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(optionsLista) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Food model) {
                holder.txtNameFoodh.setText(model.getName());
                holder.txtFoodPrice.setText(String.format("$ %s", model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.imageViewListaItem);

                //Add favorites
                if (dblocal.isFavorites(adapterLista.getRef(position).getKey(), Common.currentUser.getPhone()))
                    holder.ImageFavo.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                //click btnQuickShooping

                holder.ImageQuickSho.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isxist = new Database(getBaseContext()).checkFooidExixt(adapterLista.getRef(position).getKey(), Common.currentUser.getPhone());
                        if (!isxist) {
                            new Database(getBaseContext()).addCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapterLista.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(), model.getImage()
                            ));

                        } else {
                            new Database(getBaseContext()).incrementarCart(Common.currentUser.getPhone(), adapterLista.getRef(position).getKey());
                        }

                        Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });

                //click Share

                holder.ImageShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(model.getImage()).into(target);
                    }
                });

                //click change status of favorites
                if (dblocal.isFavorites(adapterLista.getRef(position).getKey(),Common.currentUser.getPhone()))
                {
                    holder.ImageFavo.setImageResource(R.drawable.ic_favorite_black_24dp);
                }else
                {
                    holder.ImageFavo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                final Favorites favorites= new Favorites();
                favorites.setFoodId(adapterLista.getRef(position).getKey());
                favorites.setFooddescription(model.getDescription());
                favorites.setFooddiscount(model.getDiscount());
                favorites.setFoodimage(model.getImage());
                favorites.setFoodname(model.getName());
                favorites.setFoodmenuId(model.getMenuId());
                favorites.setFoodprice(model.getPrice());
                favorites.setUserPhone(Common.currentUser.getPhone());

                holder.ImageFavo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!dblocal.isFavorites(adapterLista.getRef(position).getKey(), Common.currentUser.getPhone())) {
                            dblocal.AddFavorites(favorites);
                            holder.ImageFavo.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(SearchActivity.this, model.getName() + " Esta añadido a Favoritos..!!", Toast.LENGTH_SHORT).show();
                        } else {
                            dblocal.removeFavorites(adapterLista.getRef(position).getKey(), Common.currentUser.getPhone());
                            holder.ImageFavo.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(SearchActivity.this, model.getName() + " Se removio de favoritos..!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //click a una categoria
                holder.setClickListener(new InterfaceClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean inlongclick) {


                        // Star new activity
                        Intent intent= new Intent(SearchActivity.this,ComidaDetallesActivity.class);
                        intent.putExtra("Foods",adapterLista.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item_lista,viewGroup,false);
                return new FoodViewHolder(view);
            }
        };

        adapterLista.startListening();
        recycler_food_lista.setAdapter(adapterLista);

    }

    private void startSearch(CharSequence text) {
        SearchOptionsLista= new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(categoryLista.orderByChild("name").equalTo(text.toString()),Food.class).build();
        SearchAdapterLista= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(SearchOptionsLista) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                holder.txtNameFoodh.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewListaItem);


                holder.setClickListener(new InterfaceClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean inlongclick) {

                        // Star new activity
                        Intent intent= new Intent(SearchActivity.this,ComidaDetallesActivity.class);
                        intent.putExtra("Foods",SearchAdapterLista.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                recycler_food_lista.setAdapter(SearchAdapterLista);
                SearchAdapterLista.startListening();
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item_lista,viewGroup,false);
                return new FoodViewHolder(view);
            }
        };
    }

    private void loadSuggest() {
        categoryLista.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Food item= snapshot.getValue(Food.class);
                            if (item!=null)
                                suggertList.add(item.getName());//Add nombre of food to suggest list
                        }
                        materialSearchBar.setLastSuggestions(suggertList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStop() {
        if (SearchAdapterLista!=null){
            SearchAdapterLista.stopListening();
        }
        if (adapterLista!=null)
            adapterLista.stopListening();
        super.onStop();
    }



}


