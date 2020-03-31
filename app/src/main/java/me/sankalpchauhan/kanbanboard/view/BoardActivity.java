package me.sankalpchauhan.kanbanboard.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.adapters.BoardListAdapter;
import me.sankalpchauhan.kanbanboard.adapters.CardAdapter;
import me.sankalpchauhan.kanbanboard.fragments.GenericBottomSheet;
import me.sankalpchauhan.kanbanboard.fragments.ListCreateBottomSheet;
import me.sankalpchauhan.kanbanboard.fragments.MemberSearchBottomSheet;
import me.sankalpchauhan.kanbanboard.model.Board;
import me.sankalpchauhan.kanbanboard.model.BoardList;
import me.sankalpchauhan.kanbanboard.model.Card;
import me.sankalpchauhan.kanbanboard.model.TeamBoard;
import me.sankalpchauhan.kanbanboard.util.Constants;
import me.sankalpchauhan.kanbanboard.util.FirestoreReorderableItemTouchHelperCallback;
import me.sankalpchauhan.kanbanboard.viewmodel.BoardActivityViewModel;

import static me.sankalpchauhan.kanbanboard.util.Constants.BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.BOARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.CARD_LIST;
import static me.sankalpchauhan.kanbanboard.util.Constants.PERSONAL_BOARDS;
import static me.sankalpchauhan.kanbanboard.util.Constants.USERS;

public class BoardActivity extends AppCompatActivity {
    Toolbar toolbar;
    String id;
    Board board;
    TeamBoard teamBoard;
    BoardActivityViewModel boardActivityViewModel;
    FloatingActionButton listCreateFAB, memberAddFAB;
    BoardListAdapter boardListAdapter;
    ItemTouchHelper boardTouchHelper;
    RecyclerView rvList;
    Map<String, Object> updatedMap = new HashMap<>();
    Map<String, Object> engagedUsers = new HashMap<>();
    Map<String, Object> map1 = new HashMap<>();

    //Remove these from here after testing
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference database = rootRef.collection(USERS);
    private CollectionReference databaseBoard = rootRef.collection(BOARDS);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        getIntentData();
        toolbar = findViewById(R.id.toolbar);
        if(teamBoard!=null) {
            toolbar.setTitle(teamBoard.getTitle());
            engagedUsers = teamBoard.getEngagedUsers();
        } else {
            toolbar.setTitle(board.getTitle());
        }
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        initDrawer();
        listCreateFAB = findViewById(R.id.list_add_fab);
        rvList = findViewById(R.id.rv_list_item);
        memberAddFAB = findViewById(R.id.member_add_fab);
        setUpRecyclerView(rvList);
        initBoardActivityViewModel();
        if(teamBoard!=null){
            memberAddFAB.show();
        }
        listCreateFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListCreateBottomSheet listCreateBottomSheet = new ListCreateBottomSheet();
                listCreateBottomSheet.show(getSupportFragmentManager(), "listcreatebottomsheet");
            }
        });
        memberAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberSearchBottomSheet memberSearchBottomSheet = new MemberSearchBottomSheet();
                memberSearchBottomSheet.show(getSupportFragmentManager(), "membersearchbottomsheet");
            }
        });
    }

    public void getIntentData(){
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("BoardId");
        board = (Board) bundle.getSerializable("Board");
        teamBoard = (TeamBoard) bundle.getSerializable("TeamBoard");
    }

    private void initBoardActivityViewModel() {
        boardActivityViewModel = new ViewModelProvider(this).get(BoardActivityViewModel.class);
    }

    public void addListToDB(String title){
        if(teamBoard==null) {
            boardActivityViewModel.createList(this, id, title);
        }else {
            boardActivityViewModel.createTeamList(this, id, title);
        }
    }


    public void setUpRecyclerView(RecyclerView recyclerView){
        final CollectionReference boardListCollection;
        if(teamBoard==null) {
             boardListCollection= database.document(firebaseAuth.getCurrentUser().getUid()).collection(PERSONAL_BOARDS).document(id).collection(BOARD_LIST);
        } else {
            boardListCollection = databaseBoard.document(id).collection(BOARD_LIST);
        }
        Log.e(Constants.TAG, id);
        FirestoreRecyclerOptions<BoardList> boardOptions = new FirestoreRecyclerOptions.Builder<BoardList>()
                .setQuery(boardListCollection.orderBy("position"), BoardList.class)
                .build();
        boardListAdapter = new BoardListAdapter(boardOptions, this);
        recyclerView.setHasFixedSize(false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setAdapter(boardListAdapter = new BoardListAdapter(boardOptions, this){
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                //setupEmptyView(mExperienceList, mExperienceEmpty, getItemCount());
            }
        });
        boardTouchHelper = new ItemTouchHelper(new FirestoreReorderableItemTouchHelperCallback<>(this, boardListAdapter, boardListCollection));
        boardTouchHelper.attachToRecyclerView(recyclerView);

        boardListAdapter.setOnItemClickListner(new BoardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent i = new Intent(BoardActivity.this, CardActivity.class);
                String listid = documentSnapshot.getId();
                BoardList boardList = documentSnapshot.toObject(BoardList.class);
                Bundle b = new Bundle();
                b.putString("listId", listid);
                b.putString("boardId", id);
                b.putSerializable("BoardList", boardList);
                if(isTeam()){
                    b.putString("teamBoard", "yes");
                }
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public void addMember(String userId, String email){
        engagedUsers.put(userId, email);
        updatedMap.put("engagedUsers", engagedUsers);
        boardActivityViewModel.addTeamMember(this, id, updatedMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boardListAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //boardListAdapter.startAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boardListAdapter.stopListening();
       // boardListAdapter.stopAdapter();
    }

    public String getBoardId(){
        return id;
    }

    public boolean isTeam(){
        if(teamBoard!=null){
            return true;
        }
        return false;
    }

    public void manualLifeCycleControl(){
        super.onPause();
        super.onStop();
    }

    private void initDrawer(){
        if(teamBoard!=null) {

            String email, name;
            email = teamBoard.getCreatedByUserEmail();
            String[] nameemail = email.split("@");
            name = nameemail[0];
            map1 = teamBoard.getEngagedUsers();
            String firstLine;
            if(map1.size()==1){
                firstLine = "No Team Members";
            } else {
                firstLine = "Members: "+map1.size();
            }

            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withSelectionFirstLine(firstLine)
                    .withSelectionSecondLine("Creator: "+name)
                    .withHeaderBackground(R.color.colorPrimary)
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();

            Drawer drawer = new DrawerBuilder()
                    .withAccountHeader(headerResult)
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withIdentifier(1).withName("Show Members"),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName("Build Version: " ).withSelectable(false)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            switch ((int) drawerItem.getIdentifier()) {
                                case 1:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("memberMap", (Serializable) map1);
                                    GenericBottomSheet fragInfo = new GenericBottomSheet();
                                    fragInfo.setArguments(bundle);
                                    fragInfo.show(getSupportFragmentManager(), "generic");
                                    return false;
                            }
                            return true;
                        }
                    })
                    .build();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.archive, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.archiveItem:
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}
