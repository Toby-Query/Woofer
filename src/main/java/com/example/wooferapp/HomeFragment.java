package com.example.wooferapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class HomeFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private View mContainer;

    ArrayList<String> names=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mContainer == null) {
            mContainer = inflater.inflate(R.layout.fragment_home, container, false);
            try {
                initializeViews();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        return mContainer;
    }
    public void initializeViews() throws ExecutionException, InterruptedException {
        // Get references to the views
        expandableListView = mContainer.findViewById(R.id.expandableListView);
        Button rerunButton = mContainer.findViewById(R.id.rerunButton);

        rerunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rerunCode();
            }
        });

        populateExpandableListView();
    }

    public void rerunCode() {
        try {
            expandableListDetail.clear();
            expandableListDetail = ExpandableListDataPump.getData();
            expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void populateExpandableListView() throws ExecutionException, InterruptedException {

        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

    }
}