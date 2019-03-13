/**
 * This is an abstract VoterType class which contains basic VoterId method.
 *
 */

public class VoterType {


    private int voterId;

    public VoterType(int voterId) {
        this.voterId = voterId;
    }

    public int getVoterId() {
        return voterId;
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    private void vote (Motion motion){

    }



}
