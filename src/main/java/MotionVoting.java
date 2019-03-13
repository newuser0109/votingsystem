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
    private int totalVote = 101;
    private int totalVoteNeededToPass = 51;
    private int tiedVote = 50;
    private int totalSenateVoteAllowed = 100;
    private int totalVpVoteAllowed = 1;
    private static final int FIFTEEN_MINUTES = 15 * 60;


    public Map<Integer, String> getSenateVoteMap() {
        return senateVoteMap;
    }

    public void setSenateVoteMap(Map<Integer, String> senateVoteMap) {
        this.senateVoteMap = senateVoteMap;
    }

    public Map<Integer, String> getVpVoteMap() {
        return vpVoteMap;
    }

    public void setVpVoteMap(Map<Integer, String> vpVoteMap) {
        this.vpVoteMap = vpVoteMap;
    }

    public  void  voteTally(VoterType voterType, VoteEnum voteEnum){
        if(voterType instanceof  Senator){
            senateVoteMap.put(voterType.getVoterId(), voteEnum.toString());
        }
        if(voterType  instanceof  VP){
            vpVoteMap.put(voterType.getVoterId(), voteEnum.toString());
        }
    }

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

    public boolean checkIfVoteCount101(){
        int totalVoted = this.senateVoteMap.size();
        totalVoted += this.vpVoteMap.size();
        boolean voteCount101= false;
        if(totalVoted == totalVote){
            voteCount101 = true;
        }
        return voteCount101;
    }


    public void addSenatorVote(VoteEnum voteEnum, Senator senator, Motion motion) {
        voteTally(senator, voteEnum);
                motion.setCurrentMotionStatus(MotionStatusEnum.OPEN.toString());
    }

    public void addVpVote(VoteEnum voteEnum, VP vp, Motion motion) {
        if (motion.getCurrentMotionStatus()
            .equals(MotionStatusEnum.TIED.toString())
            && this.vpVoteMap.size() < 1) {
            voteTally(vp, voteEnum);
            motion.setCurrentMotionStatus(MotionStatusEnum.OPEN.toString());
        }
    }

    public boolean checkIfVoterTypeReachedLimit(VoterType voterType){
        boolean voterTypeReachedLimit = false;
        if(voterType instanceof  Senator){
            if(senateVoteMap.size() == this.totalSenateVoteAllowed){
                voterTypeReachedLimit = true;
            }
        }
        if(voterType  instanceof  VP){
            if(vpVoteMap.size() == this.totalVpVoteAllowed){
                voterTypeReachedLimit = true;
            }
        }
        return voterTypeReachedLimit;
    }


    public boolean closeMotionVoteBeforeTimeAllowed(Motion motion) {
        LocalTime finalTime = LocalTime.now();
        long fifteen_minutes = Duration.between(finalTime, motion.getMotionStartTime())
            .getSeconds();
        if (FIFTEEN_MINUTES == fifteen_minutes) {
            motion.setMotionOpen(false);
        }
        return motion.isMotionOpen();
    }


    public void createMotionResult(Motion motion) {
        setMotionResultVotesAndStatus(motion);
    }

    public void closeMotion(Motion motion) {
        if (motion != null) {
            if (motion.getCurrentMotionStatus().equals(MotionStatusEnum.PASSED.toString()) || motion
                .getCurrentMotionStatus().equals(MotionStatusEnum.FAILED.toString())) {
                motion.setMotionOpen(false);
                motion.setMotionEndTime(LocalTime.now());
            }
        }
    }


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
            }
        } else if (motion.getCurrentMotionStatus().equals(MotionStatusEnum.TIED.toString())
            && !isVpAvailable) {
            motion.setMotionOpen(false);
            motion.setMotionEndTime(LocalTime.now());
            motion.setCurrentMotionStatus(MotionStatusEnum.FAILED.toString());
        }
    }

    public void calculateResultAndClose(Motion motion) {
        createMotionResult(motion);
        closeMotion(motion); // Close the motion if passed or failed.
    }

    public void printResultIfClosed(Motion motion) {
        if (!motion.isMotionOpen()) {
            System.out.println(motion);
        }
    }

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

    private void setMotionStatusToTied(Motion motion) {
        motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
    }

    private void setMotionStatus(Motion motion, int totalYesCount, int totalNoCount) {
        //check if motion passed or failed. //Motion passes only if votes are more than 50% - 51 votes.
        if (totalYesCount >= totalVoteNeededToPass) {
            motion.setCurrentMotionStatus(MotionStatusEnum.PASSED.toString());
        } else if (totalNoCount == totalYesCount) {
            motion.setCurrentMotionStatus(MotionStatusEnum.TIED.toString());
        } else {
            motion.setCurrentMotionStatus(MotionStatusEnum.FAILED.toString());
        }
    }

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

    private Map yesNoMapCount(Map<Integer, String> map) {
        Map<String, Long> yesNoMapCount = map
            .values()
            .stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return yesNoMapCount;
    }
}
