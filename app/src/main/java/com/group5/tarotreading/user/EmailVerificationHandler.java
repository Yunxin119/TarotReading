package com.group5.tarotreading.user;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationHandler {
    private static final String TAG = "EmailVerificationHandler";
    private final FirebaseAuth auth;
    private EmailVerificationListener listener;

    public interface EmailVerificationListener {
        void onVerificationEmailSent();
        void onVerificationSuccess();
        void onVerificationFailed(Exception e);
    }

    public EmailVerificationHandler() {
        this.auth = FirebaseAuth.getInstance();
    }

    public void setVerificationListener(EmailVerificationListener listener) {
        this.listener = listener;
    }

    public void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Verification email sent");
                            if (listener != null) {
                                listener.onVerificationEmailSent();
                            }
                        } else {
                            Log.e(TAG, "Failed to send verification email", task.getException());
                            if (listener != null) {
                                listener.onVerificationFailed(task.getException());
                            }
                        }
                    });
        }
    }


    public void checkEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        if (listener != null) {
                            listener.onVerificationSuccess();
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onVerificationFailed(task.getException());
                    }
                }
            });
        }
    }
}
