import java.time.LocalTime;

/**
 * This is a Motion Results class.
 *
 */

public class MotionResult {

    private String motionStatus;
    private int yesVotes;
    private int noVotes;
    private LocalTime motionVotingOpen;
    private LocalTime motionVotingClosed;


    public String getMotionStatus() {
        return motionStatus;
    }

    public void setMotionStatus(String motionStatus) {
        this.motionStatus = motionStatus;
    }

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

    public LocalTime getMotionVotingOpen() {
        return motionVotingOpen;
    }

    public void setMotionVotingOpen(LocalTime motionVotingOpen) {
        this.motionVotingOpen = motionVotingOpen;
    }

    public LocalTime getMotionVotingClosed() {
        return motionVotingClosed;
    }

    public void setMotionVotingClosed(LocalTime motionVotingClosed) {
        this.motionVotingClosed = motionVotingClosed;
    }
}
