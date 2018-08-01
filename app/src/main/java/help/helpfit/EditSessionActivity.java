package help.helpfit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import help.helpfit.Model.TrainingSession;

public class EditSessionActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private TextView editSessionTitle, editSessionDate, editSessionTime, editSessionFee,
            editSessionNote, editSessionClassType, editSessionParticipant;
    private RadioButton editRBPersonal, editRBGroup;
    private Button editBtn;
    private String SessionKey;

    private TextInputLayout classTypeLayout,participantLayout, noteLayout;

    //firebase variable
    private FirebaseDatabase database;
    private DatabaseReference session;
    private DatabaseReference userSession;
    private TrainingSession trainingSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);

        //Intent extra = new Intent();
        Bundle extra = getIntent().getExtras();
        SessionKey = extra.getString("Sessionkey");

        editSessionTitle = findViewById(R.id.Edit_Name_Session);
        editSessionDate = findViewById(R.id.Edit_Date_Session);
        editSessionTime = findViewById(R.id.Edit_Time_Session);
        editSessionFee = findViewById(R.id.Edit_Fee_Session);
        editSessionNote = findViewById(R.id.Edit_Note_Session);
        editSessionClassType = findViewById(R.id.Edit_ClassType_Session);
        editSessionParticipant = findViewById(R.id.Edit_Participant_Session);

        editRBPersonal = findViewById(R.id.Edit_RBPersonal);
        editRBGroup = findViewById(R.id.Edit_RBGroup);

        editBtn = findViewById(R.id.Edit_Btn);

        //Text Layout
        classTypeLayout = findViewById(R.id.layoutClassType);
        participantLayout = findViewById(R.id.layoutParticipant);
        noteLayout = findViewById(R.id.layoutNote);

        //init firebase
        database = FirebaseDatabase.getInstance();
        session = database.getReference("Session");
        userSession = database.getReference("UserSession");

        session.child(SessionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editSessionTitle.setText(dataSnapshot.child("title").getValue().toString());
                editSessionDate.setText(dataSnapshot.child("date").getValue().toString());
                editSessionTime.setText(dataSnapshot.child("time").getValue().toString());
                editSessionFee.setText(dataSnapshot.child("fee").getValue().toString());
                if(dataSnapshot.child("type").getValue().toString().equalsIgnoreCase("Personal")){
                    editRBPersonal.setChecked(true);
                    editSessionNote.setText(dataSnapshot.child("note").getValue().toString());
                    editSessionNote.setVisibility(View.VISIBLE);
                }
                else if(dataSnapshot.child("type").getValue().toString().equalsIgnoreCase("Group")){
                    editRBGroup.setChecked(true);
                    editSessionClassType.setText(dataSnapshot.child("classType").getValue().toString());
                    editSessionParticipant.setText(dataSnapshot.child("participant").getValue().toString());
                    editSessionClassType.setVisibility(View.VISIBLE);
                    editSessionParticipant.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editRBPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteLayout.setVisibility(View.VISIBLE);
                editSessionNote.setVisibility(View.VISIBLE);

                classTypeLayout.setVisibility(View.GONE);
                participantLayout.setVisibility(View.GONE);
            }
        });

        editRBGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteLayout.setVisibility(View.GONE);

                classTypeLayout.setVisibility(View.VISIBLE);
                participantLayout.setVisibility(View.VISIBLE);
                editSessionClassType.setVisibility(View.VISIBLE);
                editSessionParticipant.setVisibility(View.VISIBLE);
            }
        });

        editSessionClassType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence classType[] = new CharSequence[] {"Dance", "MMA", "Sport"};

                AlertDialog.Builder builder = new AlertDialog.Builder(EditSessionActivity.this);
                builder.setTitle("Pick Your Class Type");
                builder.setItems(classType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editSessionClassType.setText(classType[which].toString());
                    }
                });
                builder.show();
            }
        });

        editSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditSessionActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(),"Datepickerdialog");

            }
        });

        editSessionTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        EditSessionActivity.this
                        ,now.get(Calendar.HOUR_OF_DAY)
                        ,now.get(Calendar.MINUTE)
                        ,true
                );
                timePickerDialog.show(getFragmentManager(),"TimePicker");
            }
        });
        
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSession();
            }
        });
    }

    private void editSession() {
        session.child(SessionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("title").setValue(editSessionTitle.getText().toString());
                dataSnapshot.getRef().child("date").setValue(editSessionDate.getText().toString());
                dataSnapshot.getRef().child("time").setValue(editSessionTime.getText().toString());
                dataSnapshot.getRef().child("fee").setValue(Long.parseLong(editSessionFee.getText().toString()));
                if(editRBPersonal.isChecked()){
                    dataSnapshot.getRef().child("type").setValue("Personal");
                    dataSnapshot.getRef().child("note").setValue(editSessionNote.getText().toString());
                }
                else if(editRBGroup.isChecked()){
                    dataSnapshot.getRef().child("type").setValue("Group");
                    dataSnapshot.getRef().child("classType").setValue(editSessionClassType.getText().toString());
                    dataSnapshot.getRef().child("participant").setValue(Long.parseLong(editSessionParticipant.getText().toString()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toast.makeText(EditSessionActivity.this,"Successfully edit a Session", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%02d/%02d/%02d",dayOfMonth,(monthOfYear+1),year%100);
        editSessionDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = String.format("%02d.%02d",hourOfDay,minute);
        editSessionTime.setText(time);
    }

}
