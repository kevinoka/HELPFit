package help.helpfit.Model;

public class TrainingSession {
    private String title, date, time, status, type;
    private Long fee;

    public TrainingSession() {
    }

    public TrainingSession(String title, String date, String time, String status, Long fee, String type) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.status = status;
        this.fee = fee;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
