package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.bignerdranch.android.criminalintent.DatePickerFragment.EXTRA_DATE;
import static com.bignerdranch.android.criminalintent.TimePickerFragment.EXTRA_TIME;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mFirstButton;
    private Button mLastButton;

    private java.text.DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private java.text.DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    public static CrimeFragment newInstance(UUID crimeId) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        final CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // Здесь намеренно оставлено пустое место
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // И здесь тоже
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(getFragmentManager(), DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateDate();

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(getFragmentManager(), DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mFirstButton = v.findViewById(R.id.btn_first);
        mFirstButton.setEnabled(!CrimeLab.get(getActivity()).isFirstCrime(mCrime));

        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewPager) container).setCurrentItem(0);
            }
        });

        mLastButton = v.findViewById(R.id.btn_last);
        mLastButton.setEnabled(!CrimeLab.get(getActivity()).isLastCrime(mCrime));

        mLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewPager) container).setCurrentItem(CrimeLab.get(getActivity()).getCrimes().size() - 1);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            final Date date = (Date) data.getSerializableExtra(EXTRA_DATE);
            Calendar calUpated = Calendar.getInstance();
            calUpated.setTime(date);

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.setTime(mCrime.getDate());

            calCurrent.set(Calendar.YEAR, calUpated.get(Calendar.YEAR));
            calCurrent.set(Calendar.MONDAY, calUpated.get(Calendar.MONDAY));
            calCurrent.set(Calendar.DAY_OF_MONTH, calUpated.get(Calendar.DAY_OF_MONTH));

            mCrime.setDate(calCurrent.getTime());
            updateDate();
        }

        if (requestCode == REQUEST_TIME) {
            final Date date = (Date) data.getSerializableExtra(EXTRA_TIME);

            Calendar calUpated = Calendar.getInstance();
            calUpated.setTime(date);

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.setTime(mCrime.getDate());

            calCurrent.set(Calendar.HOUR, calUpated.get(Calendar.HOUR));
            calCurrent.set(Calendar.MINUTE, calUpated.get(Calendar.MINUTE));

            mCrime.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
    }
}
