public class Event {
    private String subscription;
    private String message;

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscString() {
        return subscription;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
