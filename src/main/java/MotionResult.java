/**
 * This is a Motion Results class.
 *
 */

public class MotionResult {

    private int yesVotes;
    private int noVotes;


    public int getYesVotes() {
        return yesVotes;
    }

    public void setYesVotes(int yesVotes) {
        this.yesVotes = yesVotes;
    }

    public int getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(int noVotes) {
        this.noVotes = noVotes;
    }

    @Override
    public String toString() {
        return "MotionResult{" +
            "yesVotes=" + yesVotes +
            ", noVotes=" + noVotes +
            '}';
    }
}
