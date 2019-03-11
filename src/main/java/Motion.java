import java.util.Date;

/**
 * This is a motion class, which is what senators will vote for. Motion has a constructor which will open the motion, motion name
 * and start time.
 *
 */

public class Motion {

    private boolean motionOpen;
    private boolean motionClosed;
    private String motionName;
    private Date motionStartTime;
    private Date motionEndTime;
    private MotionResult motionResult;

    public Motion(boolean motionOpen, String motionName, Date motionStartTime) {
        this.motionOpen = motionOpen;
        this.motionName = motionName;
        this.motionStartTime = motionStartTime;
    }

    //Motion needs to take in Votes with Senator Id.

    //Motion needs method to check if voting is allowed.
    //Motion needs method that returns if motion passed.
    //Motion needs method that returns if motion failed.
    //motion needs method to close motion voting.
            //  check if tie, if tie only VP can vote, once VP votes motion can close.
            // VP can only vote on tied state.
            // IF VP not available motion can be forced to closed state which will fail the motion.
    //motion needs method to check if Voter has already voted.
    //motion needs method to allow only 101 votes.
    //motion needs query to determine current state of vote.


    public boolean isMotionOpen() {
        return motionOpen;
    }

    public void setMotionOpen(boolean motionOpen) {
        this.motionOpen = motionOpen;
    }

    public boolean isMotionClosed() {
        return motionClosed;
    }

    public void setMotionClosed(boolean motionClosed) {
        this.motionClosed = motionClosed;
    }

    public String getMotionName() {
        return motionName;
    }

    public void setMotionName(String motionName) {
        this.motionName = motionName;
    }

    public Date getMotionStartTime() {
        return motionStartTime;
    }

    public void setMotionStartTime(Date motionStartTime) {
        this.motionStartTime = motionStartTime;
    }

    public Date getMotionEndTime() {
        return motionEndTime;
    }

    public void setMotionEndTime(Date motionEndTime) {
        this.motionEndTime = motionEndTime;
    }


}
