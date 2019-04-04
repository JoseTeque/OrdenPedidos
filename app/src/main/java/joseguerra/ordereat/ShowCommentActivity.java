package joseguerra.ordereat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.modelo.Rating;
import joseguerra.ordereat.viewHolder.ShowCommentViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowCommentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference referenceRating;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;
    FirebaseRecyclerOptions<Rating> options;

    String foodId= "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //note: add this code before setContentview method

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_show_comment);

        //Firebase

        database= FirebaseDatabase.getInstance();
        referenceRating= database.getReference("Rating");

        //Init view
        recyclerView= findViewById(R.id.IdRecyclerComment);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Init SwipRefresh

        swipeRefreshLayout= findViewById(R.id.IdSwipeRefres);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() !=null)
                    foodId= getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if (!foodId.isEmpty()) {
                    //create request query
                    Query query = referenceRating.orderByChild("foodId").equalTo(foodId);

                    options= new FirebaseRecyclerOptions.Builder<Rating>().setQuery(query,Rating.class).build();

                    adapter= new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.txtPhone.setText(model.getUserPhone());
                            holder.txtComment.setText(model.getComment());
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));

                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_layout,viewGroup,false);

                            return new ShowCommentViewHolder(view);
                        }
                    };
                    
                    loadComment(foodId);
                }
            }
        });
             //thread to load comment on first launch

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                if (getIntent() !=null)
                    foodId= getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if (!foodId.isEmpty()) {
                    //create request query
                    Query query = referenceRating.orderByChild("foodId").equalTo(foodId);

                    options = new FirebaseRecyclerOptions.Builder<Rating>().setQuery(query, Rating.class).build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                            holder.txtPhone.setText(model.getUserPhone());
                            holder.txtComment.setText(model.getComment());
                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));

                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_layout, viewGroup, false);

                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(foodId);
                }
            }
        });

    }

    private void loadComment(String foodId) {

        adapter.startListening();

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
