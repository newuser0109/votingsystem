import java.time.LocalTime;

/**
 * This is a motion class, which is what senators/vp will vote for. Motion has a constructor which will open the motion, motion name
 * and start time.
 * It contains motion status attributes: startTIme, endTime, yesVotes, noVotes, status, open/closed,motionName.
 */

public class Motion {

    private boolean motionOpen;
    private String motionName;
    private LocalTime motionStartTime;
    private LocalTime motionEndTime;
    private int yesVotes;
    private int noVotes;
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


    public String currentMotionStatus(Motion motion){
        return motion.currentMotionStatus;
    }

    @Override
    public String toString() {
        return "Motion{" +
            "motionOpen=" + motionOpen +
            ", motionName='" + motionName + '\'' +
            ", motionStartTime=" + motionStartTime +
            ", motionEndTime=" + motionEndTime +
            ", yesVotes=" + yesVotes +
            ", noVotes=" + noVotes +
            ", currentMotionStatus='" + currentMotionStatus + '\'' +
            '}';
    }
}
