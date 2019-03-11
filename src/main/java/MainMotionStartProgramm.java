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

    }



}
