package help.helpfit.Model;

public class Personal extends TrainingSession {

    private String note;

    public Personal() {
    }

    public Personal(String title, String date, String time, String status, Long fee, String type, String note) {
        super(title, date, time, status, fee, type);
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
