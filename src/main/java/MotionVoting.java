/*
 * Copyright (c) ${date} Travelport. All rights reserved.
 */

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a motion voting class that will tally votes based on voterType.
 *
 */

public class MotionVoting {

    private Map<Integer, String> senateVoteMap = new HashMap<>();
    private Map<Integer, String> vpVoteMap = new HashMap<>();
    private int totalVote = 101;
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

    public void addVote(VoteEnum voteEnum, VoterType voterType, Motion motion){
        // check if motion open
        if(motion.isMotionOpen()) {
            //check if voted  && 101 count
            if (!checkIfAlreadyVoted(voterType) && !checkIfVoteCount101() &&  !checkIfVoterTypeReachedLimit(voterType)) {
               voteTally(voterType, voteEnum);
                motion.setCurrentMotionStatus(MotionStatusEnum.OPEN.toString());
            } else {
                System.out.println("Voter already voted");
                motion.setCurrentMotionStatus(MotionStatusEnum.CLOSED.toString());
                motion.setMotionEndTime(LocalTime.now());
                motion.setMotionOpen(false);
            }
        }
        else{
            System.out.println("Motion not open");
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

}
