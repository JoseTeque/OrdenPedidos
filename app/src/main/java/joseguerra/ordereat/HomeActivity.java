package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Banner;
import joseguerra.ordereat.modelo.Categoria;
import joseguerra.ordereat.modelo.Favorites;
import joseguerra.ordereat.modelo.Token;
import joseguerra.ordereat.viewHolder.MenuViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity" ;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private FirebaseRecyclerOptions<Categoria> options;
    private FirebaseRecyclerAdapter<Categoria, MenuViewHolder> adapter;

    private TextView txtNameUser;

    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;

    private  CounterFab fab;

    private SwipeRefreshLayout swipeRefreshLayout;

    //Slider Banner
    HashMap<String,String> image_list;
    SliderLayout sliderLayout;

    String restaurantId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //Init Paper

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu"); // poner titulo en el toolbar
        setSupportActionBar(toolbar);

        //GetIntent


        //Iniciar firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Restaurantes").child(Common.restaurantSelectId).child("Detalles").child("categoria");

        // se movio la funcion del adaptador porque se tiene que actualizar luego de instanciar database para poder obtener la animacion
        options = new FirebaseRecyclerOptions.Builder<Categoria>()
                .setQuery(category,Categoria.class).build();

        adapter= new FirebaseRecyclerAdapter<Categoria, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Categoria model) {
                holder.txtNameMenu.setText(model.getName());
                Picasso.get().load(model.getImagen()).into(holder.imageView);

                holder.setClickListener(new InterfaceClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean inlongclick) {
                        // Get category id and send to new activity
                        Intent intentLista= new Intent(HomeActivity.this,ListaComidaActivity.class);
                        //because categoryId is key, so we just get key of this item
                        intentLista.putExtra("categoriaId",adapter.getRef(position).getKey());
                        startActivity(intentLista);
                    }
                });

            }
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item,viewGroup,false);
                return new MenuViewHolder(view);
            }
        };

        //Init swipLayout

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
                  loadMenu();
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
                    loadMenu();
                else
                {
                    Toast.makeText(getBaseContext(), "Check la conection a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


         fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent= new Intent(HomeActivity.this,CartActivity.class);
               startActivity(intent);
                Log.d(TAG,"error al cargar lista");

            }
        });

        fab.setCount(new Database(this).getCounter(Common.currentUser.getPhone()));
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_viewHome);
        navigationView.setNavigationItemSelectedListener(this);




        //set name for user
        View headerView= navigationView.getHeaderView(0);
        txtNameUser=headerView.findViewById(R.id.IdTxtNameHeader);
        txtNameUser.setText(Common.currentUser.getNombre());// accediendo al nombre de la persona actual

        //cargar menu
        recycler_menu= findViewById(R.id.IdRecyclerMenu);
        layoutManager= new GridLayoutManager(this,2);
        recycler_menu.setLayoutManager(layoutManager);

        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall);
        recycler_menu.setLayoutAnimation(controller);


        //obtener token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                   String token= instanceIdResult.getToken();
                 UpdateToken(token);
            }
        });

        //function slider
        //need call this function after you this database firebase

        Setupslider();


    }

    private void Setupslider() {
        sliderLayout= findViewById(R.id.IdSliderBanner);
        image_list= new HashMap<>();

        final DatabaseReference banners= FirebaseDatabase.getInstance().getReference("Restaurantes").child(Common.restaurantSelectId).child("Detalles").child("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot post:dataSnapshot.getChildren()) {
                    Banner banner = post.getValue(Banner.class);
                    //No will concat string name and id like
                    //pizza_01 -> and we will use PIZZA  for show description, 01 for food id to click
                    assert banner != null;
                    image_list.put(banner.getName()+"@@@"+ banner.getId(), banner.getImagen());
                }
                for (String key:image_list.keySet())
                {
                    String[] keySplit= key.split("@@@");
                    String nameOfFood= keySplit[0];
                    String idOfFood= keySplit[1];

                    //create slider
                    final TextSliderView textSliderView= new TextSliderView(getBaseContext());
                    textSliderView.description(nameOfFood)
                            .image(image_list.get(key))
                            .setProgressBarVisible(true)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent= new Intent(HomeActivity.this,ComidaDetallesActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);

                                }
                            });
                    //add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("Foods",idOfFood);
                    sliderLayout.addSlider(textSliderView);

                    //remove event after finish
                    banners.removeEventListener(this);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }

    private void UpdateToken(String tokenrefresh) {
        FirebaseDatabase DB= FirebaseDatabase.getInstance();
        DatabaseReference tokens= DB.getReference("Tokens");

        Token token= new Token(tokenrefresh,false);//false because this tokensend from client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void loadMenu() {

        adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation

        Objects.requireNonNull(recycler_menu.getAdapter()).notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_search)
            startActivity(new Intent(HomeActivity.this,SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent= new Intent(HomeActivity.this,CartActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent= new Intent(HomeActivity.this,OrderStatusActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_logout) {

            //Delete remember user & password
            AccountKit.logOut();

            //Logout
            Intent signIn= new Intent(HomeActivity.this,MainActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }else if (id==R.id.nav_Update_Name)
        {
            showChangePassword();
        }
        else if (id==R.id.nav_HomeAddress)
        {
            showHomeAddress();
        }
        else if (id==R.id.nav_favo)
        {
            startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
        }

        else if (id==R.id.nav_setting)
        {
           showSettingDialog();
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingDialog() {

        final AlertDialog.Builder alertDialog= new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("SETTINGS");

        LayoutInflater inflater= LayoutInflater.from(this);
        @SuppressLint("InflateParams") View view= inflater.inflate(R.layout.setting_layout,null);

        final CheckBox checkBox= view.findViewById(R.id.IdCheckNew);

        //ADD CODE REMEMBER THE CHECKBOX
        Paper.init(this);
        String isSuscribe= Paper.book().read("seb_new");
        if (isSuscribe==null || TextUtils.isEmpty(isSuscribe) || isSuscribe.equals("false"))
            checkBox.setChecked(false);
        else
            checkBox.setChecked(true);

        alertDialog.setView(view);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (checkBox.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new","true");
                }else
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new","false");
                }

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();

    }

    private void showHomeAddress() {

        final AlertDialog.Builder alertDialog= new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change home Address");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater= LayoutInflater.from(this);
        @SuppressLint("InflateParams") View view= inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText edtxHomeAddress= view.findViewById(R.id.IdEdtxHomeAddress);

        alertDialog.setView(view);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();

                  //set new home address
                Common.currentUser.setHomeAddress(Objects.requireNonNull(edtxHomeAddress.getText()).toString());

                  FirebaseDatabase.getInstance().getReference("user")
                          .child(Common.currentUser.getPhone())
                          .setValue(Common.currentUser)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  Toast.makeText(HomeActivity.this, "Update Address Succesful", Toast.LENGTH_SHORT).show();
                              }
                          });
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void showChangePassword() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("UPDATE NAME");
        dialog.setMessage("Please fill all information");

        LayoutInflater inflater= LayoutInflater.from(this);
        @SuppressLint("InflateParams") View view= inflater.inflate(R.layout.update_name_layout,null);

        final MaterialEditText edtxName= view.findViewById(R.id.IdEdtxName);


        dialog.setView(view);

        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                 //change password here

                final android.app.AlertDialog witenDialog = new SpotsDialog.Builder().setContext(HomeActivity.this).build();
                        witenDialog.show();

                        //Update name
                Map<String,Object> name= new HashMap<>();
                name.put("nombre", Objects.requireNonNull(edtxName.getText()).toString());

                FirebaseDatabase.getInstance().getReference("user")
                        .child(Common.currentUser.getPhone())
                        .updateChildren(name)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                 witenDialog.dismiss();
                                 if (task.isSuccessful())
                                 {
                                     Toast.makeText(HomeActivity.this, "Name updateed..!!", Toast.LENGTH_SHORT).show();
                                 }
                            }
                        });



            }

        });


        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!= null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        if(adapter!=null ){
            adapter.stopListening();
        }
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCounter(Common.currentUser.getPhone()));
        //fix click back button from food and don´t see category
        if (adapter!=null)
            adapter.startListening();

    }
}
