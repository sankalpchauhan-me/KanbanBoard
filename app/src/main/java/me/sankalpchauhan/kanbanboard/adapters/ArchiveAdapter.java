package me.sankalpchauhan.kanbanboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.Card;

public class ArchiveAdapter extends FirestoreRecyclerAdapter<Card, ArchiveAdapter.CardHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ArchiveAdapter(@NonNull FirestoreRecyclerOptions<Card> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, int i, @NonNull Card card) {
        cardHolder.tv.setText(card.getTitle());

    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
        return new CardHolder(v);
    }

    public class CardHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public CardHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.card_title);

        }
    }
}
