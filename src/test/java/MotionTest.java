import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Random;

public class MotionTest {

    //This is to test that Voting is not allowed if Motion is not open.
    @Test
    public void test_motion_not_allowed_to_vote_if_closed() {
        Motion motion = new Motion();
        Assert.assertFalse(motion.startMotionVote(VoteEnum.YES, new Senator(123)));
    }

    //This is to test if motion can be close before 15 minutes
    @Test
    public void test_if_motion_closed_before_15_min() throws InterruptedException {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Thread.sleep(2);
        Assert.assertTrue(motion.closeMotionVote(motion));
    }

    //This is to test voter already voted - NO
    @Test
    public void test_if_voter_voted_in_motion_NO() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Assert.assertFalse(motion.checkIfAlreadyVoted(23));
    }

    //This is to test voter already voted - YES
    @Test
    public void test_if_voter_voted_in_motion_YES() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        motion.addVote(VoteEnum.NO, new Senator(23));
        Assert.assertTrue(motion.checkIfAlreadyVoted(23));
    }


    //This is to test if vote count is less than 101
    @Test
    public void test_if_motion_vote_count_less_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        motion.addVote(VoteEnum.NO, new Senator(23));
        Assert.assertTrue(motion.checkIfVoteCount101());
    }

    //This is to test if vote count is more than 101
    @Test
    public void test_if_motion_vote_count_more_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 102;
        for(int i = 0 ; i < maxVote; i++) {
            motion.addVote(VoteEnum.NO, new Senator(random.nextInt()));
        }
        Assert.assertFalse(motion.checkIfVoteCount101());
    }
}
