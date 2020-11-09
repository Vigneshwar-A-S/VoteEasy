package com.highsecured.voteeasy.VoiceVoting;

public class PartyVote {

    String party;
    long voteCount;

    public PartyVote(String party, long voteCount) {
        this.party = party;
        this.voteCount = voteCount;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }
}
