package help.helpfit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import help.helpfit.Common.Common;
import help.helpfit.Model.User;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {


    private Button btnSignUp, btnSignIn;
    private ConstraintLayout rootLayout;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootLayout = findViewById(R.id.rootLayout);

        //Init PaperDB
        Paper.init(this);

        //init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        String usr = Paper.book().read(Common.user_field);
        String pwd = Paper.book().read(Common.pwd_field);

        if(usr != null && pwd != null)
            if(!TextUtils.isEmpty(usr) && !TextUtils.isEmpty(pwd))
                autoLogin(usr, pwd);

    }

    private void autoLogin(String usr, String pwd) {
        auth.signInWithEmailAndPassword(usr,pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        users.child(auth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                Common.currentUser = user;

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Failed, "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Log In");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View signin_layout = inflater.inflate(R.layout.login,null);

        final EditText editEmail = signin_layout.findViewById(R.id.editEmail);
        final EditText editPassword = signin_layout.findViewById(R.id.editPassword);

        dialog.setView(signin_layout);

        //set button
        dialog.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //check validation
                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter your email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Your password is too short", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                //login
                auth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Paper.book().write(Common.user_field,editEmail.getText().toString());
                                Paper.book().write(Common.pwd_field,editPassword.getText().toString());

                                users.child(auth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        Common.currentUser = user;
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed"+e.getMessage(),Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });


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

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign Up");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View signup_layout = inflater.inflate(R.layout.register,null);

        final EditText editFirstName = signup_layout.findViewById(R.id.editFirstName);
        final EditText editLastName = signup_layout.findViewById(R.id.editLastName);
        final EditText editEmail = signup_layout.findViewById(R.id.editEmail);
        final EditText editPassword = signup_layout.findViewById(R.id.editPassword);
        final EditText editSpeciality = signup_layout.findViewById(R.id.editSpecialty);
        final EditText editLevel = signup_layout.findViewById(R.id.editLevel);

        final RadioButton RBTrainer = signup_layout.findViewById(R.id.RBTrainer);
        final RadioButton RBMember = signup_layout.findViewById(R.id.RBMember);

        final TextInputLayout LSpecilaity = signup_layout.findViewById(R.id.LLSpeciality);
        final TextInputLayout LLevel = signup_layout.findViewById(R.id.LLLevel);

        dialog.setView(signup_layout);

        editLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence level[] = new CharSequence[] {"Beginner", "Intermediate", "Expert"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Pick Your Level");
                builder.setItems(level, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editLevel.setText(level[which].toString());
                    }
                });
                builder.show();
            }
        });

        editSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence speciality[] = new CharSequence[] {"Yoga", "Boxing", "Workout"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Pick Your Speciality");
                builder.setItems(speciality, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editSpeciality.setText(speciality[which].toString());
                    }
                });
                builder.show();
            }
        });


        RBTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSpecilaity.setVisibility(View.VISIBLE);
                editSpeciality.setVisibility(View.VISIBLE);
                editLevel.setVisibility(View.GONE);
                LLevel.setVisibility(View.GONE);
            }
        });
        RBMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSpecilaity.setVisibility(View.GONE);
                LLevel.setVisibility(View.VISIBLE);
                editLevel.setVisibility(View.VISIBLE);
                editSpeciality.setVisibility(View.GONE);
            }
        });

        //set button
        dialog.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String type;

                if(RBTrainer.isChecked()){ type = "Trainer";}
                else { type = "Member";}

                dialogInterface.dismiss();

                //check validation
                if(TextUtils.isEmpty(editFirstName.getText().toString()))
                {
                    Snackbar.make(signup_layout, "Please enter your first name", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editLastName.getText().toString()))
                {
                    Snackbar.make(signup_layout, "Please enter your last name", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editEmail.getText().toString()))
                {
                    Snackbar.make(signup_layout, "Please enter your email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editPassword.getText().toString()))
                {
                    Snackbar.make(signup_layout, "Please enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(editPassword.getText().toString().length() < 6)
                {
                    Snackbar.make(signup_layout, "Your password is too short", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }


                //register/sign up new user
                auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user to db
                                User user = new User();
                                user.setFirstName(editFirstName.getText().toString());
                                user.setLastName(editLastName.getText().toString());
                                user.setEmailAddress(editEmail.getText().toString());
                                user.setPassword(editPassword.getText().toString());
                                user.setType(type);
                                if(TextUtils.isEmpty(editSpeciality.getText().toString())){
                                    user.setSpeciality("None");
                                }else user.setSpeciality(editSpeciality.getText().toString());

                                if(TextUtils.isEmpty(editLevel.getText().toString())){
                                    user.setLevel("None");
                                }else user.setLevel(editLevel.getText().toString());

                                //use email as a key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Sign up successful", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Sign up failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Registration failed."+e.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
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
}
