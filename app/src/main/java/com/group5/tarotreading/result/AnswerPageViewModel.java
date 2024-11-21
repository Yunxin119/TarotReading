package com.group5.tarotreading.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnswerPageViewModel extends ViewModel {
    private MutableLiveData<TarotResponse> tarotResponse = new MutableLiveData<>();
    private MutableLiveData<String> currentInterpretation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLastInterpretation = new MutableLiveData<>(false);

    public void setTarotResponse(String response) {
        TarotResponse newResponse = new TarotResponse(response);
        tarotResponse.setValue(newResponse);
        currentInterpretation.setValue(newResponse.getCurrentInterpretation());
        isLastInterpretation.setValue(!newResponse.hasNext());
    }

    public void showNextInterpretation() {
        TarotResponse response = tarotResponse.getValue();
        if (response != null && response.hasNext()) {
            currentInterpretation.setValue(response.getNext());
            isLastInterpretation.setValue(!response.hasNext());
        }
    }

    public LiveData<String> getCurrentInterpretation() {
        return currentInterpretation;
    }

    public LiveData<Boolean> isLastInterpretation() {
        return isLastInterpretation;
    }
}
