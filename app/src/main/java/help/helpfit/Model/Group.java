package help.helpfit.Model;

public class Group extends TrainingSession {

    private String classType;
    private Long participant;

    public Group() {
    }

    public Group(String title, String date, String time, String status, Long fee, String type, String classType, Long participant) {
        super(title, date, time, status, fee, type);
        this.classType = classType;
        this.participant = participant;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Long getParticipant() {
        return participant;
    }

    public void setParticipant(Long participant) {
        this.participant = participant;
    }
}
