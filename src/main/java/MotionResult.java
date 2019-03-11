import java.util.Date;
import java.util.List;

/**
 * This is a Motion Results class.
 *
 */

public class MotionResult {
    private boolean motionStatus;
    private List<Integer> yesVotes;
    private List<Integer> noVotes;
    private Date motionVotingOpen;
    private Date motionVotingClosed;

    public boolean isMotionStatus() {
        return motionStatus;
    }

    public void setMotionStatus(boolean motionStatus) {
        this.motionStatus = motionStatus;
    }

    public List<Integer> getYesVotes() {
        return yesVotes;
    }

    public void setYesVotes(List<Integer> yesVotes) {
        this.yesVotes = yesVotes;
    }

    public List<Integer> getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(List<Integer> noVotes) {
        this.noVotes = noVotes;
    }

    public Date getMotionVotingOpen() {
        return motionVotingOpen;
    }

    public void setMotionVotingOpen(Date motionVotingOpen) {
        this.motionVotingOpen = motionVotingOpen;
    }

    public Date getMotionVotingClosed() {
        return motionVotingClosed;
    }

    public void setMotionVotingClosed(Date motionVotingClosed) {
        this.motionVotingClosed = motionVotingClosed;
    }
}
