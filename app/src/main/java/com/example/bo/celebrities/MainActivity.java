package com.example.bo.celebrities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;

public class MainActivity extends AppCompatActivity {
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer sb = new StringBuffer();
                String HTML = null;
                while ((HTML = bufferedReader.readLine())!= null){

                    sb.append(HTML);
                }
                return  sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
   }

   public  class DownloadImage extends  AsyncTask<String, Void, Bitmap>{

       @Override
       protected Bitmap doInBackground(String... strings) {
           try {
               URL url = new URL(strings[0]);
               URLConnection connection = url.openConnection();
               InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return  bitmap;
           } catch (Exception e) {
               e.printStackTrace();
           }
           return null;
       }

    }
    public  void click(View view){
        Button button = (Button)view;
        if(button.getText().toString().equals(names.get(randomIndex))){
            Toast.makeText(MainActivity.this,"Correct",Toast.LENGTH_SHORT).show();
            showQuestion();
        }
        else{
            Toast.makeText(MainActivity.this,"Wrong",Toast.LENGTH_SHORT).show();
            showQuestion();
        }

    }

    public void showQuestion() {
        randomIndex = new Random().nextInt(names.size());
        try {
            Bitmap downloadImage = new DownloadImage().execute(Urls.get(randomIndex)).get();
            imageView.setImageBitmap(downloadImage);
            Integer randomPosition = new Random().nextInt(4);
            for (int i = 0; i < 4; i++) {
                if (i == randomPosition) {
                    answers[i] = names.get(randomIndex);
                } else {
                    Integer randomAnswer = new Random().nextInt(names.size());
                    while (randomAnswer == randomIndex) {
                        randomAnswer = new Random().nextInt(names.size());
                    }
                    answers[i] = names.get(randomAnswer);
                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        } catch (ExecutionException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    }

    Bitmap bitmap;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> Urls = new ArrayList<>();
    ImageView imageView;
    Integer randomIndex;
    //Correct answer position
    Integer randomPosition;
    String[] answers = new String[4];
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask downloadTask = new DownloadTask();
        imageView = (ImageView) findViewById(R.id.imageView);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);
        try {
            String result = downloadTask.execute("https://www.imdb.com/list/ls000034489/").get();
            String[] strings = result.split("lister-list");
            result = strings[1];
            strings = result.split("lister-working hidden");
            result = strings[0];
            result = result.replace("\r\n", " ").replace("\n", " ");
            Pattern p = Pattern.compile("src=\"(.*?)\"width");
            Matcher m = p.matcher(result);

            while(m.find()){
                Urls.add(m.group(1));
            }

            p = Pattern.compile("ref_=nmls_hd\">(.*?)</a>");
            m = p.matcher(result);
            Log.e("result", result);
            while ((m.find())){
                names.add(m.group(1));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        showQuestion();
    }
}
