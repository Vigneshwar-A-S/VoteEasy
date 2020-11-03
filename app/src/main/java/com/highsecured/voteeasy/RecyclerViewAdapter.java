package com.highsecured.voteeasy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Candidate> CandidateData;



    private ArrayList<String> mName_Candi = new ArrayList<>();
    private ArrayList<String> mName_Party = new ArrayList<>();
    private ArrayList<String> mImage_Candi = new ArrayList<>();
    private ArrayList<String> mImage_Party = new ArrayList<>();
    private ArrayList<String> mPoll_no = new ArrayList<>();
    private String phoneNumber;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mName_Candi, ArrayList<String> mName_Party, ArrayList<String> mImage_Candi, ArrayList<String> mImage_Party, ArrayList<String> mPoll_no, String phoneNumber) {
        this.mContext = mContext;
        this.mName_Candi = mName_Candi;
        this.mName_Party = mName_Party;
        this.mImage_Candi = mImage_Candi;
        this.mImage_Party = mImage_Party;
        this.mPoll_no = mPoll_no;
        this.phoneNumber = phoneNumber;
    }

    public RecyclerViewAdapter(List<Candidate> CandidateData) {
        this.CandidateData = CandidateData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.flip_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Glide.with(mContext).asBitmap().load(mImage_Candi.get(position)).into(holder.candi_image);
        Glide.with(mContext).asBitmap().load(mImage_Party.get(position)).into(holder.party_image);



        holder.candi_name.setText(mName_Candi.get(position));
        holder.party_name.setText(mName_Party.get(position));


        holder.party_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.flipView.flipTheView();

            }
        });


        holder.candi_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Candidate Name: " + mName_Candi.get(position), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(view.getContext(), PopActivity.class);

                        intent.putExtra("name" , mName_Candi.get(position));
                        intent.putExtra("ImageUrl" , mImage_Candi.get(position) );
                        intent.putExtra("party" , mName_Party.get(position));
                        intent.putExtra("poll_no" , mPoll_no.get(position));
                        intent.putExtra("phoneNumber" , phoneNumber);
                        view.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mName_Candi.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView candi_name;
        TextView party_name;
        ImageView candi_image;
        ImageView party_image;
        CardView cardView_candi;
        CardView cardView_party;
        EasyFlipView flipView;


        public MyViewHolder(View itemView){
            super(itemView);

            candi_name = itemView.findViewById(R.id.candi_name);
            candi_image = itemView.findViewById(R.id.candi_image);
            cardView_candi = itemView.findViewById(R.id.cardview_candi);
            cardView_party = itemView.findViewById(R.id.cardview_party);
            party_name = itemView.findViewById(R.id.party_name);
            party_image = itemView.findViewById(R.id.party_image);
            flipView = itemView.findViewById(R.id.flip);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipView.flipTheView();

                }
            });


        }


    }
}
