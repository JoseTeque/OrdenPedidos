package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.interfaz.InterfaceClickListener;
import joseguerra.ordereat.modelo.Request;
import joseguerra.ordereat.viewHolder.OrderViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView_status;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerOptions<Request> adapterOptions;
    private FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

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
        setContentView(R.layout.activity_order_status);

        //Firebase

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("Requests");

        recyclerView_status= findViewById(R.id.Id_RecyclerStatus);
        recyclerView_status.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView_status.setLayoutManager(layoutManager);

        //if we start OrdenStatus activity from home actvity
        //we will not put any extras, so we just load order by phone from common

        if(getIntent() == null){
            loadOrder(Common.currentUser.getPhone());
        }else{
            if (getIntent().getStringExtra("userPhone")==null)
                loadOrder(Common.currentUser.getPhone());
            else
                loadOrder(getIntent().getStringExtra("userPhone"));
        }

    }

    private void loadOrder(String phone) {

        adapterOptions= new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(databaseReference.orderByChild("phone").equalTo(phone), Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(adapterOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Request model) {

                holder.OrderName.setText(adapter.getRef(position).getKey());
                holder.OrderPhone.setText(model.getPhone());
                holder.OrderAddress.setText(model.getAddress());
                holder.OrderStautus.setText(Common.converCodeToStatus(model.getStatus()));
                holder.setClickListener(new InterfaceClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean inlongclick) {
                         Common.currentKey= adapter.getRef(position).getKey();

                         startActivity(new Intent(OrderStatusActivity.this,TrackingOrderActivity.class));
                    }
                });

                holder.IconDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatusActivity.this, "No se puede remover la order..", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_layout,viewGroup,false);
                return new OrderViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView_status.setAdapter(adapter);

    }

    private void deleteOrder(final String key) {

        databaseReference.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderStatusActivity.this, "Order :" +
                                key +
                                "Se elimino de la base..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderStatusActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

