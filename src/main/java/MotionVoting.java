/*
 * Copyright (c) ${date} Travelport. All rights reserved.
 */

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is a motion voting class that will tally votes based on voterType.
 *
 */

public class MotionVoting {

    private Map<Integer, String> senateVoteMap = new HashMap<>();
    private Map<Integer, String> vpVoteMap = new HashMap<>();
    private static final int FIFTEEN_MINUTES = 15 * 60;


    public Map<Integer, String> getSenateVoteMap() {
        return senateVoteMap;
    }


    public Map<Integer, String> getVpVoteMap() {
        return vpVoteMap;
    }


    /**
     * This method keeps the vote tally.
     * @param voteEnum
     * @param voterType
     */
    public  void  voteTally(VoterType voterType, VoteEnum voteEnum){
        if (!checkIfVoteCount101() && !checkIfAlreadyVoted(voterType)
            && !checkIfVoterTypeReachedLimit(voterType)) {
            if (voterType instanceof Senator) {
                senateVoteMap.put(voterType.getVoterId(), voteEnum.toString());
            }
            if (voterType instanceof VP) {
                vpVoteMap.put(voterType.getVoterId(), voteEnum.toString());
            }
        }
    }

    /**
     * This method checks if voter already voted.
     * @param voterType
     * @return true/false
     */
    public boolean checkIfAlreadyVoted(VoterType voterType){
        boolean alreadyVoted = false;
        if(voterType instanceof  Senator){
            if(senateVoteMap.containsKey(voterType.getVoterId())){
                alreadyVoted = true;
            }
        }
        if(voterType  instanceof  VP){
            if(vpVoteMap.containsKey(voterType.getVoterId())){
                alreadyVoted = true;
            }
        }
        return alreadyVoted;
    }

    /**
     * This method check if vote count is 101 or not.
     *
     * @return true/false
     */
    public boolean checkIfVoteCount101(){
        int totalVoted = this.senateVoteMap.size();
        totalVoted += this.vpVoteMap.size();
        boolean voteCount101= false;
        int totalVote = 101;
        if(totalVoted == totalVote){
            voteCount101 = true;
        }
        return voteCount101;
    }


    /**
     * This method adds all senator votes.
     *
     * @param voteEnum
     * @param senator
     * @param motion
     */
    public void addSenatorVote(VoteEnum voteEnum, Senator senator, Motion motion) {
        voteTally(senator, voteEnum);
        motion.setCurrentMotionStatus(MotionStatusEnum.OPEN.toString());
    }

    /**
     * This method adds VP Vote.
     *
     * @param voteEnum
     * @param vp
     * @param motion
     */
    public void addVpVote(VoteEnum voteEnum, VP vp, Motion motion) {
        if (motion.getCurrentMotionStatus()
            .equals(MotionStatusEnum.TIED.toString())
            && this.vpVoteMap.size() < 1) {
            voteTally(vp, voteEnum);
            motion.setCurrentMotionStatus(MotionStatusEnum.OPEN.toString());
        }
    }

    /**
     * This method checks if voter type such as senator or VP has reached there voting limit.
     *
     * @param voterType
     * @return true/false
     */
    public boolean checkIfVoterTypeReachedLimit(VoterType voterType){
        boolean voterTypeReachedLimit = false;
        if(voterType instanceof  Senator){
            int totalSenateVoteAllowed = 100;
            if(senateVoteMap.size() == totalSenateVoteAllowed){
                voterTypeReachedLimit = true;
            }
        }
        if(voterType  instanceof  VP){
            int totalVpVoteAllowed = 1;
            if(vpVoteMap.size() == totalVpVoteAllowed){
                voterTypeReachedLimit = true;
            }
        }
        return voterTypeReachedLimit;
    }


    /**
     * This method checks if motion can be closed before allowed time, if time is up calculate the results.
     *
     * @param motion
     * @return  true/false
     */
    public boolean closeMotionVoteBeforeTimeAllowed(Motion motion) {
        LocalTime finalTime = LocalTime.now();
        long fifteen_minutes = Duration.between(finalTime, motion.getMotionStartTime())
            .getSeconds();
        if (FIFTEEN_MINUTES == fifteen_minutes) {
            calculateResultAndClose(motion);

            System.out.println("Motion closed since 15 min is up");
        }
        System.out.println("Motion open since its not 15 min yet");

        return motion.isMotionOpen();
    }


    /**
     * This method calculates final result.
     *
     * @param motion
     */
    public void createMotionResult(Motion motion) {
        setMotionResultVotesAndStatus(motion);
    }

    /**
     * This method closes the motion.
     *
     * @param motion
     */
    public void closeMotion(Motion motion) {
        if (motion != null) {
            if (motion.getCurrentMotionStatus().equals(MotionStatusEnum.PASSED.toString()) || motion
                .getCurrentMotionStatus().equals(MotionStatusEnum.FAILED.toString())) {
                motion.setMotionOpen(false);
                motion.setMotionEndTime(LocalTime.now());
            }
        }
    }

