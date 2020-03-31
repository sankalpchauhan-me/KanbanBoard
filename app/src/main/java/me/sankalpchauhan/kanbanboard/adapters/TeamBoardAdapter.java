package me.sankalpchauhan.kanbanboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.TeamBoard;

public class TeamBoardAdapter extends FirestoreRecyclerAdapter<TeamBoard, TeamBoardAdapter.TeamBoardHolder> {
    private OnItemClickListener listener;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TeamBoardAdapter(@NonNull FirestoreRecyclerOptions<TeamBoard> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TeamBoardAdapter.TeamBoardHolder teamBoardHolder, int i, @NonNull TeamBoard teamBoard) {
        Map<String, Object> map = teamBoard.getEngagedUsers();
            if(!map.containsKey(firebaseAuth.getCurrentUser().getUid())){
                teamBoardHolder.itemView.setVisibility(View.GONE);
                teamBoardHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                teamBoardHolder.mBoardTitle.setText(teamBoard.getTitle());
            }
    }

    @NonNull
    @Override
    public TeamBoardAdapter.TeamBoardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_layout, parent, false);
        return new TeamBoardHolder(v);
    }

    class TeamBoardHolder extends RecyclerView.ViewHolder{
        TextView mBoardTitle;

        public TeamBoardHolder(@NonNull View itemView) {
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
