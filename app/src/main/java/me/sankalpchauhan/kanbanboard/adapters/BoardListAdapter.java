package me.sankalpchauhan.kanbanboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.model.BoardList;
import me.sankalpchauhan.kanbanboard.model.Card;
import me.sankalpchauhan.kanbanboard.util.Constants;
import me.sankalpchauhan.kanbanboard.util.FirestoreReorderableItemTouchHelperCallback;
import me.sankalpchauhan.kanbanboard.util.IgnoreChangesFirestoreRecyclerAdapter;
import me.sankalpchauhan.kanbanboard.view.BoardActivity;
import me.sankalpchauhan.kanbanboard.view.CardActivity;

import static me.sankalpchauhan.kanbanboard.util.Constants.BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.CARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.PERSONAL_BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;

public class BoardListAdapter extends IgnoreChangesFirestoreRecyclerAdapter<BoardList, BoardListAdapter.ListViewHolder> {
    private OnItemClickListener listener;
    //Remove these from here after testing
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    private CollectionReference databaseBoard = rootRef.collection(BOARDS);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private RecyclerView.RecycledViewPool recycledViewPool;
    CardAdapter cardAdapter = null;
    private Context mContext;
    ItemTouchHelper cardTouchHelper;
    boolean set = false;

    public BoardListAdapter(@NonNull FirestoreRecyclerOptions<BoardList> options, Context context) {
        super(options);
        this.recycledViewPool= new RecyclerView.RecycledViewPool();
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull BoardListAdapter.ListViewHolder listViewHolder, int position, @NonNull BoardList boardList) {
        listViewHolder.toolbar.setTitle(boardList.getTitle());
        if (mContext instanceof BoardActivity) {
            String boardid = ((BoardActivity) mContext).getBoardId();
            final CollectionReference boardListCollection;
            if(((BoardActivity) mContext).isTeam()){
                boardListCollection = databaseBoard.document(boardid).collection(BOARD_LIST).document(getSnapshots().getSnapshot(position).getId()).collection(CARD_LIST);
            } else {
                boardListCollection = database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(boardid).collection(BOARD_LIST).document(getSnapshots().getSnapshot(position).getId()).collection(CARD_LIST);
            }
            //Log.e(Constants.TAG, id);
            FirestoreRecyclerOptions<Card> cardOptions = new FirestoreRecyclerOptions.Builder<Card>()
                    .setQuery(boardListCollection.orderBy("position"), Card.class)
                    .build();
            cardAdapter = new CardAdapter(cardOptions);
            //bind(cardOptions);
            listViewHolder.cardRv.setHasFixedSize(false);
            listViewHolder.cardRv.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
            listViewHolder.cardRv.setRecycledViewPool(recycledViewPool);
            listViewHolder.cardRv.setAdapter(cardAdapter = new CardAdapter(cardOptions) {
                @Override
                public void onDataChanged() {
                    super.onDataChanged();
                    //setupEmptyView(mExperienceList, mExperienceEmpty, getItemCount());
                }
            });
            BoardActivity boardActivity = (BoardActivity) mContext;
            cardTouchHelper = new ItemTouchHelper(new FirestoreReorderableItemTouchHelperCallback<>(boardActivity, cardAdapter, boardListCollection));
        cardTouchHelper.attachToRecyclerView(listViewHolder.cardRv);
//        if(!set){
//            cardAdapter.startListening();
//        }
            cardAdapter.startListening();
            Log.e("Test", "onBindCalled");
        cardAdapter.setOnItemClickListner(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position2) {
                Intent i = new Intent(mContext, CardActivity.class);
                String listid = documentSnapshot.getId();
                Card c = documentSnapshot.toObject(Card.class);
                Bundle b = new Bundle();
                b.putString("cardId", listid);
                b.putString("boardId", boardid);
                b.putSerializable("BoardList", boardList);
                b.putSerializable("listId", getSnapshots().getSnapshot(listViewHolder.getAdapterPosition()).getId());
                b.putSerializable("card", c);
                if(((BoardActivity) mContext).isTeam()){
                    b.putSerializable("teamBoard", "yes");
                }
                i.putExtras(b);
                mContext.startActivity(i);
                //cardAdapter.stopListening();
            }
        });
        }
    }

    private void bind(FirestoreRecyclerOptions<Card> cardOptions){
        if(cardAdapter!=null){
            cardAdapter.stopListening();
        }
        cardAdapter = new CardAdapter(cardOptions);

    }

    public void startAdapter(){
        if(cardAdapter!=null) {
            set = true;
            Log.e("test", "Started");
            cardAdapter.startListening();
        }
    }

    public void stopAdapter(){
        if(cardAdapter!=null) {
            set = true;
            Log.e("test", "Stopped");
            cardAdapter.startListening();
        }
    }

    @NonNull
    @Override
    public BoardListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item, parent, false);
        return new ListViewHolder(v);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        Button mCreateCardBTN;
        Toolbar toolbar;
        RecyclerView cardRv;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            mCreateCardBTN = itemView.findViewById(R.id.create_card_BTN);
            toolbar = itemView.findViewById(R.id.toolbar);
            cardRv = itemView.findViewById(R.id.card_view_rv);
            mCreateCardBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
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
