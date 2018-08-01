package help.helpfit.Model;

public class Rating {
    private String Comment, timeStamp;
    private Long Rating;

    public Rating() {
    }

    public Rating(String comment, Long rating, String timeStamp) {
        Comment = comment;
        this.timeStamp = timeStamp;
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getRating() {
        return Rating;
    }

    public void setRating(Long rating) {
        Rating = rating;
    }
}
