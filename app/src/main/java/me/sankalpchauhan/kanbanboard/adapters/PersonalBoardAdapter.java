package me.sankalpchauhan.kanbanboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.Board;

public class PersonalBoardAdapter extends FirestoreRecyclerAdapter<Board, PersonalBoardAdapter.BoardHolder> {

    private OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PersonalBoardAdapter(@NonNull FirestoreRecyclerOptions<Board> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BoardHolder boardHolder, int position, @NonNull Board board) {
        boardHolder.mBoardTitle.setText(board.getTitle());
    }

    @NonNull
    @Override
    public BoardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_layout, parent, false);
        return new BoardHolder(v);
    }

    class BoardHolder extends RecyclerView.ViewHolder{
        TextView mBoardTitle;

        public BoardHolder(@NonNull View itemView) {
            super(itemView);
            mBoardTitle = itemView.findViewById(R.id.board_title);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);

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
