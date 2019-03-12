import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a motion class, which is what senators will vote for. Motion has a constructor which will open the motion, motion name
 * and start time.
 *
 */

public class Motion {

    private boolean motionOpen;
    private String motionName;
    private LocalTime motionStartTime;
    private LocalTime motionEndTime;
    private MotionResult motionResult;
    private final int voteCount = 101;
    private Map<Integer,String> voteMap = new HashMap<>();
    private String currentMotionStatus;

    private static final int FIFTEEN_MINUTES = 15 * 60;
    private MotionVoting motionVoting;


    public Motion() {
    }

    public Motion(boolean motionOpen, String motionName, LocalTime motionStartTime) {
        this.motionOpen = motionOpen;
        this.motionName = motionName;
        this.motionStartTime = motionStartTime;
        this.currentMotionStatus = MotionStatusEnum.OPEN.toString();
        this.motionVoting = new MotionVoting();
    }

    //Motion needs to take in Votes with VoterType.

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


    public String getCurrentMotionStatus() {
        return currentMotionStatus;
    }

    public void setCurrentMotionStatus(String currentMotionStatus) {
        this.currentMotionStatus = currentMotionStatus;
    }

    public boolean isMotionOpen() {
        return motionOpen;
    }

    public void setMotionOpen(boolean motionOpen) {
        this.motionOpen = motionOpen;
    }


    public String getMotionName() {
        return motionName;
    }

    public void setMotionName(String motionName) {
        this.motionName = motionName;
    }

    public LocalTime getMotionStartTime() {
        return motionStartTime;
    }

    public void setMotionStartTime(LocalTime motionStartTime) {
        this.motionStartTime = motionStartTime;
    }

    public LocalTime getMotionEndTime() {
        return motionEndTime;
    }

    public void setMotionEndTime(LocalTime motionEndTime) {
        this.motionEndTime = motionEndTime;
    }

    public Map<Integer, String> getVoteMap() {
        return voteMap;
    }

    public boolean closeMotionVote(Motion motion){
        LocalTime finalTime = LocalTime.now();
        long fifteen_minutes = Duration.between(finalTime, motion.getMotionStartTime()).getSeconds();
        if(
                FIFTEEN_MINUTES == fifteen_minutes){
            motion.setMotionOpen(false);
        }

        return motion.motionOpen;
    }

    public String currentMotionStatus(Motion motion){
        return motion.currentMotionStatus;
    }

}
