package help.helpfit.Fragment.FragmentSession;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import help.helpfit.Common.Common;
import help.helpfit.Interfaces.SessionClickListener;
import help.helpfit.Model.Rating;
import help.helpfit.Model.TrainingSession;
import help.helpfit.R;
import help.helpfit.ViewHolder.SessionViewHolder;
import help.helpfit.ViewRatingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView createdRec;

    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference userSesssion;
    private DatabaseReference users;
    private FirebaseAuth auth;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerOptions<TrainingSession> options;
    private FirebaseRecyclerAdapter adapter;


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        createdRec = view.findViewById(R.id.rec_history_session);

        //firebase
        database = FirebaseDatabase.getInstance();
        session = database.getReference("Session");
        userSesssion = database.getReference("UserSession");
        users = database.getReference("Users");
        auth = FirebaseAuth.getInstance();

        //Load all session
        loadSession();

        return view;
    }

    private void loadSession() {

        Query query = userSesssion.child(auth.getCurrentUser().getUid().toString()).child("ListSession");

        options = new FirebaseRecyclerOptions.Builder<TrainingSession>()
                .setIndexedQuery(query,session,TrainingSession.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TrainingSession, SessionViewHolder>(options) {


            @NonNull
            @Override
            public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.session_list,parent,false);
                return new SessionViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final SessionViewHolder holder, final int position, @NonNull final TrainingSession model) {
                if(model.getStatus().equalsIgnoreCase("Open")){holder.hide();}
                if(model.getStatus().equalsIgnoreCase("OnGoing")){holder.hide();}
                holder.TheTitle.setText(model.getTitle());
                //holder.SessionTitle.setText(model.getTitle());
                holder.SessionDate.setText(model.getDate());
                holder.SessionTime.setText(model.getTime());
                holder.SessionFee.setText("$"+model.getFee().toString());
                holder.SessionStatus.setText(model.getStatus());
                holder.SessionType.setText(model.getType());

                holder.editBtn.setVisibility(View.GONE);
                holder.deleteBtn.setVisibility(View.GONE);


                if(Common.currentUser.getType().equalsIgnoreCase("Member")){
                    holder.editBtn.setVisibility(View.GONE);
                    holder.giveRating.setVisibility(View.VISIBLE);
                }


                session.child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(model.getType().toString().equalsIgnoreCase("Personal")){
                            holder.SessionNote.setText(dataSnapshot.child("note").getValue().toString());
                            holder.SessionGrup.setVisibility(View.GONE);
                        }
                        else{
                            holder.SessionClassType.setText(dataSnapshot.child("classType").getValue().toString());
                            holder.SessionMaxParticipants.setText(dataSnapshot.child("participant").getValue().toString());
                            holder.SessionPersonal.setVisibility(View.GONE);
                        }

                        if(dataSnapshot.child("ParticipantList")
                                .child(auth.getCurrentUser().getUid().toString()).child("rating").exists()){
                            holder.giveRating.setVisibility(View.GONE);
                        }

                        DatabaseReference rf = dataSnapshot.child("ParticipantList").getRef();

                        rf.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    //stats.put(childSnapshot.getKey(), childSnapshot.getValue().toString());
                                    if(childSnapshot.child("rating").exists()&&(Common.currentUser.getType().equalsIgnoreCase("Trainer"))){
                                        holder.viewRatingSessionBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                session.child(adapter.getRef(position).getKey()).child("ParticipantList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("rating").exists() && Common.currentUser.getType().equalsIgnoreCase("Trainer")){
                            holder.viewRatingSessionBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                holder.giveRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        View rating_layout = inflater.inflate(R.layout.rating,null);

                        final RatingBar Rb = rating_layout.findViewById(R.id.ratingBar);
                        final EditText comment = rating_layout.findViewById(R.id.Comment);

                        dialog.setView(rating_layout);

                        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                                String time = simpleDateFormat.format(calendar.getTime());

                                Rating theRating = new Rating(comment.getText().toString(),(long)Rb.getRating(), time);

                                session.child(adapter.getRef(position).getKey()).child("ParticipantList").child(auth.getCurrentUser().getUid()).setValue(theRating);

                                session.child(adapter.getRef(position).getKey()).child("TrainerId").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot TrainerId) {
                                        userSesssion.child(TrainerId.getValue().toString()).child("AvrgRating").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int currentRating = Math.round(Rb.getRating());
                                                if(dataSnapshot.exists()){
                                                    int oldRating = Integer.parseInt(dataSnapshot.getValue().toString());
                                                    userSesssion.child(TrainerId.getValue().toString()).child("AvrgRating").setValue((oldRating+currentRating)/2);
                                                }
                                                else {
                                                    userSesssion.child(TrainerId.getValue().toString()).child("AvrgRating").setValue(currentRating);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                dialog.dismiss();
                            }
                        });

                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                holder.viewRatingSessionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ViewRatingActivity.class);
                        intent.putExtra("Sessionkey",adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

                holder.setSessionClickListener(new SessionClickListener() {
                    boolean visible;
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        visible = !visible;
                        holder.LLSessionMain.setVisibility(visible? View.VISIBLE:View.GONE);
                    }
                });
            }
        };

        createdRec.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        createdRec.setLayoutManager(layoutManager);
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


