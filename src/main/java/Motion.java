import java.time.Duration;
import java.time.LocalTime;
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
    private LocalTime motionStartTime;
    private LocalTime motionEndTime;
    private MotionResult motionResult;
    private final int voteCount = 101;
    private Map<Integer,String> voteMap = new HashMap<>();
    private String currentMotionStatus;

    private static final int FIFTEEN_MINUTES = 15 * 60;


    public Motion() {
    }

    public Motion(boolean motionOpen, String motionName, LocalTime motionStartTime) {
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

    public boolean startMotionVote(VoteEnum voteEnum, VoterType voterType){
        if(this.isMotionOpen()){
            addVote(voteEnum,voterType);
        }
        return this.isMotionOpen();
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

    public boolean checkIfAlreadyVoted(int voterId){
        return this.voteMap.containsKey(voterId);
    }

    public boolean checkIfVoteCount101(){
        return this.voteMap.size() < this.voteCount;
    }

    public void addVote(VoteEnum voteEnum, VoterType voterType){
        // check if motion open
        if(this.isMotionOpen()) {
            //check if voted  && 101 count
            if (!checkIfAlreadyVoted(voterType.getVoterId()) && checkIfVoteCount101()) {
                //Vote vote = new Vote(voteEnum.toString(),voterType.getVoterId());
                this.voteMap.put(voterType.getVoterId(), voteEnum.toString());
                this.currentMotionStatus = MotionStatusEnum.OPEN.toString();

            } else {
                System.out.println("Voter already voted");
                this.currentMotionStatus = MotionStatusEnum.CLOSED.toString();
                this.setMotionEndTime(LocalTime.now());
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
