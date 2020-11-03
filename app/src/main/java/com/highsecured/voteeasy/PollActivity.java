package com.highsecured.voteeasy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollActivity extends AppCompatActivity {
    private static final String TAG = "PollActivity";
    private Context mContext;


    private List<Candidate> CandidateData;
    private RecyclerViewAdapter adapter;
    private RecyclerView rv;

    private String name;
    private String party;
    private String ImageUrl;
    private String votePoll;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        TextView details = findViewById(R.id.details);

        Intent intent = getIntent();

        String CandiNameList = intent.getStringExtra("CandiNames");
        String PartyNameList = intent.getStringExtra("CandiParty");
        String CandiImageURLs = intent.getStringExtra("CandiImages");
        String PartyImageURLs = intent.getStringExtra("PartyImages");
        String Polling_no = intent.getStringExtra("Polling_no");
        String VoterDetails = intent.getStringExtra("VoterDetails");
        String UserphoneNumber = intent.getStringExtra("phoneNumber");

           Log.d(TAG, "onCreate: CandiNameList: " + CandiNameList);
           Log.d(TAG, "onCreate: PartyNameList: " + PartyNameList);
           Log.d(TAG, "onCreate: CandiImageURLs: " + CandiImageURLs);
           Log.d(TAG, "onCreate: PartyImageURLs: " + PartyImageURLs);
           Log.d(TAG, "onCreate: Polling_no: " + Polling_no);
           Log.d(TAG, "onCreate: VoterDetails: " + VoterDetails);

                 ArrayList<String> mName_Candi = new ArrayList<>();
                  ArrayList<String> mName_Party = new ArrayList<>();
                   ArrayList<String> mImage_Candi = new ArrayList<>();
                   ArrayList<String> mImage_Party = new ArrayList<>();
                   ArrayList<String> mPoll_no = new ArrayList<>();





        if(CandiNameList != ""){
            String[] CandiNameStrings = CandiNameList.trim().split(" " , -2);                                                                                                     ;
            for (String a : CandiNameStrings)
            {
                mName_Candi.add(a);
            }
        }

        if (PartyNameList != ""){

            String[] PartyNameStrings = PartyNameList.trim().split(" " , -2);

            for (String a : PartyNameStrings)
            {
                mName_Party.add(a);
            }
        }

        if (CandiImageURLs != ""){
             String[] CandiURLStrings   = CandiImageURLs.trim().split(" " , -2);


            for (String a : CandiURLStrings)
            {
                mImage_Candi.add(a);
            }

        }

        if (PartyImageURLs != ""){
            String[] PartyURLStrings = PartyImageURLs.trim().split(" " , -2);


            for (String a : PartyURLStrings)
            {
                mImage_Party.add(a);
            }

        }

        if (Polling_no != ""){
            String[] PollingStrings = Polling_no.trim().split(" " , -2);


            for (String a : PollingStrings)
            {
                mPoll_no.add(a);
            }

        }

        if(VoterDetails != ""){
            ArrayList<String> mVoter_Details = new ArrayList<>();
            String[] VoterDetailsStrings = VoterDetails.trim().split("_" , -2);
            for (String a : VoterDetailsStrings)
            {
                mVoter_Details.add(a);
            }

            if(mVoter_Details.size() > 1){


                String name = mVoter_Details.get(4);
                String phoneno = mVoter_Details.get(0);
                String voteridno = mVoter_Details.get(1);
                String relation = mVoter_Details.get(9);
                String dob = mVoter_Details.get(5);
                String consti = mVoter_Details.get(2);
                String constiName = mVoter_Details.get(6);
                String part = mVoter_Details.get(3);
                String partName = mVoter_Details.get(7);
                String address = mVoter_Details.get(8);

                String det  = "<b>Name</b>           :</b> " + name + "<br>"+
                        "<b>Voter ID no        :</b> " + voteridno + "<br>" +
                        "<b>DOB                :</b> " + dob + "<br>" +
                        "<b>Relation           :</b> " + relation + "<br>" +
                        "<b>Constituency no.   :</b> " + consti + "<br>" +
                        "<b>Constituency Name  :</b> " + constiName + "<br>" +
                        "<b>Part no.           :</b> " + part + "<br>" +
                        "<b>Part Name          :</b> " + partName + "<br>" +
                        "<b>Address            :</b> " + address + "<br><br><br>" +
                        "No. of Candidates in Your Part - " + "<b>" + mName_Party.size() + "</b>";


                details.setText(Html.fromHtml(det));

            }


        }

                RecyclerView recyclerView = findViewById(R.id.candi_recyclerview);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mName_Candi, mName_Party, mImage_Candi, mImage_Party , mPoll_no , UserphoneNumber);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        showPopup();

                    }
                });
                


    }
    
//    public void showPopup(String name, String ImageUrl, String party, String poll_noo){
       public void showPopup(){

        final Dialog mDialog = new Dialog(PollActivity.this);
        mDialog.setContentView(R.layout.custom_popup);

        ImageView close;
        ImageView CandidateImage;
        TextView CandiName , PartyName;
        Button vote_btn;
        
        close = mDialog.findViewById(R.id.closeView);
        CandidateImage = mDialog.findViewById(R.id.CandiImage);
        CandiName = mDialog.findViewById(R.id.CandiName);
        PartyName = mDialog.findViewById(R.id.PartyName);
        vote_btn = mDialog.findViewById(R.id.vote_btn);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        vote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mDialog.show();

    }


}
