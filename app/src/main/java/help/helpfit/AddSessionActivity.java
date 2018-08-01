package help.helpfit;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import help.helpfit.Model.Group;
import help.helpfit.Model.Personal;
import help.helpfit.Model.TrainingSession;

public class AddSessionActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private TextView SessionTitle, SessionDate, SessionTime, SessionFee,
                     SessionNote, SessionClassType, SessionParticipant;
    private RadioButton RBPersonal, RBGroup;
    private Button createBtn;

    //firebase variable
    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference userSession;
    private TrainingSession trainingSession;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        SessionTitle = findViewById(R.id.Name_Session);
        SessionDate = findViewById(R.id.Date_Session);
        SessionTime = findViewById(R.id.Time_Session);
        SessionFee = findViewById(R.id.Fee_Session);
        SessionNote = findViewById(R.id.Note_Session);
        SessionClassType = findViewById(R.id.ClassType_Session);
        SessionParticipant = findViewById(R.id.Participant_Session);

        RBPersonal = findViewById(R.id.RBPersonal);
        RBGroup = findViewById(R.id.RBGroup);

        createBtn = findViewById(R.id.Create_Btn);

        //init firebase
        database = FirebaseDatabase.getInstance();
        session = database.getReference("Session");
        userSession = database.getReference("UserSession");
        auth = FirebaseAuth.getInstance();

        RBPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionNote.setVisibility(View.VISIBLE);

                SessionClassType.setVisibility(View.GONE);
                SessionParticipant.setVisibility(View.GONE);
            }
        });

        RBGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionNote.setVisibility(View.GONE);

                SessionClassType.setVisibility(View.VISIBLE);
                SessionParticipant.setVisibility(View.VISIBLE);
            }
        });

        SessionClassType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence classType[] = new CharSequence[] {"Dance", "MMA", "Sport"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddSessionActivity.this);
                builder.setTitle("Pick Your Class Type");
                builder.setItems(classType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SessionClassType.setText(classType[which].toString());
                    }
                });
                builder.show();
            }
        });

        SessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddSessionActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(),"Datepickerdialog");
            }
        });

        SessionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddSessionActivity.this
                        ,now.get(Calendar.HOUR_OF_DAY)
                        ,now.get(Calendar.MINUTE)
                        ,true);
                timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
                timePickerDialog.show(getFragmentManager(),"TimePicker");
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();
            }
        });
    }

    private void saveToDB() {
        if(RBPersonal.isChecked()){
            trainingSession = new Personal(SessionTitle.getText().toString(),
                    SessionDate.getText().toString(),SessionTime.getText().toString(),"Open",
                    Long.parseLong(SessionFee.getText().toString()),"Personal", SessionNote.getText().toString());

        }
        else if(RBGroup.isChecked()){
            trainingSession = new Group(SessionTitle.getText().toString(),
                    SessionDate.getText().toString(),SessionTime.getText().toString(),"Open",
                    Long.parseLong(SessionFee.getText().toString()),"Group",
                    SessionClassType.getText().toString(),
                    Long.parseLong(SessionParticipant.getText().toString()));
        }
        String sessionKey = session.push().getKey();
        session.child(sessionKey).setValue(trainingSession);
        session.child(sessionKey).child("TrainerId").setValue(auth.getCurrentUser().getUid());

        userSession.child(auth.getCurrentUser().getUid().toString()).child("ListSession").child(sessionKey).setValue("Open");

        Toast.makeText(AddSessionActivity.this,"Session Created!"
                ,Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%02d/%02d/%02d",dayOfMonth,(monthOfYear+1),year%100);
        SessionDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = String.format("%02d.%02d",hourOfDay,minute);
        SessionTime.setText(time);
    }

}
