import org.junit.Assert;
import org.junit.Test;

public class MotionTest {

    //This is to test that Voting is not allowed if Motion is not open.
    @Test
    public void test_motion_not_allowed_to_vote_if_closed() {
        Motion motion = new Motion();
        Assert.assertFalse(motion.startVote(VoteEnum.YES, new Senator(123)));
    }

}
