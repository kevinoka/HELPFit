package help.helpfit.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import help.helpfit.AddSessionActivity;
import help.helpfit.Common.Common;
import help.helpfit.Interfaces.SessionClickListener;
import help.helpfit.Model.Group;
import help.helpfit.Model.Personal;
import help.helpfit.Model.TrainingSession;
import help.helpfit.R;
import help.helpfit.ViewHolder.SessionViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //firebase variable
    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference userSession;
    private RecyclerView sessionRec;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerOptions<TrainingSession> options;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseAuth auth;

    private FloatingActionButton floatBtn;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //firebase
        database = FirebaseDatabase.getInstance();
        session = database.getReference("Session");
        userSession = database.getReference("UserSession");
        auth = FirebaseAuth.getInstance();

        floatBtn = view.findViewById(R.id.floatingActionButton);
        sessionRec = view.findViewById(R.id.rec_MainSession);


        //Load all session
        loadSession();

        if(Common.currentUser.getType().equalsIgnoreCase("Member")){
            floatBtn.setVisibility(View.GONE);
        }

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddSessionActivity.class));
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void loadSession() {

        options = new FirebaseRecyclerOptions.Builder<TrainingSession>()
                .setQuery(session,TrainingSession.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TrainingSession, SessionViewHolder>(options) {


            @NonNull
            @Override
            public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.session_item,parent,false);
                return new SessionViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final SessionViewHolder holder, final int position, @NonNull final TrainingSession model) {
                //if(model.getStatus().equalsIgnoreCase("Finish")){holder.hide();}
                holder.SessionTitle.setText(model.getTitle());
                holder.SessionDate.setText(model.getDate());
                holder.SessionTime.setText(model.getTime());
                holder.SessionFee.setText("$ "+model.getFee().toString());
                holder.SessionStatus.setText(model.getStatus());
                holder.SessionType.setText(model.getType());

                if(Common.currentUser.getType().equalsIgnoreCase("Trainer")){
                    holder.SessionJoinButton.setVisibility(View.GONE);
                }

                if(model.getType().toString().equalsIgnoreCase("Personal")){
                    holder.SessionGrup.setVisibility(View.GONE);

                }
                else {
                    holder.SessionPersonal.setVisibility(View.GONE);
                }

                session.child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(model.getType().toString().equalsIgnoreCase("Personal")){
                            Personal Thesession = dataSnapshot.getValue(Personal.class);
                            holder.SessionNote.setText(Thesession.getNote());
                                Long numJS = dataSnapshot.child("ParticipantList").getChildrenCount();
                                if(numJS>=1){
                                    holder.SessionJoinButton.setVisibility(View.GONE);
                                    holder.FullTextView.setVisibility(View.VISIBLE);
                                }

                        }
                        else {
                            Group Thesession = dataSnapshot.getValue(Group.class);
                            holder.SessionClassType.setText(Thesession.getClassType());
                            holder.SessionMaxParticipants.setText(Thesession.getParticipant().toString());
                                Long numJS = dataSnapshot.child("ParticipantList").getChildrenCount();
                                if(numJS>=Long.parseLong(dataSnapshot.child("participant").getValue().toString())){
                                    holder.SessionJoinButton.setVisibility(View.GONE);
                                    holder.FullTextView.setVisibility(View.VISIBLE);
                                }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.SessionJoinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure join "+model.getTitle()+" Session?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                session.child(adapter.getRef(position).getKey()).child("ParticipantList").child(auth.getCurrentUser().getUid()).setValue("join");
                                userSession.child(auth.getCurrentUser().getUid()).child("ListSession").child(adapter.getRef(position).getKey()).setValue("True");

                                Toast.makeText(getActivity(),"Join a Session !", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                adapter.notifyItemChanged(position);
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                holder.setSessionClickListener(new SessionClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                    }
                });
            }
        };

        sessionRec.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        sessionRec.setLayoutManager(layoutManager);
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
