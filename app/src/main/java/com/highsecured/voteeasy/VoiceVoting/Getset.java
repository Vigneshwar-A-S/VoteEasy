package com.highsecured.voteeasy.VoiceVoting;

public class Getset {

/*   String name = mVoter_Details.get(4);
                        String phoneno = mVoter_Details.get(0);
                        String voteridno = mVoter_Details.get(1);
                        String relation = mVoter_Details.get(9);
                        String dob = mVoter_Details.get(5);
                        String consti = mVoter_Details.get(2);
                        String constiName = mVoter_Details.get(6);
                        String part = mVoter_Details.get(3);
                        String partName = mVoter_Details.get(7);
                        String address = mVoter_Details.get(8);*/


   String phoneno, voteridno, relation, dob, consti, constiName, part, partName, address , pollingList , nameList;


    public Getset(String phoneno, String voteridno, String relation, String dob, String consti, String constiName, String part, String partName, String address, String pollingList, String nameList) {
        this.phoneno = phoneno;
        this.voteridno = voteridno;
        this.relation = relation;
        this.dob = dob;
        this.consti = consti;
        this.constiName = constiName;
        this.part = part;
        this.partName = partName;
        this.address = address;
        this.pollingList = pollingList;
        this.nameList = nameList;
    }

    public Getset() {
    }

    public String getPollingList() {
        return pollingList;
    }

    public void setPollingList(String pollingList) {
        this.pollingList = pollingList;
    }

    public String getNameList() {
        return nameList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getVoteridno() {
        return voteridno;
    }

    public void setVoteridno(String voteridno) {
        this.voteridno = voteridno;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getConsti() {
        return consti;
    }

    public void setConsti(String consti) {
        this.consti = consti;
    }

    public String getConstiName() {
        return constiName;
    }

    public void setConstiName(String constiName) {
        this.constiName = constiName;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}


