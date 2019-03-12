import java.util.Date;

/**
 * This is a main program that will start motion voting.
 *
 */

public class MainMotionStartProgramm {

    public static void main(String[] args) {


        Motion motion = new Motion(true,"Motion1", new Date());
        System.out.println("motion name: " + motion.getMotionName()
                + " and startTime: " + motion.getMotionStartTime()
                + " and IsMotionOpen: " + motion.isMotionOpen());

        motion.addVote(VoteEnum.NO, new Senator(123));
        System.out.println(motion.currentMotionStatus(motion));
        motion.addVote(VoteEnum.NO, new Senator(456));
        System.out.println(motion.currentMotionStatus(motion));
        motion.addVote(VoteEnum.YES, new Senator(111));
        System.out.println(motion.currentMotionStatus(motion));
        motion.addVote(VoteEnum.YES, new Senator(4564));
        System.out.println(motion.currentMotionStatus(motion));


        motion.getVoteMap().forEach((k,v)->System.out.println("Voter Id:" + k + " Vote: " + v));

    }



}
