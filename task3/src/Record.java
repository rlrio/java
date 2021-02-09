import java.util.Date;

public class Record {
    private Date date;
    private String user;
    private boolean toppedUp;
    private int volume;
    private boolean success;
    private int beforeVolume;
    private int afterVolume;

    public Record() { }

    public Date getDate() {
        return date;
    }
    public String getUser() {
        return user;
    }
    public boolean isToppedUp() {
        return toppedUp;
    }
    public int getVolume() {
        return volume;
    }
    public boolean isSuccess() { return success; }

    public int getBeforeVolume() {
        return beforeVolume;
    }

    public int getAfterVolume() {
        return afterVolume;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setToppedUp(boolean toppedUp) {
        this.toppedUp = toppedUp;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setSuccess(boolean success) { this.success = success; }

    public void setAfterVolume(int afterVolume) {
        this.afterVolume = afterVolume;
    }

    public void setBeforeVolume(int beforeVolume) {
        this.beforeVolume = beforeVolume;
    }
}
