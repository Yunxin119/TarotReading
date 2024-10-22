package com.group5.tarotreading;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TodayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        Button todayButton = view.findViewById(R.id.today);
        todayButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardPickingActivity.class);
            intent.putExtra("cardnum", 1);
            intent.putExtra("question", "Tell me today's fortune, give me suggestion");
            startActivity(intent);
        });

        return view;
    }
}