    /**
     * This method checks If VP can vote, based on Motion Ties. And sets appropriate status.
     *
     * @param motion
     * @param voteEnum
     * @param vp
     * @param isVpAvailable
     */
    public void vpVoteIfTied(Motion motion, VoteEnum voteEnum,
        VP vp, boolean isVpAvailable) {
        //If Motion status is TIED VP Votes
        if (motion.getCurrentMotionStatus().equals(MotionStatusEnum.TIED.toString())
            && isVpAvailable) {
            addVpVote(voteEnum, vp, motion);
            motion.setCurrentMotionStatus(MotionStatusEnum.PASSED.toString());
            motion.setMotionOpen(false);
            motion.setMotionEndTime(LocalTime.now());
            if (voteEnum.equals(VoteEnum.YES)) {
                motion.setYesVotes(motion.getYesVotes() + 1);
            } else {
                motion.setNoVotes(motion.getNoVotes() + 1);
                motion.setCurrentMotionStatus(MotionStatusEnum.FAILED.toString());
            }
        } else if (motion.getCurrentMotionStatus().equals(MotionStatusEnum.TIED.toString())
            && !isVpAvailable) {
            motion.setMotionOpen(false);
            motion.setMotionEndTime(LocalTime.now());
            motion.setCurrentMotionStatus(MotionStatusEnum.FAILED.toString());
        }
    }

    /**
     * This method calculates Results and close the motion.
     *
     * @param motion
     */
    public void calculateResultAndClose(Motion motion) {
        createMotionResult(motion);
        closeMotion(motion); // Close the motion if passed or failed.
    }

    /**
     * This method prints the motion result.
     *
     * @param motion
     */
    public void printResultIfClosed(Motion motion) {
        if (!motion.isMotionOpen()) {
            System.out.println(motion);
        }
    }

    /**
     * This private method calculates motion results, votes and sets the status, vote counts.
     *
     * @param motion
     */
    private void setMotionResultVotesAndStatus(Motion motion) {
        MotionVoting motionVoting = motion.getMotionVoting();
        //number of yes or no votes for senate:
        Map<String, Long> senateCount = yesNoMapCount(motionVoting.getSenateVoteMap());
        //number of yes or no vote for VP:
        Map<String, Long> vpCount = yesNoMapCount(motionVoting.getVpVoteMap());

        int totalYesCount = getTotalYesCount(senateCount, vpCount, VoteEnum.YES);
        motion.setYesVotes(totalYesCount);

        int totalNoCount = getTotalNoCount(senateCount, vpCount, VoteEnum.NO);
        motion.setNoVotes(totalNoCount);

        setMotionStatus(motion, totalYesCount, totalNoCount);
    }

    /**
     * This method  sets motion to tied status.
     *
     * @param motion
     */
    private void setMotionStatusToTied(Motion motion) {
        motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
    }

    /**
     * This method sets the motions status based on yes , no or tied count.
     *
     * @param motion
     * @param totalYesCount
     * @param totalNoCount
     */
    private void setMotionStatus(Motion motion, int totalYesCount, int totalNoCount) {
        //check if motion passed or failed. //Motion passes only if votes are more than 50% - 51 votes.
        int totalVoteNeededToPass = 51;
        if (totalYesCount >= totalVoteNeededToPass) {
            motion.setCurrentMotionStatus(MotionStatusEnum.PASSED.toString());
        } else if (totalNoCount == totalYesCount) {
            motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
        } else {
            motion.setCurrentMotionStatus(MotionStatusEnum.FAILED.toString());
        }
    }

    /**
     * This method gets total  number of No counts.
     *
     * @param senateCount
     * @param vpCount
     * @param no
     * @return totalNocount
     */
    private int getTotalNoCount(Map<String, Long> senateCount, Map<String, Long> vpCount,
        VoteEnum no) {
        int totalNoCount = 0;
        if (senateCount.containsKey(no.toString())) {
            totalNoCount = Math.toIntExact((senateCount.get(no.toString())));
        }
        if (vpCount.containsKey(VoteEnum.NO.toString())) {
            totalNoCount += Math.toIntExact((vpCount.get(VoteEnum.YES.toString())));
        }
        return totalNoCount;
    }

    /**
     * This method gets total  number of YES counts.
     *
     * @param senateCount
     * @param vpCount
     * @param yes
     * @return totalYesCount
     */
    private int getTotalYesCount(Map<String, Long> senateCount, Map<String, Long> vpCount,
        VoteEnum yes) {
        int totalYesCount = 0;
        if (senateCount.containsKey(yes.toString())) {
            totalYesCount = Math.toIntExact((senateCount.get(yes.toString())));
        }
        if (vpCount.containsKey(VoteEnum.NO.toString())) {
            totalYesCount += Math.toIntExact((vpCount.get(VoteEnum.YES.toString())));
        }
        return totalYesCount;
    }

    /**
     * This method gets the Map count of Yes or No.
     *
     * @param map
     * @return yesNoMap
     */
    private Map yesNoMapCount(Map<Integer, String> map) {
        Map<String, Long> yesNoMapCount = map
            .values()
            .stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return yesNoMapCount;
    }
}
