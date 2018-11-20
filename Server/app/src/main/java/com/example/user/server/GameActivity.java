package com.example.user.server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class GameActivity extends AppCompatActivity {

    TextView t2,t3,t4,sc;
    Button cam, restart, share;
    int count = 0;
    Bitmap bitmap;
    String image_path ="";
    int score = 0;
    ProgressBar progressbar, advancement;
    List l;
    ArrayList<Integer> m = new ArrayList<Integer>();
    CountDownTimer clock;
    boolean time_is_over = false;
    int game_lives_g = 0;
    TextToSpeech speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        String message = intent.getStringExtra("a");
        message = message.substring(1,message.length()-2);
        String info[] = message.split(";, ");
        String message2 = intent.getStringExtra("b");
        game_lives_g = Integer.parseInt(message2);

        t2 = (TextView)findViewById(R.id.textView2);
        t3 = (TextView)findViewById(R.id.textView3);
        t4 = (TextView)findViewById(R.id.textView4);
        sc = (TextView)findViewById(R.id.score);
        cam = (Button)findViewById(R.id.button3);
        restart = (Button)findViewById(R.id.button4);
        progressbar = (ProgressBar)findViewById(R.id.pb);
        advancement = (ProgressBar)findViewById(R.id.advancement);
        share = (Button)findViewById(R.id.button5);

        sc.setText("Score : 0");
        progressbar.setVisibility(View.GONE);
        advancement.setProgress(0);
        advancement.setMax(game_lives_g);

        int seconds = game_lives_g*20;
        speech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);
                }
            }
        });

//        final Typeface cf = Typeface.createFromAsset(getAssets(),"fonts/angrybirds_regular.ttf");
//        cam.setTypeface(cf);
//        restart.setTypeface(cf);
//        share.setTypeface(cf);

        l = Arrays.asList(info);
        Collections.shuffle(l);
        System.out.println(""+l.get(0)+l.get(1)+l.get(2));

        t3.setText("Ready ... ");
        cam.setVisibility(View.INVISIBLE);
        cam.setClickable(false);

        new CountDownTimer(4000,1000){

            @Override
            public void onTick(long l) {
                t4.setText("Starts in : "+l/1000);
            }

            @Override
            public void onFinish() {
                t3.setText("Take an image of "+l.get(count));
                speech.speak("Take an image of " + l.get(count), TextToSpeech.QUEUE_FLUSH, null);
                clock.start();
                cam.setVisibility(View.VISIBLE);
                cam.setClickable(true);
            }
        }.start();


        //timer starts
        clock = new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished/1000;
                t4.setText(String.format("%02d",seconds/60)+":"+String.format("%02d",seconds%60));
            }

            public void onFinish() {
                share.setVisibility(View.VISIBLE);
                t4.setText("Time is over!");
                t4.setTextColor(Color.RED);
                count = 0;
                m.add(score);
                t2.setText("Game is over. Your score is: "+score);
                t3.setText("");
                score = 0;
                cam.setVisibility(View.INVISIBLE);
                cam.setClickable(false);
                time_is_over = true;
            }
        };

        cam.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                t2.setText("");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        restart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                restart();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this awesome game. I have scored "+m.get(0)+" :D .");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        count ++;
        progressbar.setVisibility(View.VISIBLE);
        cam.setVisibility(View.INVISIBLE);
        cam.setClickable(false);
        if (data != null){
            bitmap = (Bitmap)data.getExtras().get("data");
            String a =save_image_toSD(bitmap);
            image_path = a;
            Log.d(a,"done");
            try {
                send();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send() throws InterruptedException {
        new AsyncTask<Integer, Void, Void>(){
            String msg = "";
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    msg = executeSSHcommand();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Void result){

                progressbar.setVisibility(View.GONE);
                if(!time_is_over){
                    cam.setVisibility(View.VISIBLE);
                    cam.setClickable(true);
                    if (msg.equals(l.get(count-1))){
                        t2.setText("Well done.");
                        score ++;
                    }
                    else t2.setText("Bad Luck");
                    sc.setText("Score : "+score);
                    advancement.setProgress(count);


                    if (count>=game_lives_g){
                        count = 0;
                        m.add(score);
                        clock.cancel();
                        share.setVisibility(View.VISIBLE);
                        t2.setText("Game is over. Your score is: "+score);
                        t3.setText("");
                        t4.setTextColor(Color.GREEN);
                        score = 0;
                        cam.setVisibility(View.INVISIBLE);
                        cam.setClickable(false);
                    }else {
                        t3.setText("Take an image of " + l.get(count));
                        speech.speak("Take an image of " + l.get(count), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }

            }
        }.execute(1);
//        Thread.sleep(2000);

    }

    public String executeSSHcommand() throws IOException, JSchException, SftpException {
//        String user = "avijit";
//        String host = "ec2-54-183-97-56.us-west-a.compute.amazonaws.com";
//        String password = "CcjSci431";
        String user, host, password;
        Global g = Global.getInstance();
        String[] uhp =  g.getData();
        if (uhp[0] == null|uhp[1] == null|uhp[2] == null) {
            user = "tahsin";
            host = "192.168.1.108";
            password = "CcjSci431";
        }else{
            user = uhp[0];
            host = uhp[1];
            password = uhp[2];
        }
        Log.d(user,password);
        int port = 22;
        String m = "";

        try {

            JSch jsch = new JSch();
//            System.out.println("hu");
//            jsch.addIdentity("private_key",read_key().toByteArray(),null,null);
//            System.out.println("identity added ");
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            System.out.println("session created.");

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(10000);
            session.connect();
            System.out.println("session connected.....");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            Log.d("Success", " :)");
            ChannelSftp c = (ChannelSftp) channel;
            c.put(image_path+"/myimage.png", "/home/tahsin/PycharmProjects/Object_detection");
            c.exit();
            channel.disconnect();

            Thread.sleep(4000); //pause for 4 seconds

            ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
            channel2.setCommand("cd /home/tahsin/PycharmProjects/Object_detection\n" + "cat a.txt");
            channel2.setErrStream(System.err);
            channel2.setInputStream(null);
            InputStream in = channel2.getInputStream();
            channel2.connect();
            Log.d("Success on 2nd channel", " :)");

            byte[] tmp = new byte[1024];
            while (in.read() != -1)
            {
                int i = in.read(tmp, 0, 1024);
                Log.d(""+in.read(),""+i);

                m = new String(tmp, 0, i);
                Log.d("Object detected",m);
            }

            in.close();
            channel2.disconnect();
            session.disconnect();
        }
        catch(JSchException e) {
            // show the error in the UI
            Log.d("error", " :(");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return m;
    }

    private String save_image_toSD(Bitmap b){
        OutputStream output;
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Log.d("a",filepath.getAbsolutePath());
        File dir = new File(filepath.getAbsolutePath() + "/Server Image folder");
        dir.mkdir();

        File file = new File(dir, "myimage.png");

//        Toast.makeText(MainActivity.this, "Image Saved to SD Card", Toast.LENGTH_SHORT).show();

        try {
            output = new FileOutputStream(file);

            b.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
        return filepath.getAbsolutePath() + "/Server Image folder";
    }

    private void restart(){
        Intent main_activity = new Intent(getApplicationContext(),MainActivity.class);
        main_activity.putExtra("a",Integer.toString(game_lives_g));
        main_activity.putExtra("b","play");
        startActivity(main_activity);
    }

//    public void onPause(){
//        if(speech != null){
//            speech.stop();
//            speech.shutdown();
//        }
//        super.onPause();
//    }
}
