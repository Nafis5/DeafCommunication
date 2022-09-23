
package com.pack1.deafcommunication;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText textView;
    private ActionBarDrawerToggle abdt;
    private DrawerLayout dl;
    // private int spinnerindex=25;
    private String langpref = "en-US";
    private Spinner spinner;
    private TextView langview;
    SharedPreferences sharedPreferences;
    String data = "English (United States)";
    int counter = 0;
    boolean secondbar = false;
    SharedPreferences s2;
    private InterstitialAd mInterstitialAd;
     Button delete_button;
     Button speaker_button;
    //for Ad showing logic
    String URL= "https://contrapuntal-supers.000webhostapp.com/adShow.json";
    int x=5;
    private static final  int TTS=101;
    private  TextToSpeech textToSpeech;
    String continuedText="";
    Intent intent_speaker;
    boolean isListening=false;
    private SpeechRecognizer sr;
    private int Record_Audio_Permmision_Code=1;

    private Toolbar toolbar;
    private AdView mAdView;
    private ImageView mic_btn;
    String interstialAdUnit="ca-app-pub-3103198316569371/4724217334";







    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //khela hobe

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.CLose);
        textView = findViewById(R.id.txtSpeechInput);

        mic_btn=findViewById(R.id.btnSpeak);





        delete_button=findViewById(R.id.delete);
        speaker_button=findViewById(R.id.speaker2);

        //banner ad intiliaze
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
       mAdView = findViewById(R.id.adView);
       AdRequest adRequestBanner = new AdRequest.Builder().build();
       mAdView.loadAd(adRequestBanner);
       loadInterstial();




        //text to speech
        speaker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                texttospeech();

            }
        });
        textToSpeech= new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
               if(status!=TextToSpeech.ERROR) textToSpeech.setLanguage(new Locale(langpref));
               else{
                   Toast.makeText(MainActivity.this, "Your device does not support this language", Toast.LENGTH_SHORT).show();
               }
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                continuedText="";

            }
        });



        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());


        textView.setMovementMethod(new ScrollingMovementMethod());

        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.langnames, R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);

        //spinner stuffs start
        spinner.setAdapter(adapter);

        backuptextsize();
        spinner.setOnItemSelectedListener(this);


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                s2 = getSharedPreferences("userdetails2", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = s2.edit();
               if(id==R.id.Offline_mode) {
                 Intent i=new Intent(MainActivity.this,Lang.class);
                 startActivity(i);
                   try{
                       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pro.deafcommunication")));
                       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?=com.pro.deafcommunication")));
                   }catch(android.content.ActivityNotFoundException e){
                       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pro.deafcommunication")));

                   }

                }

                if(id==R.id.remove_ads){
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pro.deafcommunication")));
                       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?=com.pro.deafcommunication")));
                    }catch(android.content.ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pro.deafcommunication")));

                    }


                }

                if (id == R.id.ExtraLarge) {
                    editorr.putInt("textsize", 15);
                    textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));

                }

                if (id == R.id.ExtraSmall) {
                    editorr.putInt("textsize", 7);
                    textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));

                }

                if (id == R.id.Large) {
                    editorr.putInt("textsize", 13);
                    textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics()));


                }
                if (id == R.id.Normal) {

                    editorr.putInt("textsize", 11);
                    textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics()));


                }
                if (id == R.id.Small) {
                    editorr.putInt("textsize", 9);
                    textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics()));


                }
                editorr.commit();


                return true;
            }
        });
      //spinner stuff ends
        checkPermission();

    }

    public void loadInterstial(){
        AdRequest adRequest = new AdRequest.Builder().build();
        // mInterstitialAd.setAdUnitId("ca-app-pub-3103198316569371/4724217334");
        InterstitialAd.load(this, interstialAdUnit, adRequest, new InterstitialAdLoadCallback() {

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd=null;
                mAdView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd=null;
                        mAdView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mInterstitialAd=null;
                        mAdView.setVisibility(View.VISIBLE);

                    }
                });
            }
        });



    }

    private void showInterstial(){
        if(mInterstitialAd!=null){

            mAdView.setVisibility(View.GONE);
            mInterstitialAd.show(this);
        }
        else{
            loadInterstial();
        }

    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            new AlertDialog.Builder(this)
                    .setTitle("Record Audio permission needed")
                    .setMessage("This permission is needed to record speech and convert it to text otherwise speech to text will not work")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.RECORD_AUDIO}, Record_Audio_Permmision_Code);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, Record_Audio_Permmision_Code);
        }
    }

    class listener implements RecognitionListener
    {
        public Object MainActivity;

        public void onReadyForSpeech(Bundle params)
        {

        }
        public void onBeginningOfSpeech()
        {

        }
        public void onRmsChanged(float rmsdB)
        {

        }
        public void onBufferReceived(byte[] buffer)
        {

        }
        public void onEndOfSpeech()
        {

        }
        public void onError(int error)
        {
            if(isListening) sr.startListening(intent_speaker);



        }

        public void onResults(Bundle results)
        {
            String str = new String();

            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {

                str=""+data.get(i);
            }
            continuedText=continuedText+" "+str;
            textView.setText(continuedText);
            if(isListening)  sr.startListening(intent_speaker);


        }
        public void onPartialResults(Bundle partialResults)
        {

        }
        public void onEvent(int eventType, Bundle params)
        {

        }
    }
//speech to text
    public void getSpeechInput(View view) {
            checkPermission();
        if (isListening) {
            isListening = false;

            mic_btn.setImageResource(R.drawable.mic_new_off);
            sr.stopListening();

        } else {
            isListening = true;

            mic_btn.setImageResource(R.drawable.mic_new_on);
        }
        if (isListening) {
            start();
        }
    }



    //speech to text method
    public void start(){
        intent_speaker = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent_speaker.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent_speaker.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langpref);
        sr.startListening(intent_speaker);
    }

    //spinner items

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        sharedPreferences = getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (counter > 0) {
            langpref = getResources().getStringArray(R.array.langcodes)[position];


            editor.putString("userlangkey", getResources().getStringArray(R.array.langcodes)[position]);
            editor.putString("userlangnamekey", getResources().getStringArray(R.array.langnames)[position]);
            editor.putBoolean("langset", true);
            editor.commit();
            setlang();

        } else {


            setlang();
        }


        counter++;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setlang() {
        sharedPreferences = getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        langpref = sharedPreferences.getString("userlangkey", "en-US");
        data = sharedPreferences.getString("userlangnamekey", "English (United States)");
        //langview.setText("Current Language:" + data);

    }


    public void backuptextsize() {

        s2 = getSharedPreferences("userdetails2", Context.MODE_PRIVATE);

            int a = s2.getInt("textsize", 11);

            textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, getResources().getDisplayMetrics()));
    }
    public void texttospeech(){
        try {
            int speechstatus = textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            if (speechstatus == TextToSpeech.ERROR)
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
        catch(NullPointerException e){
            Toast.makeText(MainActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
        }
        showInterstial();


    }
    public void copytoclipboard(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TextView", textView.getText().toString());
        clipboard.setPrimaryClip(clip);
        clip.getDescription();

        Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
        showInterstial();


    }
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {


        } else {
            requestStoragePermission();
        }

    }







}


