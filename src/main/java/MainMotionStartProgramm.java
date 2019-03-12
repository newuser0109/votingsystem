import java.time.LocalTime;

/**
 * This is a main program that will start motion voting.
 *
 */

public class MainMotionStartProgramm {

    public static void main(String[] args) throws InterruptedException {

        MotionVoting motionVoting = new MotionVoting();
        Motion motion = new Motion(true,"Motion1", LocalTime.now());
        System.out.println("motion name: " + motion.getMotionName()
                + " and startTime: " + motion.getMotionStartTime()
                + " and IsMotionOpen: " + motion.isMotionOpen());

        motionVoting.addVote(VoteEnum.NO, new Senator(123), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motionVoting.addVote(VoteEnum.NO, new Senator(456), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motionVoting.addVote(VoteEnum.YES, new Senator(111), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motionVoting.addVote(VoteEnum.YES, new Senator(4564), motion);
        System.out.println(motion.currentMotionStatus(motion));


        motion.getVoteMap().forEach((k,v)->System.out.println("Voter Id:" + k + " Vote: " + v));


    }



}
