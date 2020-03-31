package me.sankalpchauhan.kanbanboard.util;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

public class FirestoreReorderableItemTouchHelperCallback<T extends PositionAwareDocument> extends ItemTouchHelper.SimpleCallback {
    private final Activity mContext;
    private final IgnoreChangesFirestoreRecyclerAdapter<T, ?> mRecyclerAdapter;
    private final CollectionReference mCollectionReference;
    private String type;
    private int dragFrom = -1;
    private int dragTo = -1;

    public FirestoreReorderableItemTouchHelperCallback(
            Activity context,
            IgnoreChangesFirestoreRecyclerAdapter<T, ?> recyclerAdapter,
            CollectionReference collectionReference) {
        super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        mContext = context;
        mRecyclerAdapter = recyclerAdapter;
        mCollectionReference = collectionReference;
    }

//    public FirestoreReorderableItemTouchHelperCallback(
//            Activity context,
//            IgnoreChangesFirestoreRecyclerAdapter<T, ?> recyclerAdapter,
//            CollectionReference collectionReference, String type) {
//        super( ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
//        mContext = context;
//        mRecyclerAdapter = recyclerAdapter;
//        mCollectionReference = collectionReference;
//        this.type = type;
//    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (dragFrom == -1) {
            dragFrom = source.getAdapterPosition();
        }
        dragTo = target.getAdapterPosition();
        mRecyclerAdapter.notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (dragFrom == -1) {
            return;
        }
        final PositionAwareDocument draggedItem = mRecyclerAdapter.getItem(dragFrom);
        final PositionAwareDocument draggedToItem = mRecyclerAdapter.getItem(dragTo);
        final String draggedId = mRecyclerAdapter.getSnapshots().getSnapshot(dragFrom).getId();
        final int itemCount = mRecyclerAdapter.getItemCount();

        mRecyclerAdapter.setIgnoreChanges(true);

        if (dragTo == itemCount - 1) {
            draggedItem.setPosition(draggedToItem.getPosition() + 100);
        } else if (dragTo == 0) {
            draggedItem.setPosition(draggedToItem.getPosition() / 2);
        } else {
            PositionAwareDocument draggedToNext = mRecyclerAdapter.getItem(dragTo > dragFrom ? dragTo + 1 : dragTo - 1);
            draggedItem.setPosition((draggedToItem.getPosition() + draggedToNext.getPosition()) / 2);
        }

        mCollectionReference.document(draggedId).update("position", draggedItem.getPosition()).addOnCompleteListener(mContext, task -> mRecyclerAdapter.setIgnoreChanges(false));
        dragFrom = dragTo = -1;
    }




    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

}
