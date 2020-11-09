package com.highsecured.voteeasy;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.highsecured.voteeasy.VoiceVoting.PartyVote;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class ResultActivity extends AppCompatActivity {

    ArrayList<PartyVote> result = new ArrayList<>();
    ArrayList<BarEntry> barEntry = new ArrayList<>();
    ArrayList<String> labelNames;
    BarChart barChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        barChart = findViewById(R.id.barchart);

        labelNames = new ArrayList<>();

        barEntry.clear();
        labelNames.clear();
        resultedVotes();

        for (int i = 0; i < result.size(); i++){
            String party = result.get(i).getParty();
            long voteCount = result.get(i).getVoteCount();
            barEntry.add(new BarEntry(i, voteCount));
            labelNames.add(party);

        }

        BarDataSet barDataSet = new BarDataSet(barEntry,"VOTE AUDIT");
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        Description description = new Description();
        description.setText("2021 Election Result");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        // we need to set Xaxis value formater
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        //set position or
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        xAxis.setLabelRotationAngle(270);
        barChart.animateY(3000);
        barChart.invalidate();

    }

    private void resultedVotes(){

        result.clear();
        result.add(new PartyVote("PARTY  1", 250));
        result.add(new PartyVote("PARTY  2", 500));
        result.add(new PartyVote("PARTY  3", 351));
        result.add(new PartyVote("PARTY  4", 400));
        result.add(new PartyVote("PARTY  5", 610));
        result.add(new PartyVote("PARTY  6", 500));
        result.add(new PartyVote("PARTY  7", 250));
        result.add(new PartyVote("PARTY  8", 800));
        result.add(new PartyVote("PARTY  9", 136));
        result.add(new PartyVote("PARTY 10", 281));


    }
}
