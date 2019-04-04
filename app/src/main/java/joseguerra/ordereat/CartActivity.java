package joseguerra.ordereat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import joseguerra.ordereat.Helper.RecyclerItemTouchHelper;
import joseguerra.ordereat.Remote.ApiService;
import joseguerra.ordereat.Remote.IGoogleService;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.common.Config;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.interfaz.RecyclerItemTouchListener;
import joseguerra.ordereat.modelo.DataMessage;
import joseguerra.ordereat.modelo.MyResponse;
import joseguerra.ordereat.modelo.Order;
import joseguerra.ordereat.modelo.Request;
import joseguerra.ordereat.modelo.Token;
import joseguerra.ordereat.viewHolder.CartAdapter;
import joseguerra.ordereat.viewHolder.CartViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchListener {

    private static final int PAYPAL_REQUESTS_CODE = 9999;
    private static final int LOCATION_PERMISSION_REQUEST = 500;
    private static final int PLAY_SERVICES_REQUEST = 9997;
    private RecyclerView recyclerView_Cart;
    private RecyclerView.LayoutManager layoutManager;
    public TextView txtPrecio;

    RelativeLayout rootLayout;

    private Button btnPlaceOrder;

    private FirebaseDatabase database;
    private DatabaseReference databaseReferenceCart;

    List<Order> listCart= new ArrayList<>();
    CartAdapter adapter;

    ApiService myService;

    Place shippingAddres;

    //Add Location

    private LocationCallback mLastLocationCallback;
    private FusedLocationProviderClient apiClientProvider;
    private LocationRequest locationRequest;
    private Location location;
    private LatLng yourLocation;

    private static final int UPDATE_INTERVAL = 1000;
    private static final int FATEST_INTERVAL = 5000;
    private static final int DISPLACEMENT = 10;

    public String url;
    public static final String key="AIzaSyBHWTUhRCbOLL2Gv2cttYBnkuZQw4f8NVE";

    //declare google api retrofit
    IGoogleService mGoogleService;


    //create paypal

    static PayPalConfiguration config= new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String comment="", address="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //note: add this code before setContentview method

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                          .setDefaultFontPath("fonts/restaurant_font.otf")
                           .setFontAttrId(R.attr.fontPath)
                          .build());

        setContentView(R.layout.activity_cart);

        //Runtime Permission location

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (CheckPlayService()) {
            InitFused();
        }

        //init paypal

        Intent intent= new Intent(CartActivity.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Init Retrofit service
         myService= Common.getFCMservice();

         //Init Google service
        mGoogleService= Common.getGoogleservice();

        //firebase

        database= FirebaseDatabase.getInstance();
        databaseReferenceCart= database.getReference("Restaurantes").child(Common.restaurantSelectId).child("Resquests");

        // init view

        btnPlaceOrder= findViewById(R.id.IdBtnPlaceOrder);
        txtPrecio= findViewById(R.id.Id_Total);
        recyclerView_Cart= findViewById(R.id.Id_Lista_Cart);
        recyclerView_Cart.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView_Cart.setLayoutManager(layoutManager);
        rootLayout= findViewById(R.id.IdrootLayout);

        //swipe to delete

        ItemTouchHelper.SimpleCallback itemTouch= new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT,this);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView_Cart);


        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listCart.size()>0)
                 ShowAletDialog();
                else
                {
                    Toast.makeText(CartActivity.this, "El carro esta vacio..!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lodaListFood();

    }

    private boolean CheckPlayService() {
        int resulCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resulCode!= ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resulCode)) {
                GooglePlayServicesUtil.getErrorDialog(resulCode, this, PLAY_SERVICES_REQUEST).show();
            }else {
                 Toast.makeText(this, "This Device Not Support", Toast.LENGTH_SHORT).show();
                 finish();

            }
            return false;
        }
        return true;
    }

    private void checkPermission(String accessFineLocation, String accessCoarseLocation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    private void InitFused() {
        apiClientProvider = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        builLocationRequest();
        builLocationCallback();
        apiClientProvider.requestLocationUpdates(locationRequest, mLastLocationCallback, Looper.myLooper());
    }

    private void builLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void builLocationCallback()
    {
        mLastLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLocations().get(locationResult.getLocations().size()-1);

                if (ActivityCompat.checkSelfPermission(CartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                apiClientProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                              yourLocation= new LatLng(location.getLatitude(),location.getLongitude());

                             Log.d("Location","Your Location : " + yourLocation.latitude + "," + yourLocation.longitude);
                        }
                    }
                });
            }
        };

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void ShowAletDialog() {

        AlertDialog.Builder alertdialog= new AlertDialog.Builder(CartActivity.this);
        alertdialog.setTitle("One more step");
        alertdialog.setMessage("Enter Your address...");

        LayoutInflater inflater= this.getLayoutInflater();
        @SuppressLint("InflateParams") View order_address_comment= inflater.inflate(R.layout.order_address_comment,null);

    //    final MaterialEditText edtxAddress= order_address_comment.findViewById(R.id.IdEdtAddress);
        //Init Places the google

        Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));
        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));
        }
        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment edtxAddress=(AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete);
        //Hide search icon before fragment
        assert edtxAddress != null;
        edtxAddress.setPlaceFields(Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME));
        //set hint for autocomplete Edit text
        ((EditText) Objects.requireNonNull(edtxAddress.getView()).findViewById(R.id.places_autocomplete_search_input)).setHint("Enter your address");
        //set text size
        ((EditText)edtxAddress.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(14);

        //GET ADDRESS AUTOCOMPLETE
      edtxAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
          @Override
          public void onPlaceSelected(@NonNull Place place) {
            shippingAddres=place;
          }

          @Override
          public void onError(@NonNull Status status) {
              Log.e("ERROR",status.getStatusMessage());
          }
      });

        final MaterialEditText edtxComment= order_address_comment.findViewById(R.id.IdEdtComment);

        final RadioButton rdiShipAdress= order_address_comment.findViewById(R.id.IdRdiShipToAddress);
        final RadioButton rdiHomeAdress= order_address_comment.findViewById(R.id.IdRdiHomeAddress);
        final RadioButton rdiCOD= order_address_comment.findViewById(R.id.IdRdiCashOnDelivery);
        final RadioButton rdiPaypal= order_address_comment.findViewById(R.id.IdRdipaypal);



        //EVENT RADIOBUTTON

        rdiShipAdress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               //ship to this address features
               if (isChecked) //isChecked==true
               {
                   url= "https://maps.googleapis.com/maps/api/geocode/json?latlng="+yourLocation.latitude+","+yourLocation.longitude+"&key="+key+"";
                   Log.d("URL",url);
                 mGoogleService.getAddressName(url).enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        //if fetch Api ok
                         try {
                             JSONObject jsonObject= new JSONObject(response.body());
                             JSONArray jsonArray = jsonObject.getJSONArray("results");

                             JSONObject firstObject= jsonArray.getJSONObject(0);
                             address= firstObject.getString("formatted_address");


                             //set address to edtAddress
                             ((EditText)edtxAddress.getView().findViewById(R.id.places_autocomplete_search_input)).setText(address);


                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }

                     @Override
                     public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                         Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 });
               }

            }
        });

        rdiHomeAdress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    if (TextUtils.isEmpty(Common.currentUser.getHomeAddress())|| Common.currentUser.getHomeAddress()!= null)
                    {
                        address = Common.currentUser.getHomeAddress();
                        ((EditText)edtxAddress.getView().findViewById(R.id.places_autocomplete_search_input)).setText(address);
                    }else {

                        Toast.makeText(CartActivity.this, "Please update your home address", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        alertdialog.setView(order_address_comment);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Add Check condition here
                //if user select address from place fragment, just use it.
                //if use select ship to this address , get address from location and use it.
                //if use select home address  , get home address from profile  and use it.

                if (!rdiShipAdress.isChecked() && !rdiHomeAdress.isChecked()) {
                    if (shippingAddres != null) {
                        address = shippingAddres.getAddress();
                    } else {
                        Toast.makeText(CartActivity.this, "Ingrese una direccion o seleccione una direccion en los botones", Toast.LENGTH_SHORT).show();
                        // remove fragment
                        getSupportFragmentManager().beginTransaction()
                                .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete))).commit();
                        return;
                    }
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(CartActivity.this, "Ingrese una direccion o seleccione una direccion en los botones", Toast.LENGTH_SHORT).show();
                    // remove fragment
                    getSupportFragmentManager().beginTransaction()
                            .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete))).commit();
                    return;
                }


                comment = Objects.requireNonNull(edtxComment.getText()).toString();

                //check payment
                if (!rdiCOD.isChecked() && !rdiPaypal.isChecked()) {
                    Toast.makeText(CartActivity.this, "Ingrese un metodo de pago", Toast.LENGTH_SHORT).show();
                    // remove fragment
                    getSupportFragmentManager().beginTransaction()
                            .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete))).commit();
                } else if (rdiPaypal.isChecked()) {

                    String formatAumont = txtPrecio.getText().toString()
                            .replace("$", "")
                            .replace(",", "");

                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAumont),
                            "USD", "Eat it app Order", PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUESTS_CODE);

                    // remove fragment
                    getSupportFragmentManager().beginTransaction()
                            .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete))).commit();

                }
                else if (rdiCOD.isChecked())
                {
                    //copy code onActivityResult
                    //Create new Request
                    Request request= new Request(
                            "unpaid", //state from json
                            Common.currentUser.getPhone(),
                            Common.currentUser.getNombre(),
                            address,
                            txtPrecio.getText().toString(),"0",
                            String.format("%s,%s", yourLocation.latitude,yourLocation.longitude),
                            comment,
                            listCart,"COD",Common.restaurantSelectId
                    );


                    //Submit a firebase
                    //we will using System.currentMilli to key
                    String number_millis= String.valueOf(System.currentTimeMillis());

                    databaseReferenceCart.child(number_millis)
                            .setValue(request);

                    //delete cart

                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                    sendNotificationOrder(number_millis);

                    Toast.makeText(CartActivity.this, "Thanks you, Order Place", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        });

        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // remove fragment
                getSupportFragmentManager().beginTransaction()
                        .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.Id_Place_Autocomplete))).commit();
            }
        });

        alertdialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PAYPAL_REQUESTS_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                assert data != null;
                PaymentConfirmation confirmation= data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation!=null)
                {
                    try {
                        String paymentDetail= confirmation.toJSONObject().toString(4);

                        JSONObject jsonObject= new JSONObject(paymentDetail);


               //Create new Request
                Request request= new Request(
                        jsonObject.getJSONObject("response").getString("state"), //state from json
                        Common.currentUser.getPhone(),
                        Common.currentUser.getNombre(),
                        address,
                        txtPrecio.getText().toString(),"0",
                        String.format("%s,%s", yourLocation.latitude,yourLocation.longitude),
                        comment,
                        listCart,"Paypal",Common.restaurantSelectId
                );


                //Submit a firebase
                //we will using System.currentMilli to key
                String number_millis= String.valueOf(System.currentTimeMillis());

                databaseReferenceCart.child(number_millis)
                        .setValue(request);

                //delete cart

                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                sendNotificationOrder(number_millis);

                Toast.makeText(CartActivity.this, "Thanks you, Order Place", Toast.LENGTH_SHORT).show();
                  finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if (resultCode== Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Payment cancel", Toast.LENGTH_SHORT).show();
            }else if (resultCode== PaymentActivity.RESULT_EXTRAS_INVALID)
            {
                Toast.makeText(this, " Invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotificationOrder(final String number_millis) {

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Query data= reference.orderByChild("serverToken").equalTo(true); //get all node with isServerToken

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Token serverToken= snapshot.getValue(Token.class);

                    //create raw payload to send

         //           Notificatione notification= new Notificatione("Jose Dev", "have new order " + number_millis );
           //         Sender content= new Sender(serverToken.getToken(),notification);

                    Map<String,String> datasend= new HashMap<>();
                    datasend.put("Title","Jose Dev");
                    datasend.put("Message","have new order " + number_millis);

                    assert serverToken != null;
                    DataMessage dataMessage= new DataMessage(serverToken.getToken(),datasend);

                    myService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {

                            //only run when get result
                            if (response.code() == 200) {
                                assert response.body() != null;
                                if (response.body().succes == 1) {
                                    Toast.makeText(CartActivity.this, "Thanks you, Order Place", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    Toast.makeText(CartActivity.this, "failed..!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {
                              Log.e("ERROR",t.getMessage());
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lodaListFood() {


        listCart= new Database(this).getCards(Common.currentUser.getPhone());
        adapter= new CartAdapter(listCart,this);
        adapter.notifyDataSetChanged();
        recyclerView_Cart.setAdapter(adapter);

        //calcuando total precio

        int total= 0;
        for (Order order:listCart) {
            total += (Integer.parseInt(order.getPrecio())) * (Integer.parseInt(order.getCantidad()));

            Locale locale = new Locale("en", "US");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

            txtPrecio.setText(numberFormat.format(total));
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Delete))
        {
            deleteCart(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void deleteCart(int position) {
        if (listCart.size() > 1)
        {
            //WE WILL REMOVE ITEM AT LIST<ORDER> BY POSITION
            listCart.remove(position);
        //after that, we will delete all old data from sqlite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //and finaly, we will update new data from List<Order> to sqlite
        for (Order item : listCart)
            new Database(this).addCart(item);
        //Refresh
        lodaListFood();
    }else
        {
            listCart.remove(position);
            //after that, we will delete all old data from sqlite
            new Database(this).cleanCart(Common.currentUser.getPhone());
            //and finaly, we will update new data from List<Order> to sqlite
            for (Order item : listCart)
                new Database(this).addCart(item);
            //Refresh
            lodaListFood();
            txtPrecio.setText("00,00");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CheckPlayService()) {
                        InitFused();
                    }
                } else {
                    Toast.makeText(this, "ES NECESARIO DAR PERMISO", Toast.LENGTH_SHORT).show();
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onStop() {
        apiClientProvider.removeLocationUpdates(mLastLocationCallback);
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
              if (viewHolder instanceof CartViewHolder)
              {
                  String name= ((CartAdapter) Objects.requireNonNull(recyclerView_Cart.getAdapter())).getItem(viewHolder.getAdapterPosition()).getNombreProducto();

                  final Order deleteItem= ((CartAdapter)recyclerView_Cart.getAdapter()).getItem(viewHolder.getAdapterPosition());
                  final int deleteIndex= viewHolder.getAdapterPosition();

                  adapter.removeItem(deleteIndex);
                  new Database(getBaseContext()).removefromCart(deleteItem.getProductoId(),Common.currentUser.getPhone());

                  //update txtprice total

                  //calcuando total precio

                  int total= 0;

                  List<Order> orders= new Database(getBaseContext()).getCards(Common.currentUser.getPhone());

                  for (Order item:orders)
                      total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));

                  Locale locale = new Locale("en", "US");
                  NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

                  txtPrecio.setText(numberFormat.format(total));

                  //snackbar

                  Snackbar snackbar= Snackbar.make(rootLayout, name + "Removed Item cart" ,Snackbar.LENGTH_SHORT);
                  snackbar.setAction("UNDO", new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          adapter.restoreItem(deleteItem,deleteIndex);
                          new Database(getBaseContext()).addCart(deleteItem);

                          //update txtprice total

                          //calcuando total precio

                          int total= 0;

                          List<Order> orders= new Database(getBaseContext()).getCards(Common.currentUser.getPhone());

                          for (Order item:orders)
                              total += (Integer.parseInt(item.getPrecio())) * (Integer.parseInt(item.getCantidad()));

                          Locale locale = new Locale("en", "US");
                          NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

                          txtPrecio.setText(numberFormat.format(total));

                      }
                  });

                  snackbar.setActionTextColor(Color.YELLOW);
                  snackbar.show();
              }
    }
}
