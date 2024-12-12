package com.group5.tarotreading.question;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.group5.tarotreading.R;
import com.group5.tarotreading.card.CardPickActivity;
import com.group5.tarotreading.user.LoginActivity;

public class TodayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        Button todayButton = view.findViewById(R.id.today);
        // Set onclick listener, pass required value like spreadtype, question, num of cards to card picking screen
        todayButton.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), CardPickActivity.class);
                intent.putExtra("spreadType", "OneCard");
                intent.putExtra("question", "Tell me today's fortune, give me suggestion");
                intent.putExtra("pickcard", 1);
                intent.putExtra("cutcard", false);
                startActivity(intent);
            }
        });

        return view;
    }
}
