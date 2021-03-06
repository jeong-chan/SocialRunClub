package com.example.registerloginexample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class    FragMonday extends Fragment implements OnMapReadyCallback {
    private FragmentActivity mContext;

    private static final String TAG = FragMonday.class.getSimpleName();
    private static GoogleMap mMap;
    private MapView mapView;
    private Marker currentMarker;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location mCurrentLocation;
    private Location location;
    private LocationManager locationmanager;
    private final LatLng mDefaultLocation = new LatLng(35.24160126808495, 128.69574364205644);
    private static final int DEFAULT_ZOOM=15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    public static String nowkcal, nowdistance, nowtime;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000*5*1;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000*1;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    List<Polyline>polylines = new ArrayList<>();
    private double speed = 0.0;
    private double distance = 0.0;
    private double kcal = 0.0;
    private double timeforkcal = -1;
    public static String slatitude, slongitude ;
    double delay;
    public static Polyline sharedline;

    public FragMonday(){
    }

    public interface OnTimePickerSetListener{
        void onTimePickerSet(String latitude, String longitude, Double kcal, Double distance );
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public void setKcal(double kcal){
        this.kcal = kcal;
    }

    //?????????????????? ??????????????? attach??? ??? ??????
    @Override
    public void onAttach(Activity activity) {
        mContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    //?????????????????? ??????????????? attach??? ??? ??????
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onTimePickerSetListener = (OnTimePickerSetListener) context;
    }

    public  void onDetach(){
        super.onDetach();
        onTimePickerSetListener=null;
    }

    public static FragMonday newInstance() {
        FragMonday fragMonday = new FragMonday();
        return fragMonday;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //??? ????????? ??????
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if(savedInstanceState != null ){
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        View layout= inflater.inflate(R.layout.frag_monday, container, false);

        mapView = (MapView) layout.findViewById(R.id.map);
        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.onResume();
        mapView.getMapAsync(this);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //??????????????? ?????? ????????? ??? ???????????? ??????
        MapsInitializer.initialize(mContext);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // ???????????? ?????????????????? ??????
                .setInterval(UPDATE_INTERVAL_MS) // ????????? Update ?????? ??????
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // ?????? ????????? ?????????????????? ??????

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient ?????? ??????
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

    }
    //?????? ?????????
    public static void MapClear(){
        mMap.clear();
    }

    //????????? ???????????? ??????
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setDefaultLocation();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        drawsharedpolyline();
    }


    private void drawsharedpolyline(){

        if(CheckPostActivity.getsharedlatlng() == 1){
            PolylineOptions rectOptions = new PolylineOptions();
            rectOptions.color(Color.RED);
            LatLngBounds.Builder FitZoom = new LatLngBounds.Builder();
            for(int j=0;j<CheckPostActivity.shared_latlng.size();j++) {
                rectOptions.add(CheckPostActivity.shared_latlng.get(j));
                FitZoom.include(CheckPostActivity.shared_latlng.get(j));
            }
            LatLngBounds FitZoomBound = FitZoom.build();
            sharedline = mMap.addPolyline(rectOptions);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(FitZoomBound, 200);
            mMap.moveCamera(cu);

        }
    }
    //??? ?????? ????????? ??? ?????? ????????? ??????
    private void updateLocationUI(){
        if(mMap == null){
            return;
        }
        try{
            if(mLocationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocation = null;
                getLocationPermission();
            }
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //??????????????? ???????????? ?????? ????????? ???????????? ????????? ??????
    private void setDefaultLocation(){
        if(currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("???????????? ????????? ??? ??????");
        markerOptions.snippet("?????? ???????????? GPS?????? ????????? ???????????????");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mMap.moveCamera(cameraUpdate);
    }

    String getCurrentAddress(LatLng latlng) {
        // ?????? ????????? ?????????????????? ?????? ???????????? ?????????.
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( mContext, Locale.getDefault());

        // ??????????????? ???????????? ?????? ???????????? ?????????.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
        } catch (IOException e) {
            Toast. makeText( mContext, "??????????????? ????????? ????????? ??? ????????????. ??????????????? ???????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "?????? ?????? ??????" ;
        }

        if (addressList.size() < 1) { // ?????? ???????????? ??????????????? ?????? ?????????
            return "?????? ????????? ?????? ??????" ;
        }

        // ????????? ?????? ???????????? ???????????? ??????
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    private LatLng startLatLng = new LatLng(0,0);
    private LatLng endLatLng = new LatLng(0,0);
    private LatLng currentPosition = new LatLng(0,0);
    private OnTimePickerSetListener onTimePickerSetListener;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                if(startLatLng.latitude==0&&startLatLng.longitude==0){
                    startLatLng=new LatLng(latitude,longitude);
                }

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                //?????? ????????? ?????? ???????????? ??????
                setCurrentLocation(location, markerTitle, markerSnippet);
                endLatLng = new LatLng(latitude,longitude);
                if(timeforkcal == -1){
                    timeforkcal = location.getTime();
                }
                delay = (location.getTime() - timeforkcal)/60000;
                //timeforkcal = (location.getTime() - mCurrentLocation.getTime())/60000;

                //????????? ????????? ??? ??????
                if(MainActivity.getIsRunning() == true) {
                    getnowspeed();
                    getnowdistance();
                    getnowkcal();
                    drawPath();
                    howtoshared(location,CheckPostActivity.shared_latlng);}

                mCurrentLocation = location;
                startLatLng = new LatLng(latitude,longitude);


                slatitude = Double.toString(latitude);
                slongitude = Double.toString(longitude);
                System.out.println("?????????:"+slatitude+"????????? "+slongitude+"???????????? "+kcal);
                onTimePickerSetListener.onTimePickerSet(slatitude,slongitude,kcal,distance);

                //onTimePickerSetListener.onTimePickerSet(slatitude,slongitude,kcal,distance);
            }
        }

    };

    private void howtoshared(Location currentlocation, ArrayList<LatLng> sharedlocation){
        Boolean remove_yes = false;
        int msgcount = 0;
        if(sharedline != null){
            for(int i=0;i<sharedlocation.size();i++) {
                Location shared = new Location(LocationManager.GPS_PROVIDER);
                shared.setLatitude(sharedlocation.get(i).latitude);
                shared.setLongitude(sharedlocation.get(i).longitude);
                double distance = currentlocation.distanceTo(shared);
                if(distance >= 50){
                    remove_yes = true;
                }else{remove_yes = false;}
            }
        } else if(sharedline == null){
            remove_yes = false;
        }
        if(remove_yes == true) {
            if(sharedline != null){
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
                Toast. makeText( mContext, "???????????? ??????????????????.", Toast.LENGTH_SHORT ).show();}
            sharedline.remove();
            sharedline = null;
            remove_yes = false;
        }


    }

    //???????????? ?????? ????????????
    public void getnowdistance(){
        distance += mCurrentLocation.distanceTo(location); //mCurrnetLocation(????????????)?????? location(????????????)?????? ????????????
        DecimalFormat form = new DecimalFormat("#.##"); //????????? 2?????? ???????????? ??????
        nowdistance = Double.toString(Double.parseDouble(form.format(distance/1000))); //km??? ????????????
        MainActivity.mDistanceView.setText(nowdistance+"km"); //MainActivity??? DistanceView??? ???????????? setText
    }

    //?????? ????????????
    public void getnowspeed(){
        speed = location.getSpeed()*3.6; //getspeed()??? ????????? m/s????????? 3.6??? ????????? km/h??? ??????
        DecimalFormat form = new DecimalFormat("#.##"); //????????? 2?????? ???????????? ??????
        String nowspeed = Double.toString(Double.parseDouble(form.format(speed)));
        MainActivity.mSpeedTextView.setText(nowspeed+"Km/h"); //MainActivity??? SpeedView??? ???????????? setText
    }

    //???????????? ?????? Kcal
    public void getnowkcal(){
        double MET = 0; //??????????????? ?????? ?????????????????????
        if(speed<1){
            MET = 0;
        }else if(speed< 3.20){ //?????? ????????? ????????? ?????? = 2MET
            MET = 2;
        }else if(3.20<=speed && speed<=4.70){ //????????? ????????? ???????????? ?????? = 3MET
            MET = 3;
        }else if(4.70<speed && speed<=6.30){  //?????? ?????? ????????? ???????????? ?????? = 4MET
            MET = 4;
        }else if(6.30<speed && speed<8.00){   //?????? ????????? ???????????? ?????? = 5MET
            MET = 5;
        }else if(8.00<=speed && speed<=9.69){ //????????? ????????? ????????? ?????? = 10MET
            MET = 10;
        }else if(9.70<=speed){ //???????????? ????????? ????????? ????????? ?????? = 12MET
            MET = 12;
        }

        kcal += MET*(3.5*LoginActivity.user_db.getMember_weight()*(delay/60))/1000*5; //MET?????????????????????*(3.5ml*kg*time(hour))/1000*5(1ml=0.005kacl)
        DecimalFormat form = new DecimalFormat("#.##"); //????????? 2?????? ???????????? ??????
        nowkcal = Double.toString(Double.parseDouble(form.format(kcal)));
        MainActivity.mKcalView.setText(/*MET+"MET"+delay+"hour"+PersonalActivity.login_user_weight+"kg"+"\n"+*/nowkcal+"Kcal"); //MainActivity??? KcalView??? ??????????????? setText
    }

    //Polyline ??????
    public void drawPath(){
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(15).color(Color.BLACK).geodesic(true); //PolyLine ??????(??????????????? ????????????)
        polylines.add(mMap.addPolyline(options)); //????????? ????????? ???????????? ??????
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng,25)); //???????????????
    }

    private String CurrentTime(){
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) {currentMarker.remove();}

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);*/
        if(MainActivity.getIsRunning() == true) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
        }

    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public static GoogleMap getmMap(){
        return mMap;
    }

}




