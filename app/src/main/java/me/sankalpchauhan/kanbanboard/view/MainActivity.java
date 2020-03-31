package me.sankalpchauhan.kanbanboard.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.adapters.PersonalBoardAdapter;
import me.sankalpchauhan.kanbanboard.adapters.TeamBoardAdapter;
import me.sankalpchauhan.kanbanboard.fragments.BoardCreateBottomSheet;
import me.sankalpchauhan.kanbanboard.model.Board;
import me.sankalpchauhan.kanbanboard.model.TeamBoard;
import me.sankalpchauhan.kanbanboard.model.User;
import me.sankalpchauhan.kanbanboard.util.Constants;
import me.sankalpchauhan.kanbanboard.viewmodel.MainActivityViewModel;

import static me.sankalpchauhan.kanbanboard.util.Constants.USER;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    Toolbar toolbar;
    String appVersionName;
    int appVersionCode;
    FloatingActionButton boardFAB;
    MainActivityViewModel mainActivityViewModel;
    private PersonalBoardAdapter adapter;
    private TeamBoardAdapter teamBoardAdapter;
    RecyclerView rvPersonal, rvTeam;
    ConstraintLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Boards");
        setSupportActionBar(toolbar);
        initMainActivityViewModel();
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            appVersionName = pInfo.versionName;
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                // avoid huge version numbers for this to work
                appVersionCode = (int) pInfo.getLongVersionCode();
            } else {
                appVersionCode = pInfo.versionCode;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        boardFAB = findViewById(R.id.board_add_FAB);
        rvPersonal = findViewById(R.id.rv_personal_board);
        rvTeam = findViewById(R.id.rv_team_board);
        parent = findViewById(R.id.parent);
        if(!firebaseAuth.getCurrentUser().isEmailVerified()){
            setSnackBar(parent, "Email is not verified");
        }
        initDrawer();
        initGoogleSignInClient();

        boardFAB.setOnClickListener(view -> {
            BoardCreateBottomSheet boardCreateBottomSheet = new BoardCreateBottomSheet();
            boardCreateBottomSheet.show(getSupportFragmentManager(), "boardcreatebottomsheet");
        });
        setUpPersonalRecyclerView(rvPersonal);
        setUpTeamRecyclerView(rvTeam);
    }

    private void initDrawer(){
        String email, name;
        if (isAuthenticated() != null) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String[] nameemail = email.split("@");
            name = nameemail[0];
        } else {
            email = "Log In/Sign Up";
            name = "User";
        }

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null) {
            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Glide.with(getApplicationContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imageView);
                }
            });
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionFirstLine(name)
                .withSelectionSecondLine(email)
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
                        new PrimaryDrawerItem().withIdentifier(1).withName("Sign Out"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Build Version: " + appVersionName).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                signOut();
                                return false;
                        }
                        return true;
                    }
                })
                .build();
    }

    private User getUserFromIntent() {
        return (User) getIntent().getSerializableExtra(USER);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }
    private void initMainActivityViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            goToHomePageActivity();
        }
    }

    private void goToHomePageActivity() {
        finishAffinity();
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
    }

    private void signOut() {
        singOutFirebase();
        signOutGoogle();
    }

    private void singOutFirebase() {
        firebaseAuth.signOut();
    }

    private void signOutGoogle() {
        googleSignInClient.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
        adapter.startListening();
        teamBoardAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
        adapter.startListening();
        teamBoardAdapter.stopListening();
    }

    public static FirebaseUser isAuthenticated(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

    public void addBoardToDB(String boardTitle, String boardType){
        mainActivityViewModel.createBoard(this, firebaseAuth.getCurrentUser().getUid(), boardTitle, boardType);
        mainActivityViewModel.boardLiveData.observe(this, newBoard->{
            Log.d(Constants.TAG, "Board Add Success");
        });
    }

    public void addTeamBoardToDB(String boardTitle, String boardType){
        mainActivityViewModel.createTeamBoard(this, boardTitle, boardType);
        mainActivityViewModel.teamBoardLiveData.observe(this, newTeamBoard->{
            Log.d(Constants.TAG, "Team Board Add Success");
        });
    }

    public void setUpPersonalRecyclerView(RecyclerView recyclerView){
        FirestoreRecyclerOptions<Board> options = new FirestoreRecyclerOptions.Builder<Board>()
                .setQuery(mainActivityViewModel.getQuery(firebaseAuth.getCurrentUser().getUid()), Board.class)
                .build();

        adapter = new PersonalBoardAdapter(options);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListner(new PersonalBoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent boardDetailPage = new Intent(MainActivity.this, BoardActivity.class);
                Bundle b = new Bundle();
                String id = documentSnapshot.getId();
                Board board = documentSnapshot.toObject(Board.class);
                Log.e(Constants.TAG, id+" "+ board.getTitle());
                b.putString("BoardId", id);
                b.putSerializable("Board", board);
                boardDetailPage.putExtras(b);
                startActivity(boardDetailPage);
            }
        });
    }

    public void setUpTeamRecyclerView(RecyclerView recyclerView){
        FirestoreRecyclerOptions<TeamBoard> options = new FirestoreRecyclerOptions.Builder<TeamBoard>()
                .setQuery(mainActivityViewModel.getTeamQuery(), TeamBoard.class)
                .build();

        teamBoardAdapter = new TeamBoardAdapter(options);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(teamBoardAdapter);

        teamBoardAdapter.setOnItemClickListner(new TeamBoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent boardDetailPage = new Intent(MainActivity.this, BoardActivity.class);
                Bundle b = new Bundle();
                String id = documentSnapshot.getId();
                TeamBoard teamBoard = documentSnapshot.toObject(TeamBoard.class);
                Log.e(Constants.TAG, id+" "+ teamBoard.getTitle());
                b.putString("BoardId", id);
                b.putSerializable("TeamBoard", teamBoard);
                boardDetailPage.putExtras(b);
                startActivity(boardDetailPage);
            }
        });
    }

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_LONG);
        snackbar.setAction("Resend Link?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
            }
        });
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }
}
