import java.time.LocalTime;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class MotionTest {

    MotionVoting motionVoting = new MotionVoting();

    //This is to test that Voting is not allowed if Motion is not open.
    @Test
    public void test_motion_not_allowed_to_vote_if_closed() {
        Motion motion = new Motion();
        //  Assert.assertFalse(motionVoting.startMotionVote(VoteEnum.YES, new Senator(123), motion));
    }

    //This is to test if motion cannot be close before 15 minutes
    @Test
    public void test_if_motion_cannot_closed_before_15_min() throws InterruptedException {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Thread.sleep(2);
        Assert.assertTrue(motionVoting.closeMotionVoteBeforeTimeAllowed(motion));
    }

    //This is to test voter already voted - NO
    @Test
    public void test_if_voter_voted_in_motion_NO() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        VoterType vote = new Senator(23);
        Assert.assertFalse(motionVoting.checkIfAlreadyVoted(vote));

        VoterType voteVP = new VP(231);
        Assert.assertFalse(motionVoting.checkIfAlreadyVoted(voteVP));
    }

    //This is to test voter already voted - YES
    @Test
    public void test_if_voter_voted_in_motion_YES() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        VoterType vote = new Senator(23);
        motionVoting.addVote(VoteEnum.NO, vote, motion);
        Assert.assertTrue(motionVoting.checkIfAlreadyVoted(vote));

        VoterType voteVP = new VP(231);
        motionVoting.addVote(VoteEnum.NO, voteVP, motion);
        Assert.assertTrue(motionVoting.checkIfAlreadyVoted(voteVP));
    }


    //This is to test if vote count is less than 101
    @Test
    public void test_if_motion_vote_count_less_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        VoterType vote = new Senator(98);
        motionVoting.addVote(VoteEnum.NO, vote, motion);
        Assert.assertFalse(motionVoting.checkIfVoteCount101());
    }

    //This is to test if vote count is more than 101
    @Test
    public void test_if_motion_vote_count_more_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 102;
        for(int i = 0 ; i < maxVote; i++) {
            motionVoting.addVote(VoteEnum.NO, new Senator(random.nextInt()), motion);
        }
        Assert.assertTrue(motionVoting.checkIfVoteCount101());
    }

    //This is to test current motion status - Open.
    @Test
    public void test_current_motion_status_Open() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 10;
        for(int i = 0 ; i < maxVote; i++) {
            motionVoting.addVote(VoteEnum.NO, new Senator(random.nextInt()), motion);
        }
        Assert.assertEquals(motion.currentMotionStatus(motion), MotionStatusEnum.OPEN.toString());
    }

    //This is to test current motion status - Closed.
    @Test
    public void test_current_motion_status_Closed() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 102;
        for(int i = 0 ; i < maxVote; i++) {
            motionVoting.addVote(VoteEnum.NO, new Senator(random.nextInt()), motion);
        }
        Assert.assertEquals(motion.currentMotionStatus(motion), MotionStatusEnum.CLOSED.toString());
    }

    //This is to test if voter type senator reached voting limits. Senator - 100
    @Test
    public void test_if_voter_type_senator_reached_limit() {
        Motion motion = new Motion(true, "Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 100;
        VoterType senator = null;
        for (int i = 0; i < maxVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addVote(VoteEnum.NO, senator, motion);
        }
        Assert.assertTrue(motionVoting.checkIfVoterTypeReachedLimit(senator));
    }

    //This is to test if voter type VP reached voting limits. VP - 1
    @Test
    public void test_if_voter_type_vp_reached_limit() {
        Motion motion = new Motion(true, "Motion1VP", LocalTime.now());
        Random random = new Random();
        VoterType vp = new VP(random.nextInt());
        motionVoting.addVote(VoteEnum.NO, vp, motion);

        Assert.assertTrue(motionVoting.checkIfVoterTypeReachedLimit(vp));
    }
}
