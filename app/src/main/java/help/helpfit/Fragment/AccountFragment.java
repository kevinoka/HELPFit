package help.helpfit.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import help.helpfit.Common.Common;
import help.helpfit.LoginActivity;
import help.helpfit.R;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    TextView user_name, email, ovrlTxt, userInfo, userInfo2;
    Button logout;
    RatingBar ratingBar;

    private FirebaseDatabase database;
    private DatabaseReference userSession;
    private FirebaseAuth auth;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Inflate the layout for this fragment
        user_name = view.findViewById(R.id.User_name_comment);
        email = view.findViewById(R.id.User_email);
        ovrlTxt = view.findViewById(R.id.ovrlTxt);
        logout = view.findViewById(R.id.Log_out_btn);
        ratingBar = view.findViewById(R.id.User_rating);
        userInfo = view.findViewById(R.id.UserInfo);
        userInfo2 = view.findViewById(R.id.UserInfo2);

        //init firebase
        database = FirebaseDatabase.getInstance();
        userSession = database.getReference("UserSession");
        auth = FirebaseAuth.getInstance();

        if(Common.currentUser.getType().equalsIgnoreCase("Member")){
            ratingBar.setVisibility(View.GONE);
            ovrlTxt.setVisibility(View.GONE);
            userInfo2.setText("Member");
        }

        userSession.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(Common.currentUser.getType().equalsIgnoreCase("Trainer")){
                    userInfo2.setText("Trainer");
                    if(dataSnapshot.child("AvrgRating").exists()){
                        ratingBar.setRating(Float.parseFloat(dataSnapshot.child("AvrgRating").getValue().toString()));
                    }

                    userInfo.setText(Common.currentUser.getSpeciality());
                }
                else if(Common.currentUser.getType().equalsIgnoreCase("Member")) {
                    userInfo.setText(Common.currentUser.getLevel());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        user_name.setText(Common.currentUser.getFirstName()+" "+Common.currentUser.getLastName());
        email.setText(Common.currentUser.getEmailAddress());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.init(getActivity());
                Paper.book().destroy();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Are you sure to logout ?");
                alertDialog.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        return view;
    }

}
