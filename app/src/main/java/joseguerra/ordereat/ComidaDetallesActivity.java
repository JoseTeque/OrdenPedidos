package joseguerra.ordereat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import info.hoang8f.widget.FButton;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.database.Database;
import joseguerra.ordereat.modelo.Food;
import joseguerra.ordereat.modelo.Order;
import joseguerra.ordereat.modelo.Rating;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ComidaDetallesActivity extends AppCompatActivity implements RatingDialogListener {

    TextView nombreComida, DescripcionComida,PrecioComida;
    ImageView imageViewDetalle;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton elegantNumberButton;
    FloatingActionButton  floatingButton_Rating;
    CounterFab floatingActionButton;
    RatingBar ratingBar;
    FButton btnComment;

    private FirebaseDatabase databaseDetail;
    private DatabaseReference categoryDetail;
    private DatabaseReference ratingReference;

    Food currentFood;

    String foodDetail="";

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

        setContentView(R.layout.activity_comida_detalles);

        //Firebase

        databaseDetail= FirebaseDatabase.getInstance();
        categoryDetail= databaseDetail.getReference("Restaurantes").child(Common.restaurantSelectId).child("Detalles").child("Foods");
        ratingReference= databaseDetail.getReference("Rating");

        //init view
        elegantNumberButton= findViewById(R.id.Id_Number_button);
        nombreComida= findViewById(R.id.Id_Comida_Detalle);
        DescripcionComida= findViewById(R.id.Id_txt_Description_comida);
        PrecioComida= findViewById(R.id.Id_Comida_precio);
        imageViewDetalle= findViewById(R.id.Id_ImageDetalle);
        collapsingToolbarLayout= findViewById(R.id.Id_CollapsinToolbar);
        floatingActionButton= findViewById(R.id.Id_FloatingButton);
        floatingButton_Rating= findViewById(R.id.Id_FloatingButton_Rating);
        ratingBar= findViewById(R.id.IdRating);
        btnComment= findViewById(R.id.IdBtnComment);

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ComidaDetallesActivity.this,ShowCommentActivity.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodDetail);
                startActivity(intent);
            }
        });

        floatingButton_Rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addCart(new Order(
                        Common.currentUser.getPhone(),
                      foodDetail,
                      currentFood.getName(),
                      elegantNumberButton.getNumber(),
                        currentFood.getPrice(),
                       currentFood.getDiscount(),currentFood.getImage()

                ));

                Toast.makeText(ComidaDetallesActivity.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                floatingActionButton.setCount(new Database(ComidaDetallesActivity.this).getCounter(Common.currentUser.getPhone()));
            }
        });

        floatingActionButton.setCount(new Database(this).getCounter(Common.currentUser.getPhone()));


        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandeAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        //get food id from intent

        if (getIntent()!= null)
            foodDetail= getIntent().getStringExtra("Foods");
        if (!foodDetail.isEmpty()){

            if (Common.isConectInter(getBaseContext())) {
                getDetailFoof(foodDetail);
                getRatingFood(foodDetail);
            }
            else
            {
                Toast.makeText(this, "Check la conection a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getRatingFood(String foodDetail) {

        Query foodRating= ratingReference.orderByChild("foodId").equalTo(foodDetail);
        foodRating.addValueEventListener(new ValueEventListener() {

            int sum=0, acount=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                     Rating item= postSnapshot.getValue(Rating.class);
                    assert item != null;
                    sum+= Integer.parseInt(item.getRateValue());
                     acount++;
                }
                if (acount!=0) {
                    float average = sum/acount;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very bad","Not Good","Quite Ok","Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback ")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ComidaDetallesActivity.this)
                .show();

    }



    private void getDetailFoof(String foodDetail) {

        categoryDetail.child(foodDetail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               currentFood = dataSnapshot.getValue(Food.class);

                // set Image

                Picasso.get().load(currentFood.getImage()).into(imageViewDetalle);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                PrecioComida.setText(currentFood.getPrice());
                DescripcionComida.setText(currentFood.getDescription());

                nombreComida.setText(currentFood.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {

        //get rating and upload to firebase

        final Rating rating= new Rating(Common.currentUser.getPhone(),foodDetail, String.valueOf(value),comments);

        // FIX USER RATING MULTIPLE TIME
        ratingReference.push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ComidaDetallesActivity.this, "thank you for submit rating..!!", Toast.LENGTH_SHORT).show();
            }
        });


    /*  ratingReference.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
              {
                  //Remove old value(you can delete or let it be - useless function )
                  ratingReference.child(Common.currentUser.getPhone()).removeValue();
                  //update new value

                  ratingReference.child(Common.currentUser.getPhone()).setValue(rating);
              }else
              {
                  //add new value
                  ratingReference.child(Common.currentUser.getPhone()).setValue(rating);
              }

              Toast.makeText(ComidaDetallesActivity.this, "thank you for submit rating..!!", Toast.LENGTH_SHORT).show();

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      }); */

    }


}
