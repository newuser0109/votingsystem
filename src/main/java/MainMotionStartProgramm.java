import java.time.LocalTime;
import java.util.Random;

/**
 * This is a main program that will start motion voting.
 *
 */

public class MainMotionStartProgramm {

    public static void main(String[] args) throws InterruptedException {

        // This is just running some different types of motion to show end to end.

        /**
         *  This motion should Pass.
         */
        Motion motion = new Motion(true, "MotionPassed", LocalTime.now());
        Random random = new Random();
        int maxYesVote = 60;
        int maxNoVote = 40;

        MotionVoting motionVoting = generateRandomVotes(motion, random, maxYesVote, maxNoVote);
        //Take Status
        System.out.println(motion.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVoting.calculateResultAndClose(motion);
        //Take Status
        System.out.println(motion.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVoting.vpVoteIfTied(motion, VoteEnum.YES, new VP(99), true);
        //Take Status
        System.out.println(motion.getCurrentMotionStatus());
        //Print result if motion closed.
        motionVoting.printResultIfClosed(motion);

        /**
         *  This motion should fail.
         *
         * */
        Motion motionFailed = new Motion(true, "motionFailed", LocalTime.now());
        MotionVoting motionVotingFailed = generateRandomVotes(motionFailed, random, 40, 60);
        //Take Status
        System.out.println(motionFailed.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingFailed.calculateResultAndClose(motionFailed);
        //Take Status
        System.out.println(motionFailed.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingFailed
            .vpVoteIfTied(motionFailed, VoteEnum.YES, new VP(99), true);
        //Take Status
        System.out.println(motionFailed.getCurrentMotionStatus());
        //Print result if motion closed.
        motionVotingFailed.printResultIfClosed(motionFailed);

        /**
         *  This motion should Pass since VP is available to vote with Yes.
         */

        Motion motionTied = new Motion(true, "motionTied", LocalTime.now());
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
        //Take Status
        System.out.println(motionTied.getCurrentMotionStatus());
        //Print result if motion closed.
        motionVotingTied.printResultIfClosed(motionTied);

        /**
         *  This motion should Fail since VP is available to vote with No.
         */

        Motion motionTiedFailed = new Motion(true, "motionTied", LocalTime.now());
        MotionVoting motionVotingTiedFailed = generateRandomVotes(motionTiedFailed, random, 50, 50);
        //Take Status
        System.out.println(motionTiedFailed.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingTiedFailed.calculateResultAndClose(motionTiedFailed);
        //Take Status
        System.out.println(motionTiedFailed.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingTiedFailed
            .vpVoteIfTied(motionTiedFailed, VoteEnum.NO, new VP(99), true);
        //Take Status
        System.out.println(motionTiedFailed.getCurrentMotionStatus());
        //Print result if motion closed.
        motionVotingTiedFailed.printResultIfClosed(motionTiedFailed);

        /**
         *  This motion should Fail since VP is not available.
         */

        Motion motionFailedNoVp = new Motion(true, "motionTied", LocalTime.now());
        MotionVoting motionVotingFailledNoVP = generateRandomVotes(motionFailedNoVp, random, 50,
            50);
        //Take Status
        System.out.println(motionFailedNoVp.getCurrentMotionStatus());
        //Gather Motion Result and Try to close
        motionVotingFailledNoVP.calculateResultAndClose(motionFailedNoVp);
        //Take Status
        System.out.println(motionFailedNoVp.getCurrentMotionStatus());
        //Vp Vote if Tied and is available to vote.
        motionVotingFailledNoVP
            .vpVoteIfTied(motionFailedNoVp, VoteEnum.NO, new VP(99), false);
        //Take Status
        System.out.println(motionFailedNoVp.getCurrentMotionStatus());
        //Print result if motion closed.
        motionVotingFailledNoVP.printResultIfClosed(motionFailedNoVp);


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
