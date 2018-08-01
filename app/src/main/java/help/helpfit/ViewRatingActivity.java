package help.helpfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import help.helpfit.Interfaces.SessionClickListener;
import help.helpfit.Model.Rating;
import help.helpfit.ViewHolder.SessionViewHolder;

public class ViewRatingActivity extends AppCompatActivity {

    private RecyclerView listRatingRec;

    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference users;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerOptions<Rating> options;
    private FirebaseRecyclerAdapter adapter;

    private String SessionKey;
    private TextView ReviewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rating);

        Bundle extra = getIntent().getExtras();
        SessionKey = extra.getString("Sessionkey");

        listRatingRec = findViewById(R.id.rec_list_review);

        //firebase
        database = FirebaseDatabase.getInstance();
        session = database.getReference("Session");
        users = database.getReference("Users");

        ReviewTitle = findViewById(R.id.review_title);

        session.child(SessionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReviewTitle.setText("Review for "+dataSnapshot.child("title").getValue()+" Session");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        loadReview();
    }

    private void loadReview() {
        Query query = session.child(SessionKey).child("ParticipantList");

        options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query,Rating.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Rating, SessionViewHolder>(options) {

            @NonNull
            @Override
            public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rating_list,parent,false);

                return new SessionViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final SessionViewHolder holder, int position, @NonNull Rating model) {

                holder.comment_given.setText(model.getComment());
                holder.date_comment.setText(model.getTimeStamp());
                holder.rating_comment.setRating(model.getRating());

                users.child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.Username.setText(dataSnapshot.child("firstName").getValue().toString()+dataSnapshot.child("lastName").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.setSessionClickListener(new SessionClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                    }
                });
            }

        };

        listRatingRec.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getBaseContext());
        listRatingRec.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
