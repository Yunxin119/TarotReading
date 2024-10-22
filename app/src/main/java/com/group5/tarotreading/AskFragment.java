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

public class AskFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);

        Button askButton = view.findViewById(R.id.askquestion);
        askButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuestionActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
