import java.util.Date;
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
    private Date motionStartTime;
    private Date motionEndTime;
    private MotionResult motionResult;
    private final int voteCount = 3;
    private Map<Integer,String> voteMap = new HashMap<>();
    private String currentMotionStatus;

    public Motion() {
    }

    public Motion(boolean motionOpen, String motionName, Date motionStartTime) {
        this.motionOpen = motionOpen;
        this.motionName = motionName;
        this.motionStartTime = motionStartTime;
        this.currentMotionStatus = MotionStatusEnum.OPEN.toString();
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

    public Map<Integer, String> getVoteMap() {
        return voteMap;
    }

    public boolean startVote(VoteEnum voteEnum, VoterType voterType){
        if(this.isMotionOpen()){
            addVote(voteEnum,voterType);
        }
        return this.isMotionOpen();
    }

    public void addVote(VoteEnum voteEnum, VoterType voterType){
        // check if motion open
        if(this.isMotionOpen()) {
            //check if voted  && 101 count
            if (!this.voteMap.containsKey(voterType.getVoterId()) && this.voteMap.size() < this.voteCount) {
                //Vote vote = new Vote(voteEnum.toString(),voterType.getVoterId());
                this.voteMap.put(voterType.getVoterId(), voteEnum.toString());
                this.currentMotionStatus = MotionStatusEnum.OPEN.toString();

            } else {
                System.out.println("Voter already voted");
                this.currentMotionStatus = MotionStatusEnum.CLOSED.toString();
                this.setMotionEndTime(new Date());
                this.setMotionOpen(false);
            }
        }
        else{
            System.out.println("Motion not open");
        }
    }

    public String currentMotionStatus(Motion motion){
        return motion.currentMotionStatus;
    }

}
