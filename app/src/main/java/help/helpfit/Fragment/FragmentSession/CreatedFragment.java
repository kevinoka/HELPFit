package help.helpfit.Fragment.FragmentSession;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import help.helpfit.Common.Common;
import help.helpfit.EditSessionActivity;
import help.helpfit.Interfaces.SessionClickListener;
import help.helpfit.Model.TrainingSession;
import help.helpfit.Model.User;
import help.helpfit.R;
import help.helpfit.ViewHolder.SessionViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatedFragment extends Fragment {

    private RecyclerView createdRec;

    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference userSesssion;
    private DatabaseReference users;
    private FirebaseAuth auth;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerOptions<TrainingSession> options;
    FirebaseRecyclerAdapter adapter;


    public CreatedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created, container, false);
        // Inflate the layout for this fragment
        createdRec = view.findViewById(R.id.rec_created_session);

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
        //Query query = userSesssion.child(auth.getCurrentUser().getUid().toString()).child("ListSession").orderByValue().equalTo("Open");
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
                if(model.getStatus().equalsIgnoreCase("Finish")){holder.hide();}
                if(model.getStatus().equalsIgnoreCase("OnGoing")){holder.hide();}
                holder.TheTitle.setText(model.getTitle());
                //holder.SessionTitle.setText(model.getTitle());
                holder.SessionDate.setText(model.getDate());
                holder.SessionTime.setText(model.getTime());
                holder.SessionFee.setText("$ "+model.getFee().toString());
                holder.SessionStatus.setText(model.getStatus());
                holder.SessionType.setText(model.getType());

                users.child(auth.getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user.getType().equalsIgnoreCase("Member")){
                            holder.editBtn.setVisibility(View.GONE);
                        }
                        else if(user.getType().equalsIgnoreCase("Trainer")){
                            holder.startSessionBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.startSessionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        session.child(adapter.getRef(position).getKey()).child("status").setValue("OnGoing");
                    }
                });

                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent edit = new Intent(getContext(), EditSessionActivity.class);
                        edit.putExtra("Sessionkey",adapter.getRef(position).getKey());
                        startActivity(edit);
                    }
                });

                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                adapter.getRef(position).child("Participant").child(auth.getCurrentUser().getUid()).removeValue();
                                userSesssion.child(auth.getCurrentUser().getUid()).child("ListSession")
                                        .child(adapter.getRef(position).getKey()).removeValue();
                                if(Common.currentUser.getType().equalsIgnoreCase("Trainer")){
                                    adapter.getRef(position).removeValue();
                                }

                                builder.setMessage("Session Deleted");
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
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
