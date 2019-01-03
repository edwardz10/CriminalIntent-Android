package com.bignerdranch.android.criminalintent.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bignerdranch.android.criminalintent.CrimeLab;
import com.bignerdranch.android.criminalintent.R;
import com.bignerdranch.android.criminalintent.model.Crime;

import java.io.File;
import java.util.Date;

public class FullSizePhotoFragment extends DialogFragment {

    private ImageView mImageView;

    private static final String ARG_CRIME = "crime";

    public static FullSizePhotoFragment newInstance(final Crime crime) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME, crime);
        FullSizePhotoFragment fragment = new FullSizePhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Crime crime = (Crime) getArguments().getSerializable(ARG_CRIME);

        final View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.full_size_photo, null);

        final File photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);

        mImageView = v.findViewById(R.id.full_size_photo_viewer);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(photoFile.getPath()));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }
}
