package com.highsecured.voteeasy;

public class Candidate {

    private String candi_name;
    private String candi_party;
    private String party_image;
    private String candi_image;
    private String poll_no;

    public Candidate() {
    }

    public Candidate(String candi_name, String candi_party, String party_image, String candi_image, String poll_no) {
        this.candi_name = candi_name;
        this.candi_party = candi_party;
        this.party_image = party_image;
        this.candi_image = candi_image;
        this.poll_no = poll_no;
    }

    public String getPoll_no() {
        return poll_no;
    }

    public void setPoll_no(String poll_no) {
        this.poll_no = poll_no;
    }

    public String getCandi_image() {
        return candi_image;
    }

    public void setCandi_image(String candi_image) {
        this.candi_image = candi_image;
    }

    public String getParty_image() {
        return party_image;
    }

    public void setParty_image(String party_image) {
        this.party_image = party_image;
    }

    public String getCandi_name() {
        return candi_name;
    }

    public void setCandi_name(String candi_name) {
        this.candi_name = candi_name;
    }

    public String getCandi_party() {
        return candi_party;
    }

    public void setCandi_party(String candi_party) {
        this.candi_party = candi_party;
    }
}
