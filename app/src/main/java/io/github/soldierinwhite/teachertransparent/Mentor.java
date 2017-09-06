package io.github.soldierinwhite.teachertransparent;

/**
 * Created by schoo on 2017/03/14.
 */

public class Mentor {

    private String mMentorName;
    private String mMentorEmail;

    public Mentor(String mentorName, String mentorEmail){
        mMentorName = mentorName;
        mMentorEmail = mentorEmail;
    }

    public String getMentorName(){
        return mMentorName;
    }

    public String getMentorEmail(){
        return mMentorEmail;
    }
}
