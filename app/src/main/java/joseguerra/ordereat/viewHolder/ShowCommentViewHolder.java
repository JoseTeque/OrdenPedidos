package joseguerra.ordereat.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import joseguerra.ordereat.R;
import joseguerra.ordereat.modelo.Rating;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtPhone,txtComment;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPhone= itemView.findViewById(R.id.IdTxtPhone);
        txtComment= itemView.findViewById(R.id.IdTxtComment);
        ratingBar= itemView.findViewById(R.id.IdRatingBar);
    }
}
