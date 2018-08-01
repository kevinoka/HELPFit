package help.helpfit.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import help.helpfit.Interfaces.SessionClickListener;
import help.helpfit.R;


public class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView SessionTitle, SessionDate, SessionTime, SessionFee, SessionStatus, SessionNote,
                    SessionClassType, SessionMaxParticipants, SessionType, TheTitle, FullTextView;
    public LinearLayout SessionGrup, SessionPersonal, LLSessionMain;
    public Button SessionJoinButton, finishSessionBtn, startSessionBtn, viewRatingSessionBtn, giveRating;

    public ImageButton editBtn,deleteBtn;

    private SessionClickListener sessionClickListener;

    public TextView Username,comment_given,date_comment;
    public RatingBar rating_comment;

    public SessionViewHolder(@NonNull View itemView) {
        super(itemView);
        SessionTitle = itemView.findViewById(R.id.SessionName);
        SessionDate = itemView.findViewById(R.id.SessionDate);
        SessionTime = itemView.findViewById(R.id.SessionTime);
        SessionFee = itemView.findViewById(R.id.SessionFee);
        SessionStatus = itemView.findViewById(R.id.SessionStatus);
        SessionNote = itemView.findViewById(R.id.TheNote);
        SessionClassType = itemView.findViewById(R.id.SessionClassType);
        SessionMaxParticipants = itemView.findViewById(R.id.SessionParticipant);
        SessionType = itemView.findViewById(R.id.SessionType);
        SessionGrup = itemView.findViewById(R.id.LLGroup);
        SessionPersonal = itemView.findViewById(R.id.LLPersonal);
        SessionJoinButton = itemView.findViewById(R.id.SessionJoinBtn);
        editBtn = itemView.findViewById(R.id.editSessionBtn);
        deleteBtn = itemView.findViewById(R.id.deleteSessionBtn);
        finishSessionBtn = itemView.findViewById(R.id.FinishSessionBtn);
        startSessionBtn = itemView.findViewById(R.id.StartSessionBtn);
        viewRatingSessionBtn = itemView.findViewById(R.id.ViewRatingBtn);
        TheTitle = itemView.findViewById(R.id.The_Title);
        LLSessionMain = itemView.findViewById(R.id.LLSessionMain);
        FullTextView = itemView.findViewById(R.id.FullTextView);
        giveRating = itemView.findViewById(R.id.GiveRatingBtn);

        Username = itemView.findViewById(R.id.User_name_comment);
        comment_given = itemView.findViewById(R.id.Comment_list);
        date_comment = itemView.findViewById(R.id.Date_comment);
        rating_comment = itemView.findViewById(R.id.ratingGiven);

        itemView.setOnClickListener(this);
    }

    public void setSessionClickListener(SessionClickListener sessionClickListener){
        this.sessionClickListener = sessionClickListener;
    }

    @Override
    public void onClick(View view) {
        sessionClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void hide(){
        itemView.setVisibility(View.GONE);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
    }
}
