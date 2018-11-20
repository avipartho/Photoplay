package com.example.user.server;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int count = 0;
    ImageView camera;
    Button btn_c, btn_s;
    Bitmap bitmap;
    String image_path ="";
    String saved_image_name = "";
    String mode = "";
    ArrayList<String> image_names = new ArrayList<String>();
    int game_lives_m ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_c = (Button) findViewById(R.id.button);
        btn_s = (Button) findViewById(R.id.save);
        camera = (ImageView) findViewById(R.id.imageView);
        final TextView ete = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String message = intent.getStringExtra("a");
        mode = intent.getStringExtra("b");
        game_lives_m = Integer.parseInt(message);

        if (mode.equals("play")){
            ete.setText("Take "+game_lives_m+" pictures of random objects.");
        }else if (mode.equals("learn")){
            ete.setText("Result");
        }else if (mode.equals("assist")){
            ete.setVisibility(View.INVISIBLE);
        }

        btn_c.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                ete.setVisibility(View.INVISIBLE);
            }
        });

        btn_s.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                EditText ed = (EditText) findViewById(R.id.editText);
                saved_image_name = ed.getText().toString();
                String a = save_image_toSD(bitmap,saved_image_name);
                image_path = a;
                Log.d(a,"done");
                try {
                    send();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//  AsyncTask for sending data to server and fetch
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
                msg += ";";
                image_names.add(msg);

                if (count >= game_lives_m && mode.equals("play")){
                    count = 0;
                    game_start(image_names.toString(), Integer.toString(game_lives_m));
                }
                else{
                    TextView et = (TextView) findViewById(R.id.textView);
                    et.setVisibility(View.VISIBLE);
                    if (mode.equals("play")){
                        et.setText("Take "+(game_lives_m-count)+" more.");
                        Log.d("m ", msg);
                    }else if (mode.equals("learn")) {
                        et.setClickable(true);
                        et.setMovementMethod(LinkMovementMethod.getInstance());
                        String text = "<a href='http://www.google.com/images?q="+msg.substring(0,msg.length()-1)+"'> "+msg.substring(0,msg.length()-1)+" </a>";
                        if (Build.VERSION.SDK_INT >= 24)et.setText( (Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY)));
                        else et.setText( (Html.fromHtml(text)));
                    }else {
                        EditText ed = (EditText) findViewById(R.id.editText);
                        ed.setVisibility(View.GONE);
                        btn_s.setVisibility(View.GONE);
//                  Deleting images taken for labelling
                        new File(image_path+"/"+saved_image_name+".png").delete();

                    }

                    btn_c.setVisibility(View.VISIBLE);
                    btn_c.setClickable(true);
                }

                msg = "";

            }
        }.execute(1);

    }

//    Sending data to ssh server and fetching result
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
            if (!mode.equals("assist"))c.put(image_path+"/"+saved_image_name+".png", "/home/"+user+"/PycharmProjects/Object_detection");
            else c.put(image_path+"/"+saved_image_name+".png", "/home/"+user+"/PycharmProjects/Object_detection/label");
            c.exit();
            channel.disconnect();

            if(!mode.equals("assist")){
                Thread.sleep(4000); //pause for 4 seconds

                ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
                channel2.setCommand("cd /home/"+user+"/PycharmProjects/Object_detection\n" + "cat a.txt");
//            channel2.setErrStream(System.err);
                channel2.setInputStream(null);
                InputStream in = channel2.getInputStream();
                channel2.connect();
                Log.d("Success on 2nd channel", " :)");

                byte[] tmp = new byte[1024];

                while (in.read() != -1)
                {
                    int i = in.read(tmp, 0, 1024);
                    Log.d(""+in.read(),""+i);

                    m += new String(tmp, 0, i);
                    Log.d("Object detected",m);
                }

                in.close();
                channel2.disconnect();

            }
            session.disconnect();
        }
        catch(JSchException e) {
            // show the error in the UI
            Log.d("internet connection", " not found :(");
//            Toast.makeText(getApplicationContext(),"Check your internet connection.",Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(m);
        return m;
    }

//  converting key file to bytearray fo accessing server with keys
    public ByteArrayOutputStream read_key(){
        InputStream privateKeyByteStream = getResources().openRawResource(R.raw.semion);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
        int i;
        try
        {
            i = privateKeyByteStream.read ();
            while (i != -1 )
            {
                byteArrayOutputStream.write (i);
                i = privateKeyByteStream.read ();
            }
            privateKeyByteStream.close ();
        }
        catch (IOException e)
        {
            e.printStackTrace ();
        }

        return byteArrayOutputStream;
    }

//  capturing image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        count ++;
        btn_c.setVisibility(View.INVISIBLE);
        btn_c.setClickable(false);
        if (data != null){
            bitmap = (Bitmap)data.getExtras().get("data");
            camera.setImageBitmap(bitmap);

            try {
                if (!mode.equals("assist")) {
                    saved_image_name = "sampleimage";
                    String a =save_image_toSD(bitmap,saved_image_name);
                    image_path = a;
                    Log.d(a,"done");
                    send();
                }
                else{
                    final EditText ed = (EditText) findViewById(R.id.editText);
                    ed.setVisibility(View.VISIBLE);
                    btn_s.setVisibility(View.VISIBLE);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

//  saving image
    private String save_image_toSD(Bitmap b, String im_name){
        OutputStream output;
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Log.d("a",filepath.getAbsolutePath());
        File dir = new File(filepath.getAbsolutePath() + "/Server Image folder");
        dir.mkdir();
        File file = new File(dir, im_name+".png");

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

//    start game
    private void game_start(String msg, String li){
        Intent game_activity = new Intent(getApplicationContext(),GameActivity.class);
        game_activity.putExtra("a",msg);
        game_activity.putExtra("b",li);
        image_names.clear();
        startActivity(game_activity);
    }
}

