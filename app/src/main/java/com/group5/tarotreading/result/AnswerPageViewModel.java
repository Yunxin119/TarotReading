package com.group5.tarotreading.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnswerPageViewModel extends ViewModel {
    private MutableLiveData<TarotResponse> tarotResponse = new MutableLiveData<>();
    private MutableLiveData<String> currentInterpretation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLastInterpretation = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isFirstInterpretation = new MutableLiveData<>(true);

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);//wait Text

    public void setTarotResponse(String response) {
        TarotResponse newResponse = new TarotResponse(response);
        tarotResponse.setValue(newResponse);
        currentInterpretation.setValue(newResponse.getCurrentInterpretation());
        isLastInterpretation.setValue(!newResponse.hasNext());
        isFirstInterpretation.setValue(true);
        // Set loading to false when we get the response
        isLoading.setValue(false);
    }
    // Add getter for loading state
    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
    public void showPreviousInterpretation() {
        TarotResponse response = tarotResponse.getValue();
        if (response != null && response.hasPrevious()) {
            currentInterpretation.setValue(response.getPrevious());
            isLastInterpretation.setValue(false);
            isFirstInterpretation.setValue(!response.hasPrevious());
        }
    }

    public void showNextInterpretation() {
        TarotResponse response = tarotResponse.getValue();
        if (response != null && response.hasNext()) {
            currentInterpretation.setValue(response.getNext());
            isLastInterpretation.setValue(!response.hasNext());
            isFirstInterpretation.setValue(false);
        }
    }

    public LiveData<String> getCurrentInterpretation() {
        return currentInterpretation;
    }

    public LiveData<Boolean> isLastInterpretation() {
        return isLastInterpretation;
    }
    public LiveData<Boolean> isFirstInterpretation() {
        return isFirstInterpretation;
    }
}
