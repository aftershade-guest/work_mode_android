package com.aftershade.workmode.users.settings.Help;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aftershade.workmode.R;
import com.aftershade.workmode.users.ui.profile.ManagePrivacyFragment;

import static com.aftershade.workmode.users.BottomNav.nameText;

public class HelpFragment extends Fragment {

    TextView reportProblemTxtV, faqFeedback, privacySecurityHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        nameText.setValue(getString(R.string.help_));

        reportProblemTxtV = view.findViewById(R.id.report_a_problem_help);
        faqFeedback = view.findViewById(R.id.faq_feedback_help);
        privacySecurityHelp = view.findViewById(R.id.privacy_and_security_help);

        reportProblemTxtV.setOnClickListener(onClickListener);
        faqFeedback.setOnClickListener(onClickListener);
        privacySecurityHelp.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == reportProblemTxtV.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new ReportProblemFragment()).addToBackStack("").commit();

            } else if (v.getId() == faqFeedback.getId()) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_userdash,
                        new FAQFeedbackFragment()).addToBackStack("").commit();
            }

        }
    };
}