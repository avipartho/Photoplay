package com.example.user.server;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class Tab1_play extends Fragment {

    Button start, tutorial, setting;
    int lives = 3;
    String msg= "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        start = rootView.findViewById(R.id.start);
        tutorial = rootView.findViewById(R.id.tutorial);
        setting = rootView.findViewById(R.id.setting);
        ImageView image = rootView.findViewById(R.id.im_p);
        image.setImageBitmap(new Scale_image().decodeSampledBitmapFromResource(getResources(), R.drawable.mickey, 300, 600));
//        Picasso.with(getContext()).load(R.drawable.mickey).resize(500,1000).into(image);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_activity = new Intent(getContext(), MainActivity.class);
                msg = Integer.toString(lives);
                main_activity.putExtra("a",msg);
                main_activity.putExtra("b","play");
                startActivity(main_activity);
            }
        });

        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogt = new AlertDialog.Builder(getContext());
                View tutorialview = getActivity().getLayoutInflater().inflate(R.layout.tutorial,null);
                dialogt.setTitle("Instructions");
                final TextView textView = tutorialview.findViewById(R.id.tutorial);

                String text = "";
                try {
                    InputStream io = getActivity().getAssets().open("tutorial.txt");
                    byte buffer[] = new byte[io.available()];
                    io.read(buffer);
                    io.close();
                    text = new String(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textView.setText(text);

                dialogt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogt.setView(tutorialview);
                AlertDialog alertDialogt = dialogt.create();
                alertDialogt.setCancelable(false);
                alertDialogt.setCanceledOnTouchOutside(false);
                alertDialogt.show();

                Button pbutton = alertDialogt.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.GREEN);

            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View settingview = getActivity().getLayoutInflater().inflate(R.layout.setting,null);
                dialog.setTitle("Choose difficulty level");
                final Spinner spinner = settingview.findViewById(R.id.spinner);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.level));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (spinner.getSelectedItem().toString().equals("Easy"))lives = 3;
                        else if (spinner.getSelectedItem().toString().equals("Medium")) lives = 5;
                        else lives = 7;
//                        Toast.makeText(IntroActivity.this,"Selected level",Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.setView(settingview);
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.RED);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.GREEN);
            }
        });

        return rootView;
    }
}
