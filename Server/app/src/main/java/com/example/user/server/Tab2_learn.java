package com.example.user.server;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by User on 8/22/2017.
 */

public class Tab2_learn extends Fragment {

    Button explore, help;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        explore = rootView.findViewById(R.id.start2);
        help = rootView.findViewById(R.id.help);
        ImageView image = rootView.findViewById(R.id.im_l);
        image.setImageBitmap(new Scale_image().decodeSampledBitmapFromResource(getResources(), R.drawable.pikachu, 300, 600));
//        Picasso.with(getContext()).load(R.drawable.pikachu).resize(500,1000).into(image);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main_activity = new Intent(getContext(), MainActivity.class);
                main_activity.putExtra("a","1");
                main_activity.putExtra("b","learn");
                startActivity(main_activity);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogt = new AlertDialog.Builder(getContext());
                View tutorialview = getActivity().getLayoutInflater().inflate(R.layout.tutorial,null);
                dialogt.setTitle("Wanna read?");
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

        return rootView;
    }
}
