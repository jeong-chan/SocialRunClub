package com.example.registerloginexample;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragMonday.OnTimePickerSetListener {
    private Button mStartBtn, mStopBtn, mPauseBtn;
    public static TextView mTimeTextView,mSpeedTextView,mDistanceView,mKcalView;
    private TextView runtime, runDis, runkcal;
    public static int mSec,Sec,min,hour;
    private Thread timeThread = null;
    private static Boolean isRunning = false;
    private GoogleMap snapmap;
    public String location_lat, location_long, skcal, sdistance, timer, result_time;
    private ArrayList<String> login_latitude = new ArrayList<String>();
    private ArrayList<String> login_longitude = new ArrayList<String>();
    public static ArrayList<LatLng> login_latlng = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    //private String mJsonString;
    private String location_ID = LoginActivity.user_db.getMember_id();// ?????? ???????????? ???????????? ID
    private Date date = new Date();
    SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd");
    private String run_date = datetime.format(date);

    @Override
    public void onClick(View view) {

    }

    public static Boolean getIsRunning() {
        return isRunning;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //???????????? ??????
        ViewPager viewPager = findViewById(R.id.viewPager);
        VPAdapter fragmentPagerAdapter = new VPAdapter(getSupportFragmentManager());
        FragMonday fragMonday = new FragMonday();
        FragTuesday fragTuesday = new FragTuesday();
        FragWendnesday fragWendnesday = new FragWendnesday();
        fragmentPagerAdapter.addItem(fragMonday);
        fragmentPagerAdapter.addItem(fragTuesday);
        fragmentPagerAdapter.addItem(fragWendnesday);

        //?????? ????????? ??????
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //????????? ????????????//
        //????????? ????????? ????????????
        getSupportActionBar().setTitle("???-???");
        //????????? ????????? ??????
        //????????? ??????
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //????????? ?????????
        //hideActionBar();

        //?????????
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#4ea1d3"));
        }

        mStartBtn = (Button) findViewById(R.id.btn_runStart);
        mStopBtn = (Button) findViewById(R.id.btn_runStop);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mTimeTextView = (TextView) findViewById(R.id.timeView);
        mSpeedTextView = (TextView) findViewById(R.id.speedView);
        mDistanceView = (TextView)findViewById(R.id.distanceView);
        mKcalView = (TextView)findViewById(R.id.kcalView);

        //??????????????? ?????? ??????
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteLocation();
                isRunning = true;
                v.setVisibility(View.GONE);
                mStopBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.VISIBLE);

                timeThread = new Thread(new timeThread());
                timeThread.start();

            }
        });

        //???????????? ????????? ????????????
        mStopBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isRunning = false;
                login_latlng.clear();
                for (int i= 0; i<login_longitude.size(); i++) {
                    login_latlng.add(new LatLng(Double.parseDouble(login_latitude.get(i)), Double.parseDouble(login_longitude.get(i))));
                }
                saveRunData();
                v.setVisibility(View.GONE);
                mStartBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.GONE);
                timeThread.interrupt();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("??????")
                        .setMessage("?????????????????????????")
                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(),"??????",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("???", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                                    @Override
                                    public void onSnapshotReady(Bitmap snapshot) {
                                        WritePostActivity.imageView1_test.setImageBitmap(snapshot);
                                        WritePostActivity.result_distance_view.setText(fragMonday.nowdistance+"km");
                                        WritePostActivity.result_kcal_view.setText(fragMonday.nowkcal+"kcal");
                                        WritePostActivity.result_time_view.setText(result_time);
                                    }
                                };

                                snapmap = fragMonday.getmMap();

                                PolylineOptions rectOptions = new PolylineOptions();
                                LatLngBounds.Builder FitZoom = new LatLngBounds.Builder();
                                for(int j=0;j<login_latlng.size();j++) {
                                    rectOptions.add(login_latlng.get(j));
                                    FitZoom.include(login_latlng.get(j));
                                }

                                Polyline polyline = snapmap.addPolyline(rectOptions);
                                LatLngBounds FitZoomBound = FitZoom.build();
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(FitZoomBound, 50);
                                snapmap.moveCamera(cu);
                                snapmap.snapshot(callback);

                                Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();

                fragMonday.MapClear();
                fragMonday.setSpeed(0);
                fragMonday.setDistance(0);
                fragMonday.setKcal(0);
                mSpeedTextView.setText("0.00km/h");
                mDistanceView.setText("0.00km");
                mKcalView.setText("0.00Kcal");



            }
        });

        //?????????????????? ????????? ????????????
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    mPauseBtn.setText("PAUSE");
                } else {
                    mPauseBtn.setText("START");
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSec = msg.arg1 % 100;
            Sec = (msg.arg1 / 100) % 60;
            min = (msg.arg1 / 100) / 60;
            hour = (msg.arg1 / 100) / 3600;
            timer = Integer.toString(msg.arg1);
            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, Sec, mSec);
            mTimeTextView.setText(result);
            result_time = result;
        }
    };

    public void onTimePickerSet(String latitude, String longitude, Double kcal, Double distance) {
        DecimalFormat form = new DecimalFormat("#.##");
        location_lat = latitude;
        location_long = longitude;
        System.out.println("?????????!!!!!!!!!!!!  " + longitude);
        skcal = Double.toString(Double.parseDouble(form.format(kcal)));
        sdistance = Double.toString(Double.parseDouble(form.format(distance / 1000)));
        login_latitude.add(latitude);
        login_longitude.add(longitude);
        saveLocation();
    }


    // ???????????? ???????????? ?????? ??????
    public void processDatePickerResult(String dateMessage) {
        EditText et_start = null ,et_end=null;


        // ????????? ?????? ????????? ?????????
        dates.add(dateMessage);
        System.out.println(dates.size());
        if(dates.size()==1) {
            et_start = findViewById(R.id.start_date);
            et_start.setText("?????????:  "+dateMessage);
        }else{
            et_end =findViewById(R.id.end_date);
            et_end.setText("?????????:  "+dateMessage);
        }
        if(dates.size()>1){
            //System.out.println(dates);
            statistic(dates);
            dates.clear();
        }

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();

    }
    // ????????? ????????? ?????? ???????????? ???????????? ????????????
    public void statistic(ArrayList<String> dates) {
        int rTime = 0,compare1, compare2;
        double rDis = 0.0, rKcal = 0.0;
        int sta_mSec, sta_Sec, sta_min, sta_hour;
        int istart = 0, iend=0, size;
        Date start_Day=null, end_Day=null;
        Date dbDay=null;
        size = LoginActivity.run_db.getRun_date().size();
        runtime = findViewById(R.id.timedata);
        runDis = findViewById(R.id.disdata);
        runkcal = findViewById(R.id.kcaldata);
        //System.out.println("?????? ????????? ???"+size);

        String start = dates.get(0);
        String end = dates.get(1);
        // System.out.println("?????? "+start+"???  "+end);

        //????????????????????? ????????? ????????? ???????????? ????????? ?????? ??????
        try {
            end_Day =datetime.parse(end);
            start_Day = datetime.parse(start);
            // System.out.println(end_Day +"???  "+start_Day);
            for(int k=0;k<size;k++){
                if(LoginActivity.run_db.getRun_date().get(k)!= "null") {
                    dbDay = datetime.parse(LoginActivity.run_db.getRun_date().get(k));
                    // System.out.println("???????????????????????? ?????? ?????? ??????: "+dbDay);
                    compare1 = start_Day.compareTo(dbDay);
                    compare2 = end_Day.compareTo(dbDay);
                    if(compare1<0 && compare2<0 ) {
                        istart = -1;
                        Toast.makeText(this, "????????? ?????? ?????????????????????. ", Toast.LENGTH_SHORT).show();
                        runtime.setText(null);
                        runDis.setText(null);
                        runkcal.setText(null);

                        break;
                    } else if(compare1>0)
                    {
                        //System.out.println("??????");
                        continue;

                    } else{
                        start= datetime.format(dbDay);
                        //System.out.println("????????? ??????:  "+start);
                        break;
                    }

                }
            }
            for(int k=size-1; k>-1;k--){
                dbDay=datetime.parse(LoginActivity.run_db.getRun_date().get(k));
                compare2= end_Day.compareTo(dbDay);
                if(compare2<0){
                    continue;
                }else{
                    end=datetime.format(dbDay);

                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        /// ????????? ????????? ????????? ??????
        for (int j = 0; j < size; j++) {
            if (start.equals(LoginActivity.run_db.getRun_date().get(j))) {
                istart = j;
                System.out.println("????????? ??????" + istart);
                break;
            }
        }
        for (int j = size-1; j > 0; j--) {
            if (end.equals(LoginActivity.run_db.getRun_date().get(j))) {
                iend = j;
                System.out.println("????????? ???"+iend);
                break;
            }
        }
        if(istart>=0) {
            // ????????? ????????? ???????????? ??? ????????????
            if (istart == iend) {
                for(;istart<size;istart++){

                    if(LoginActivity.run_db.getRun_date().get(istart).equals(LoginActivity.run_db.getRun_date().get(iend))) {
                        rTime += Integer.parseInt(LoginActivity.run_db.getRun_time().get(istart));
                        rDis += Double.parseDouble(LoginActivity.run_db.getRun_distance().get(istart));
                        rKcal += Double.parseDouble(LoginActivity.run_db.getRun_kcal().get(istart));
                    }

                if(LoginActivity.run_db.getRun_date().get(istart).equals(LoginActivity.run_db.getRun_date().get(iend))) {
                    rTime += Integer.parseInt(LoginActivity.run_db.getRun_time().get(istart));
                    rDis += Double.parseDouble(LoginActivity.run_db.getRun_distance().get(istart));
                    rKcal += Double.parseDouble(LoginActivity.run_db.getRun_kcal().get(istart));
                }

                }
            } else {
                for (; istart < iend; istart++) {
                    rTime += Integer.parseInt(LoginActivity.run_db.getRun_time().get(istart));
                    rDis += Double.parseDouble(LoginActivity.run_db.getRun_distance().get(istart));
                    rKcal += Double.parseDouble(LoginActivity.run_db.getRun_kcal().get(istart));

                    //System.out.println("????????? " + rTime + "  " + rDis + "  " + rKcal);
                }
            }

            sta_mSec = rTime % 100;
            sta_Sec = (rTime / 100) % 60;
            sta_min = (rTime / 100) / 60;
            sta_hour = (rTime / 100) / 3600;

            rDis = Double.parseDouble(String.format("%.3f", rDis));
            rKcal = Double.parseDouble(String.format("%.3f", rKcal));

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", sta_hour, sta_min, sta_Sec, sta_mSec);


            runtime.setText( result);
            runDis.setText(Double.toString(rDis) + " km");
            runkcal.setText(Double.toString(rKcal) + " kcal");
        }


    }


    //????????? ??????
    public class timeThread implements Runnable{
        @Override
        public void run() {
            int i = 0;

            while(true){
                while (isRunning) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try{
                        Thread.sleep(10);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                mTimeTextView.setText("");
                                mTimeTextView.setText("00:00:00:00");
                            }
                        });
                        return;
                    }


                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //??????????????? ?????????????????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        System.out.println("main??????");


        if (id == android.R.id.home) {
            Toast.makeText(this, "???????????? ??????", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            System.out.println("???????????? ??????");
            startActivity(intent);
            return true;
        }

        if(id==R.id.action_btn1){
            toast.setText("????????? ??????");
            System.out.println("????????? ??????");
            Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
            startActivity(intent);

            return true;
        }

        toast.show();
        return super.onOptionsItemSelected(item);
    }

    //???????????? ????????? ????????????.
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }


    //????????? ?????????
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    //???????????? ?????? ?????? DB??? ??????
    public void saveLocation() {
        if (isRunning == true) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {//??????????????? ????????? ??????
                            System.out.println("??????!!!!!!!!!!!!!!!!!!!");

                        } else {//??????????????? ????????? ??????
                            System.out.println("??????.............");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            {
                //????????? Volley??? ???????????? ????????? ???
                InsertLocaion insertLocaion = new InsertLocaion(location_ID, location_lat, location_long, run_date, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(insertLocaion);
            }
        } else {
            System.out.println("????????? ?????? ????????????.");
        }

    }
    //???????????? ?????? ????????? ????????? ????????? DB?????? ?????? ?????? ??????
    public void deleteLocation() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {//??????????????? ????????? ??????
                        System.out.println("????????????!!!!!!!!!!!!!!!!!!!");

                    } else {//??????????????? ????????? ??????
                        System.out.println("????????????.............");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        {
            //????????? Volley??? ???????????? ????????? ???
            DeleteLocation deleteLocation = new DeleteLocation(location_ID, run_date, responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(deleteLocation);
        }

    }

    public void saveRunData() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {//??????????????? ????????? ??????
                        System.out.println("??????2!!!!!!!!!!!!!!!!!!!");

                    } else {//??????????????? ????????? ??????
                        System.out.println("??????2.............");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //????????? Volley??? ???????????? ????????? ???
        InsertRunData insertRunData = new InsertRunData(location_ID, timer, sdistance, skcal, run_date, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(insertRunData);

    }

}




