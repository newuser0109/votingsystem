import java.time.LocalTime;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class MotionTest {

    MotionVoting motionVoting = new MotionVoting();
    Random random = new Random();


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
        Senator vote = new Senator(23);
        motionVoting.addSenatorVote(VoteEnum.NO, vote, motion);
        Assert.assertTrue(motionVoting.checkIfAlreadyVoted(vote));

        VP voteVP = new VP(231);
        motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
        motionVoting.addVpVote(VoteEnum.NO, voteVP, motion);
        Assert.assertTrue(motionVoting.checkIfAlreadyVoted(voteVP));
    }


    //This is to test if vote count is less than 101
    @Test
    public void test_if_motion_vote_count_less_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Senator vote = new Senator(98);
        motionVoting.addSenatorVote(VoteEnum.NO, vote, motion);
        Assert.assertFalse(motionVoting.checkIfVoteCount101());
    }

    //This is to test if vote count is more than 101
    @Test
    public void test_if_motion_vote_count_more_than_101() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 101;
        for(int i = 0 ; i < maxVote; i++) {
            motionVoting.addSenatorVote(VoteEnum.NO, new Senator(random.nextInt()), motion);
        }
        Assert.assertFalse(motionVoting.checkIfVoteCount101());
    }

    //This is to test current motion status - Open.
    @Test
    public void test_current_motion_status_Open() {
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 10;
        for(int i = 0 ; i < maxVote; i++) {
            motionVoting.addSenatorVote(VoteEnum.NO, new Senator(random.nextInt()), motion);
        }
        Assert.assertEquals(motion.currentMotionStatus(motion), MotionStatusEnum.OPEN.toString());
    }


    //This is to test if voter type senator reached voting limits. Senator - 100
    @Test
    public void test_if_voter_type_senator_reached_limit() {
        Motion motion = new Motion(true, "Motion1", LocalTime.now());
        Random random = new Random();
        int maxVote = 100;
        Senator senator = null;
        for (int i = 0; i < maxVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        Assert.assertTrue(motionVoting.checkIfVoterTypeReachedLimit(senator));
    }

    //This is to test if voter type VP reached voting limits. VP - 1
    @Test
    public void test_if_voter_type_vp_reached_limit() {
        Motion motion = new Motion(true, "Motion1VP", LocalTime.now());
        Random random = new Random();
        VP vp = new VP(random.nextInt());
        motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
        motionVoting.addVpVote(VoteEnum.NO, vp, motion);

        Assert.assertTrue(motionVoting.checkIfVoterTypeReachedLimit(vp));
    }

    //This is to test  motionresult passed 60/40
    @Test
    public void test_that_motion_is_passed() {
        Motion motion = new Motion(true, "MotionPassed", LocalTime.now());
        Random random = new Random();
        int maxYesVote = 60;
        int maxNoVote = 40;

        MotionVoting motionVoting = motion.getMotionVoting();
        Senator senator = null;
        for (int i = 0; i < maxYesVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.YES, senator, motion);
        }

        for (int i = 0; i < maxNoVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        motionVoting.createMotionResult(motion);
        Assert.assertEquals(MotionStatusEnum.PASSED.toString(), motion.getCurrentMotionStatus());
    }

    //This is to test  motionresult failed 40/60
    @Test
    public void test_that_motion_is_failed() {
        Motion motion = new Motion(true, "MotionFailed", LocalTime.now());
        Random random = new Random();
        int maxYesVote = 40;
        int maxNoVote = 60;

        MotionVoting motionVoting = motion.getMotionVoting();
        Senator senator = null;
        for (int i = 0; i < maxYesVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.YES, senator, motion);
        }

        for (int i = 0; i < maxNoVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        motionVoting.createMotionResult(motion);
        Assert.assertEquals(MotionStatusEnum.FAILED.toString(), motion.getCurrentMotionStatus());
    }

    //This is to test motion close if passed
    @Test
    public void test_close_motion_if_passed() {
        Motion motion = new Motion(true, "MotionPassed", LocalTime.now());
        Random random = new Random();
        int maxYesVote = 60;
        int maxNoVote = 40;

        MotionVoting motionVoting = motion.getMotionVoting();
        Senator senator = null;
        for (int i = 0; i < maxYesVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.YES, senator, motion);
        }

        for (int i = 0; i < maxNoVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        motionVoting.closeMotion(motion);
        Assert.assertTrue(motion.isMotionOpen());
    }

    //This is to test motion not closed if TIED
    @Test
    public void test_motion_open_If_TIED() {
        Motion motion = new Motion(true, "MotionTIED", LocalTime.now());
        Random random = new Random();
        int maxYesVote = 50;
        int maxNoVote = 50;

        MotionVoting motionVoting = motion.getMotionVoting();
        Senator senator = null;
        for (int i = 0; i < maxYesVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.YES, senator, motion);
        }

        for (int i = 0; i < maxNoVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        motionVoting.createMotionResult(motion);
        Assert.assertEquals(MotionStatusEnum.TIED.toString(), motion.getCurrentMotionStatus());

        motionVoting.closeMotion(motion);
        Assert.assertTrue(motion.isMotionOpen());

    }


    // Test if Motion Passes after VP Votes Yes
    @Test
    public void test_tied_Vp_Pass() {
        Motion motionTied = new Motion(true, "motionTiedPass", LocalTime.now());
        MotionVoting motionVotingTied = generateRandomVotes(motionTied, random, 50, 50);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingTied.calculateResultAndClose(motionTied);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingTied
            .vpVoteIfTied(motionTied, VoteEnum.YES, new VP(99), true);

        Assert
            .assertEquals(motionTied.getCurrentMotionStatus(), MotionStatusEnum.PASSED.toString());

    }

    // Test if Motion Fails if VP votes NO
    @Test
    public void test_tied_Vp_Fail() {
        Motion motionTied = new Motion(true, "motionTiedFailedVP", LocalTime.now());
        MotionVoting motionVotingTied = generateRandomVotes(motionTied, random, 50, 50);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingTied.calculateResultAndClose(motionTied);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingTied
            .vpVoteIfTied(motionTied, VoteEnum.NO, new VP(99), true);

        Assert
            .assertEquals(motionTied.getCurrentMotionStatus(), MotionStatusEnum.FAILED.toString());

    }

    // Test if Motion Fails if VP is not available.
    @Test
    public void test_tied_VP_Not_Avail_Failed() {
        Motion motionTied = new Motion(true, "motionTiedFailedVPNotAvail", LocalTime.now());
        MotionVoting motionVotingTied = generateRandomVotes(motionTied, random, 50, 50);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingTied.calculateResultAndClose(motionTied);
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingTied
            .vpVoteIfTied(motionTied, VoteEnum.NO, new VP(99), false);

        Assert
            .assertEquals(motionTied.getCurrentMotionStatus(), MotionStatusEnum.FAILED.toString());

    }

    public static MotionVoting generateRandomVotes(Motion motion, Random random, int maxYesVote,
        int maxNoVote) {
        MotionVoting motionVoting = motion.getMotionVoting();
        Senator senator = null;
        for (int i = 0; i < maxYesVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.YES, senator, motion);
        }

        for (int i = 0; i < maxNoVote; i++) {
            senator = new Senator(random.nextInt());
            motionVoting.addSenatorVote(VoteEnum.NO, senator, motion);
        }
        return motionVoting;
    }
}
