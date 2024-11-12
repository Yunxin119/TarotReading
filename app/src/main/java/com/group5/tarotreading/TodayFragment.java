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

import com.group5.tarotreading.cardActivity.CardPickActivity;

public class TodayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        Button todayButton = view.findViewById(R.id.today);
        todayButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardPickActivity.class);
            intent.putExtra("spreadType", "ONECARD");
            intent.putExtra("question", "Tell me today's fortune, give me suggestion");
            intent.putExtra("pickCard", 1);
            intent.putExtra("cutCard", false);
            startActivity(intent);
        });

        return view;
    }
}
