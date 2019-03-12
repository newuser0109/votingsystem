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

    public MotionVoting getMotionVoting() {
        return motionVoting;
    }

    public void setMotionVoting(MotionVoting motionVoting) {
        this.motionVoting = motionVoting;
    }

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


    public String currentMotionStatus(Motion motion){
        return motion.currentMotionStatus;
    }


    public MotionResult startMotionVote(VoteEnum voteEnum, VoterType voterType, Motion motion) {
        MotionResult motionResult = new MotionResult();
        MotionVoting motionVoting = motion.getMotionVoting();
        if (motion.isMotionOpen()) {
            if (motionVoting.checkIfVoteCount101()) {
                System.out.println(
                    "Motion already reached 101 count, closing the motion. and return results.");
                motion.setMotionOpen(false);
                motion.setCurrentMotionStatus(MotionStatusEnum.CLOSED.toString());

                //Return motion result.
            }
        } else {
            System.out.println("You cannot vote, motion is not open.");
        }
        return motionResult;
    }

    public MotionResult createMotionResult(Motion motion) {
        MotionResult motionResult = new MotionResult();
        MotionVoting motionVoting = motion.getMotionVoting();

        return motionResult;
    }
}
