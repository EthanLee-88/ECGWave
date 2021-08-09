package com.ethan.ecgwave.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ethan.ecgwave.R;
import com.ethan.ecgwave.databinding.FragmentHomeBinding;
import com.ethan.ecgwave.view.ECGRealTimeChart;


import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private ArrayList<Integer> team = new ArrayList<>();
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private ECGRealTimeChart mECGRealTimeChart;
    private int[] data = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            51, 60, 71, 88, 90, 92, 96, 122,
            125, 122, 96, 122, 129, 168, 128, 109, 100, 89,
            128, 178, 199, 256, 109, 188, 256, 1988, 2012, 2041,1999,
            2399, 256, 128, 109, 88, 67, 23, 167, 256, 562,
            235, 109, 56, 33, 12, 150, 123, 109, 99, 88,
            77, 67, 55, 34, 12, 45, 99, 156, 199, 256,
            235, 209, 200, 188, 169, 150, 123, 109, 99, 88,0,0,0,0,0,};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textHome;
        mECGRealTimeChart = binding.ecgChart;

        textView.setOnClickListener((View view) ->{
            onClick();
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    private void onClick(){
        if (mECGRealTimeChart.getData().size() == 0){
            new Thread(() -> {
                addDataDelay(data);
            }).start();
        }else {
            mECGRealTimeChart.clearData();
        }
    }

    public void addDataDelay(int[] data) {
        mECGRealTimeChart.setNoDataComing(false);
        for (int i = 0; i < 10; i++) {
            try {
                for (int datum : data) {
                    team.add(datum);
                    if (team.size() >= 16){
                        mECGRealTimeChart.addData(team);
                        Thread.sleep(68);
                        team.clear();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mECGRealTimeChart.setNoDataComing(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}