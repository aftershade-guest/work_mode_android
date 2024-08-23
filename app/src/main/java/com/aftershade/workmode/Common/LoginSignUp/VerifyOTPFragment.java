package com.aftershade.workmode.Common.LoginSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.Databases.SessionManager;
import com.aftershade.workmode.Databases.SignUpSession;
import com.aftershade.workmode.R;
import com.aftershade.workmode.users.BottomNav;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorSession;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.aftershade.workmode.Databases.SessionManager.SESSION_USERSESSION;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_DATE_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_EMAIL_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_FULLNAME_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_GENDER_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_PASSWORD_S;
import static com.aftershade.workmode.Databases.SignUpSession.KEY_PHONENUMBER_S;

public class VerifyOTPFragment extends Fragment {

    AppCompatButton verifyBtn;
    PinView userPin;
    String codeFromSystem;
    String email, password;
    HashMap<String, String> userMap;
    //InterstitialAd interstitialAd;
    String whatToDo, newPhone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            whatToDo = getArguments().getString("whatToDo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_o_t_p, container, false);

        verifyBtn = view.findViewById(R.id.verify_phone_btn);
        userPin = view.findViewById(R.id.pin_view_otp);


        if (whatToDo.equals("signUp")) {
            SignUpSession signUpSession = new SignUpSession(getContext());
            userMap = signUpSession.getInformation();

            email = userMap.get(KEY_EMAIL_S);
            password = userMap.get(KEY_PASSWORD_S);
            sendVerificationCodeToUser(userMap.get(KEY_PHONENUMBER_S));
        } else if (whatToDo.equals("changePhone")) {
            email = getArguments().getString("email");
            password = getArguments().getString("password");
            sendVerificationCodeToUser(newPhone);
        }


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callNextFromOTP(getView());

            }
        });

        return view;
    }

    private void sendVerificationCodeToUser(String phoneNo) {

        /*PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder();

        builder.setActivity(requireActivity());
        builder.requireSmsValidation(true);
        builder.setCallbacks(callbacks);
        builder.setTimeout(60L, TimeUnit.SECONDS);
        builder.setPhoneNumber(phoneNo);
        builder.setMultiFactorSession(new MultiFactorSession() {
            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        });

        PhoneAuthOptions phoneAuthOptions = builder.build();*/

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo, 60, TimeUnit.SECONDS, requireActivity(), callbacks);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeFromSystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                userPin.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(codeFromSystem, code);

        if (whatToDo.equals("signUp")) {
            signInUsingCredential(phoneAuthCredential);
        } else if (whatToDo.equals("changePhone")) {
            updatePhoneNumber(phoneAuthCredential);
        }

    }

    /**
     * @param phoneAuthCredential Method for adding/creating a new user using credential and linking their email account with
     *                            phone number account.
     */
    private void signInUsingCredential(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();

                            user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Linking successful", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            storeNewUserData();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getContext(), "Error occurred while verifying", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    /**
     * @param phoneAuthCredential Method for updating a users phone number
     */
    private void updatePhoneNumber(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid())
                            .child("phone").setValue(newPhone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Phone number successfully changed", Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void storeNewUserData() {

        FirebaseDatabase userNode = FirebaseDatabase.getInstance();
        DatabaseReference ref = userNode.getReference("Users");

        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userMap.remove(KEY_PASSWORD_S);
        userMap.put("uid", sid);

        ErrorCatching errorCatching = new ErrorCatching(getContext());

        ref.child(sid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    SessionManager sessionManager = new SessionManager(getContext(), SESSION_USERSESSION);
                    sessionManager.createLoginSession(userMap.get(KEY_FULLNAME_S), email,
                            userMap.get(KEY_PHONENUMBER_S), password, userMap.get(KEY_DATE_S), userMap.get(KEY_GENDER_S));

                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Verification email sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        errorCatching.onException(task.getException());
                                    }
                                }
                            });

                    Toast.makeText(getContext(), "Account successfully created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), BottomNav.class));
                    getActivity().finish();

                } else {
                    errorCatching.onException(task.getException());
                }
            }
        });

    }

    public void callNextFromOTP(View view) {
        String code = userPin.getText().toString();

        if (!code.isEmpty()) {
            verifyCode(code);
        }

    }
}