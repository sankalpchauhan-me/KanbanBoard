package me.sankalpchauhan.kanbanboard.adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.sankalpchauhan.kanbanboard.KanbanApp;
import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.Card;
import me.sankalpchauhan.kanbanboard.util.IgnoreChangesFirestoreRecyclerAdapter;

import static me.sankalpchauhan.kanbanboard.KanbanApp.getCrashlytics;

public class CardAdapter extends IgnoreChangesFirestoreRecyclerAdapter<Card, CardAdapter.CardHolder> {
    private OnItemClickListener listener;

    public CardAdapter(@NonNull FirestoreRecyclerOptions<Card> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, int position, @NonNull Card card) {
        cardHolder.titleTV.setText(card.getTitle());
        if(card.getAttachment()!=null){
            cardHolder.attachmentTAG.setVisibility(View.VISIBLE);
            Glide.with(cardHolder.attachmentIMG.getContext()).load(card.getAttachment()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    cardHolder.attachmentIMG.setVisibility(View.VISIBLE);
                    return false;
                }
            }).thumbnail(0.1f).into(cardHolder.attachmentIMG);
            cardHolder.attachmentIMG.setVisibility(View.VISIBLE);
        }
        if(card.getDueDate()!=null){
            Date duedate = card.getDueDate();
            Date currDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(duedate);
            cardHolder.dueDate.setText(formattedDate);
            cardHolder.dueDate.setVisibility(View.VISIBLE);
            if(currDate.after(duedate)){
                cardHolder.dueDate.setBackgroundColor(cardHolder.dueDate.getContext().getResources().getColor(R.color.urgent));

            }
        }
    }

    @NonNull
    @Override
    public CardAdapter.CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardHolder(v);
    }

    public class CardHolder extends RecyclerView.ViewHolder{
        ImageView attachmentIMG;
        TextView titleTV;
        TextView dueDate;
        ImageView attachmentTAG;

        public CardHolder(@NonNull View itemView) {
            super(itemView);
            attachmentIMG = itemView.findViewById(R.id.attachment_photo);
            titleTV = itemView.findViewById(R.id.title);
            dueDate = itemView.findViewById(R.id.duedate);
            attachmentTAG = itemView.findViewById(R.id.attachment_tag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        return;
                    }
                    int position = getAdapterPosition();
                    Log.e("Test", "Position: "+ getAdapterPosition());
                    if (listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListner(OnItemClickListener listner) {
        this.listener = listner;
    }
}
