package com.example.registerloginexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WritePostActivity extends MainActivity {

    public static ImageView imageView1_test;
    public static TextView result_distance_view, result_time_view, result_kcal_view;
    public static EditText Title_view, RunningPlace_view, Comment_view = null;
    private String imgName = "osz.png";
    public static FirebaseStorage mFireStorage;
    public static StorageReference storageRef;
    public static FirebaseDatabase database;
    public static DatabaseReference databaseRef;

    public static TextView getResult_distance_view() {
        return result_distance_view;
    }

    public static void setResult_distance_view(TextView result_distance_view) {
        WritePostActivity.result_distance_view = result_distance_view;
    }

    public static TextView getResult_time_view() {
        return result_time_view;
    }

    public static void setResult_time_view(TextView result_time_view) {
        WritePostActivity.result_time_view = result_time_view;
    }

    public static TextView getResult_kcal_view() {
        return result_kcal_view;
    }

    public static void setResult_kcal_view(TextView result_kcal_view) {
        WritePostActivity.result_kcal_view = result_kcal_view;
    }

    private ArrayAdapter<CharSequence> adapter_city, adapter_sigungu;
    String choice_city = "";
    String choice_sigungu = "";

    public static ImageView getImageView1_test() {
        return imageView1_test;
    }

    public void replaceFragment(FragTuesday fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public static void setImageView1_test(ImageView imageView1_tests) {
        imageView1_test = imageView1_tests;
    }

    //???????????? ????????? ??????
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        initFirestore();

        imageView1_test = (ImageView) findViewById(R.id.imageView);
        result_time_view = (TextView) findViewById(R.id.post_time);
        result_kcal_view = (TextView) findViewById(R.id.post_kcal);
        result_distance_view = (TextView) findViewById(R.id.post_distance);
        Title_view = (EditText) findViewById(R.id.edit_Title);
        Comment_view = (EditText) findViewById(R.id.edit_Comment);
        RunningPlace_view = (EditText) findViewById(R.id.runnigplace);
        findViewById(R.id.check_button).setOnClickListener(onClickListener);
        findViewById(R.id.cancel_button).setOnClickListener(onClickListener);
        final Spinner spinnerCity = (Spinner) findViewById(R.id.spinner_city);
        final Spinner spinnerSigungu = (Spinner) findViewById(R.id.spinner_sigungu);

        try{
            String imagepath = getCacheDir() + "/" + imgName;
            Bitmap bm = BitmapFactory.decodeFile(imagepath);
            imageView1_test.setImageBitmap(bm);
        } catch (Exception e){
        }

        //????????? ??????
        adapter_city = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region,
                                                        android.R.layout.simple_spinner_dropdown_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter_city);
        //Spinner ??????
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_seoul,
                                                                        android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_busan, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_daegu, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_incheon, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_gwangju, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_daejeon, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("???????????????")) {
                    choice_city = "???????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_ulsan, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("?????????????????????")) {
                    choice_city = "?????????????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_sejong, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("?????????")) {
                    choice_city = "?????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_gyeonggi, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("?????????")) {
                    choice_city = "?????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_gangwon, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_chung_buk, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_chung_nam, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_jeon_buk, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_jeon_nam, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_gyeong_buk, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("????????????")) {
                    choice_city = "????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_gyeong_nam, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }else if (adapter_city.getItem(position).equals("?????????????????????")) {
                    choice_city = "?????????????????????";
                    adapter_sigungu = ArrayAdapter.createFromResource(WritePostActivity.this, R.array.spinner_region_jeju, android.R.layout.simple_spinner_dropdown_item);
                    adapter_sigungu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSigungu.setAdapter(adapter_sigungu);
                    spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            choice_sigungu = adapter_sigungu.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView1) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView1) {
            }
        });
    }

    //?????? ??? ???????????? ??????
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean loop = true;
            final Intent intent = new Intent(WritePostActivity.this, MainActivity.class);
            switch (v.getId()) {
                case R.id.check_button:
                    if (choice_city.equals("") || Title_view.getText().toString().equals("") || RunningPlace_view.getText().toString().equals("") || Comment_view.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        create_and_Delete(storageRef);
                        //data_create_and_delete();
                        data_create_and_delete();
                        CheckPostActivity.shared_finish = 0;
                        startActivity(intent);
                        loop = false;
                        break;
                    }
                    break;
                case R.id.cancel_button:
                    startActivity(intent);
                    break;
            }
            }
    };
    @Override
    public void onPause(){
        super.onPause();
        finish();
    }

    //Firebase ??????
    public void initFirestore(){
        mFireStorage = FirebaseStorage.getInstance();
        storageRef = mFireStorage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
    }

    //???????????? ???????????? ??????
    public File saveBitmapToJpeg(Bitmap bitmap){
        File tempFile = new File(getCacheDir(), imgName);
        try{
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
        }
        return tempFile;
    }

    //ImageView??? image???  Bitmap?????? ??????
    public Bitmap getBitmapFromView(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //Firebase Storage??? ????????? ????????? ??????????????? ?????? ID?????? ??? ?????? ????????? ?????? ????????? ???????????? ????????? ?????? ??????
    public void create_and_Delete(StorageReference ref){
        Bitmap tmp_map = getBitmapFromView(imageView1_test);
        File tmp_file = saveBitmapToJpeg(tmp_map);
        Uri uri = Uri.fromFile(tmp_file);

        UploadTask uploadTask = ref.child(LoginActivity.user_db.getMember_id()+"_map_images").putFile(uri);

        StorageReference next_ref = ref.child(LoginActivity.user_db.getMember_id()+"map+images");

        next_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    //Firebase database??? ?????? ???????????? ?????? ID?????? ??? ?????? ????????? ?????? ????????? ?????? ????????? ?????? ??????
    public void data_create_and_delete(){
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("id").setValue(LoginActivity.user_db.getMember_id());

        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("status").child("Distance").setValue(result_distance_view.getText());
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("status").child("Time").setValue(result_time_view.getText());
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("status").child("Kcal").setValue(result_kcal_view.getText());

        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("Title").setValue(Title_view.getText().toString());
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("Comment").setValue(Comment_view.getText().toString());
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("RunningPlace").setValue(RunningPlace_view.getText().toString());

        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("City").setValue(choice_city);
        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("Sigungu").setValue(choice_sigungu);

        databaseRef.child("Track").child(LoginActivity.user_db.getMember_id()).child("LatLng").setValue(MainActivity.login_latlng.toString());
        }


}