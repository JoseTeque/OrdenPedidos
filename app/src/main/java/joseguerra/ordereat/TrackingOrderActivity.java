package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import joseguerra.ordereat.Remote.IGoogleService;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.common.DirectionJSONParser;
import joseguerra.ordereat.modelo.Request;
import joseguerra.ordereat.modelo.ShippingInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TrackingOrderActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {


    private GoogleMap mMap;
    private Marker marker;
    private Polyline polyline;

    private FirebaseDatabase database;
    private DatabaseReference reference, shippiOrder;
    private Request currenOrder;


    public String url;

    private IGoogleService mServices;

    private String key= "AIzaSyBYUXw0WstF29S2o4V3qE2lmv7ozeOGZJQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        database= FirebaseDatabase.getInstance();
        reference= database.getReference("Requests");
        shippiOrder= database.getReference("ShippingOrders");
        shippiOrder.addValueEventListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //inicializando retrofit
        mServices= new Retrofit.Builder().baseUrl("https://maps.googleapis.com")
                .addConverterFactory(ScalarsConverterFactory.create()).build().create(IGoogleService.class);
    }





    private void TrackingLocation() {

    reference.child(Common.currentKey).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            currenOrder= dataSnapshot.getValue(Request.class);
            assert currenOrder != null;
            if (currenOrder.getAddress()!=null && !currenOrder.getAddress().isEmpty())
            {

                mServices.getGeocode(currenOrder.getAddress(), key).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {

                        try {
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body());

                            String lat = ((JSONArray) jsonObject.get("results"))
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location")
                                    .get("lat").toString();

                            String lng = ((JSONArray) jsonObject.get("results"))
                                    .getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location")
                                    .get("lng").toString();


                            final LatLng locationOrder = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            mMap.addMarker(new MarkerOptions().position(locationOrder).title("Order Destination: " + Common.currentUser.getPhone()));

                            shippiOrder.child(Common.currentKey)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ShippingInformation shippingInformation = dataSnapshot.getValue(ShippingInformation.class);
                                            assert shippingInformation != null;
                                            LatLng shipperLocation = new LatLng(shippingInformation.getLat(), shippingInformation.getLng());

                                            if (marker == null) {
                                                marker = mMap.addMarker(new MarkerOptions().position(shipperLocation)
                                                        .title("Shipper # :" + shippingInformation.getOrderId())
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                                            } else {
                                                marker.setPosition(shipperLocation);

                                            }

                                            //update Camera

                                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                                    .zoom(16)
                                                    .bearing(0)
                                                    .target(shipperLocation)
                                                    .tilt(45)
                                                    .build();
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                            //draw routes
                                            if (polyline != null)
                                                polyline.remove();
                                            try {
                                                url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + shipperLocation.latitude + "," + shipperLocation.longitude + "&destination=" + locationOrder.latitude + "," + locationOrder.longitude + "&key=" + key + "";
                                                Log.e("URL", url);

                                                mServices.ObtenerRuta(url).enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                                                        new ParserTask().execute(response.body());
                                                    }

                                                    @Override
                                                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                                                    }
                                                });

                                            } catch (Exception ignored) {

                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });




                        } catch (Exception ignored) {

                        }


                    }


                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                    }
                });

            }else
            {
                if (currenOrder.getLatLong()!=null && !currenOrder.getLatLong().isEmpty())
                {
                        String[] LatLng= currenOrder.getLatLong().split(",");
                        final LatLng orderLocation= new LatLng(Double.parseDouble(LatLng[0]), Double.parseDouble(LatLng[1]));

                        mMap.addMarker(new MarkerOptions().position(orderLocation).title("Order Destination: " + Common.currentUser.getPhone()));

                    shippiOrder.child(Common.currentKey)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ShippingInformation shippingInformation = dataSnapshot.getValue(ShippingInformation.class);
                                    assert shippingInformation != null;
                                    LatLng shipperLocation = new LatLng(shippingInformation.getLat(), shippingInformation.getLng());

                                    if (marker == null) {
                                        marker = mMap.addMarker(new MarkerOptions().position(shipperLocation)
                                                .title("Shipper # :" + shippingInformation.getOrderId())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


                                    } else {
                                        marker.setPosition(shipperLocation);

                                    }

                                    //update Camera

                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .zoom(16)
                                            .bearing(0)
                                            .target(shipperLocation)
                                            .tilt(45)
                                            .build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                    //draw routes
                                    if (polyline != null)
                                        polyline.remove();
                                    try {
                                            url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + shipperLocation.latitude + "," + shipperLocation.longitude + "&destination=" + orderLocation.latitude + "," + orderLocation.longitude + "&key=" + key + "";
                                            Log.e("URL", url);

                                            mServices.ObtenerRuta(url).enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                                                    new ParserTask().execute(response.body());
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                                                }
                                            });

                                        } catch (Exception ignored) {

                                        }

                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
         mMap.getUiSettings().setZoomControlsEnabled(true);

       TrackingLocation();

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        TrackingLocation();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @SuppressLint("StaticFieldLeak")
    public class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String,String>>>> {

        ProgressDialog mDialog= new ProgressDialog(TrackingOrderActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please waiting...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> routes= null;

            try {

                jsonObject= new JSONObject(strings[0]);

                DirectionJSONParser jsonParser= new DirectionJSONParser();

                try {
                    routes = jsonParser.parse(jsonObject);
                }catch (Exception ignored)
                {

                }

            } catch (JSONException e) {
                Log.e("Error doInBackground",e.toString());
            }
            return routes;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList<LatLng> points ;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble((Objects.requireNonNull(point.get("lat"))));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polyline == null)
                polyline = mMap.addPolyline(polylineOptions);

            else {
                polyline.remove();
                polyline = mMap.addPolyline(polylineOptions);
            }

        }
    }

    @Override
    protected void onStop() {
        shippiOrder.removeEventListener(this);
        super.onStop();
    }
}
