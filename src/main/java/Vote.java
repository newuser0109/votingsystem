
/**
 * This vote class will represent each vote.
 *
 */

public class Vote {

    private String motionName;
    private int voterId;

    public Vote(String motionName, int voterId) {
        this.motionName = motionName;
        this.voterId = voterId;
    }

    public String getMotionName() {
        return motionName;
    }

    public void setMotionName(String motionName) {
        this.motionName = motionName;
    }

    public int getVoterId() {
        return voterId;
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "motionName='" + motionName + '\'' +
                ", voterId=" + voterId +
                '}';
    }
//maybe contain senator in this.
}
