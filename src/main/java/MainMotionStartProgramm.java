import java.time.LocalTime;

/**
 * This is a main program that will start motion voting.
 *
 */

public class MainMotionStartProgramm {

    public static void main(String[] args) throws InterruptedException {

        Motion motion = new Motion(true, "Motion", LocalTime.now());

        motion.getMotionVoting().addVote(VoteEnum.NO, new Senator(123), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motion.getMotionVoting().addVote(VoteEnum.NO, new Senator(456), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motion.getMotionVoting().addVote(VoteEnum.YES, new Senator(111), motion);
        System.out.println(motion.currentMotionStatus(motion));
        motion.getMotionVoting().addVote(VoteEnum.YES, new Senator(4564), motion);
        System.out.println(motion.currentMotionStatus(motion));

        Motion motion1 = new Motion(true, "Motion1", LocalTime.now());

        motion1.getMotionVoting().addVote(VoteEnum.NO, new Senator(123), motion1);
        System.out.println(motion1.currentMotionStatus(motion));
        motion1.getMotionVoting().addVote(VoteEnum.NO, new Senator(456), motion1);
        System.out.println(motion1.currentMotionStatus(motion));

        System.out.println("motion name: " + motion.getMotionName()
                + " and startTime: " + motion.getMotionStartTime()
                + " and IsMotionOpen: " + motion.isMotionOpen());

        motion.getVoteMap().forEach((k,v)->System.out.println("Voter Id:" + k + " Vote: " + v));


    }



}
