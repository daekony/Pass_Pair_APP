package edu.nccu.mis.passpair.Post;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.nccu.mis.passpair.R;

public class PostUpload extends AppCompatActivity {
    private ImageView imgRecord, imgStop, imgPlay, imageShow;
    private TextView txtRec;
    private MediaPlayer mediaplayer;
    private MediaRecorder mediarecorder;
    private String recFileName, imageFileName,fileName; //以日期時間做暫存檔名
    private File recFile, imgFile, SdPath;
    private int timer = 15 ,count = 0,point=0;
    private Handler handler = new Handler();
    private Button upload;
    private Bitmap myBitmap;
    private byte[] mContent;

    private StorageReference mStorageRef_post;
    private DatabaseReference mRef_user = FirebaseDatabase.getInstance().getReference().child("User");

    private Uri imageUri, musicUri;
    private UploadTask uploadTask_image,uploadTask_music;

    private String UID;
    private String time_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        Bundle bundle = this.getIntent().getExtras();
        UID = bundle.getString("UID");

        imageShow  =(ImageView) findViewById(R.id.postupload_imageShow);
        imageShow.setOnClickListener(listener);
        upload = (Button) findViewById(R.id.postupload_upload);
        upload.setOnClickListener(uploadfile);
        imgRecord=(ImageView)findViewById(R.id.postupload_imgRecord);
        imgPlay=(ImageView)findViewById(R.id.postupload_imgPlay);
        imgStop=(ImageView)findViewById(R.id.postupload_imgStop);
        txtRec=(TextView)findViewById(R.id.postupload_txtRec);
        imgRecord.setOnClickListener(listener);
        imgPlay.setOnClickListener(listener);
        imgStop.setOnClickListener(listener);
        SdPath = Environment.getExternalStorageDirectory(); //SD卡路徑
        mediaplayer=new MediaPlayer();
        mStorageRef_post = FirebaseStorage.getInstance().getReference().child("Post");
        txtRec.setOnClickListener(test);

        // add a left arrow to back to parent activity,
        // no need to handle action selected event, this is handled by super
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private ImageView.OnClickListener listener=new ImageView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.postupload_imgRecord:  //錄音
                    switch (count){
                        case 0:
                            count = 1;
                            try {
                                Calendar calendar = new GregorianCalendar();
                                Date nowtime = new Date(); //取得現在日期及時間
                                calendar.setTime(nowtime);
                                recFileName ="R"+add0(calendar.get(Calendar.YEAR))+add0(calendar.get(Calendar.MONTH)+1)+add0(calendar.get(Calendar.DATE))+add0(calendar.get(Calendar.HOUR))+add0(calendar.get(Calendar.MINUTE))+add0(calendar.get(Calendar.SECOND));
                                recFile=new File(checkPath(SdPath + "/Music/PassPair/") + recFileName + ".mp3");
                                mediarecorder= new MediaRecorder();
                                mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                                mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                                mediarecorder.setOutputFile(recFile.getAbsolutePath());
                                mediarecorder.setMaxDuration(15000);
                                mediarecorder.prepare();
                                mediarecorder.start();
                                handler.post(updateTimer);
                                imgDisable(imgPlay);//處理按鈕是否可按
                                imgDisable(imgStop);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            count = 0;
                            if (mediaplayer.isPlaying()) { ////停止播放
                                mediaplayer.reset();
                                txtRec.setText("停止播放"+fileName);
                            } else if(recFile!=null) { //停止錄音
                                mediarecorder.stop();
                                mediarecorder.release();
                                mediarecorder=null;
                                parse(recFile);
                                txtRec.setText("停止錄音！");
                                getFileName();
                                timer = 15;
                                handler.removeCallbacks(updateTimer);
                            }
                            imgEnable(imgPlay);
                            imgEnable(imgStop);
                            break;
                    }
                    break;
                case R.id.postupload_imgPlay:  //播放
                    switch (count){
                        case 0:
                            count = 1;
                            if (musicUri!=null){
                                playSong(musicUri);
                            }else {
                                Toast.makeText(PostUpload.this,"尚未選取音檔",Toast.LENGTH_SHORT).show();
                                showAudioChooser();
                            }
                            break;
                        case 1:
                            count = 0;
                            if (mediaplayer.isPlaying()) { ////停止播放
                                mediaplayer.reset();
                                txtRec.setText("停止播放"+fileName);
                            }
                            break;
                    }
                    break;
                case R.id.postupload_imgStop:  //停止
                    if (mediaplayer.isPlaying()) { ////停止播放
                        mediaplayer.reset();
                        txtRec.setText("停止播放"+fileName);
                    }
                    showAudioChooser();
                    break;
                case R.id.postupload_imageShow:

                    final CharSequence[] items = { "相簿", "拍照" };

                    AlertDialog dlg = new AlertDialog.Builder(PostUpload.this).setTitle("選擇照片").setItems(items,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //這裡item是根據選擇的方式，   在items數據裡面定義了兩種方式，拍照的下標為1所以就調用拍照方法
                                    if(which==0){
                                        showImageChooser();
                                    }else{
                                        capture();
                                    }
                                }
                            }).create();
                    dlg.show();
                    break;
            }
        }
    };

    private Button.OnClickListener uploadfile = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.postupload_upload:
                    if (imageUri==null&&musicUri==null){
                        Toast.makeText(PostUpload.this,"尚未選取上傳檔案",Toast.LENGTH_LONG).show();
                    }
                    if (imageUri!=null){
                        if (imageUri.getPath().isEmpty()){txtRec.setText("nothing");}
                        uploadfile(imageUri);
                    }
                    if (musicUri!=null){
                        uploadfile(musicUri);
                    }
                    break;
            }
        }
    };

    private String checkPath(String path){
        File dirFile = new File(path);

        if (!dirFile.exists()){//如果資料夾不存在
            dirFile.mkdir();//建立資料夾
        }
        return path;
    }

    private void playSong(Uri path) {
        try
        {
            mediaplayer.reset();
            mediaplayer.setDataSource(PostUpload.this,path); //播放錄音路徑
            mediaplayer.prepare();
            mediaplayer.start(); //開始播放
            txtRec.setText("播放"+fileName+"中");
            imgEnable(imgStop);
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    txtRec.setText("播放完畢！");
                    imgEnable(imgRecord);
                }
            });
        } catch (IOException e) {}
    }

    private void imgEnable(ImageView image) { //使按鈕有效
        image.setEnabled(true);
        image.setAlpha(255);
    }

    private void imgDisable(ImageView image) { //使按鈕失能
        image.setEnabled(false);
        image.setAlpha(50);
    }

    protected String add0(int n) { //個位數前面補零
        if(n<10) return ("0" + n);
        else return ("" + n);
    }

    //固定要執行的方法
    private Runnable updateTimer = new Runnable() {
        public void run() {
            if(timer > 0) {
                //計算目前已過秒數
                txtRec.setText("錄音剩餘時間：" + timer);
                timer--;
                handler.postDelayed(this, 1000);
            }else{
                mediarecorder.stop();
                mediarecorder.release();
                parse(recFile);
                txtRec.setText("錄音完成！");
                getFileName();
                imgEnable(imgPlay);
                imgEnable(imgStop);
                handler.removeCallbacks(updateTimer);
            }
        }
    };

    private void capture(){
        Intent getImageByCamera  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        imageFileName = "I"+time;
        imgFile = new File(checkPath(SdPath + "/DCIM/PassPair/") + imageFileName + ".jpg");
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        imageUri = Uri.parse(Uri.fromFile(imgFile).toString());
        startActivityForResult(getImageByCamera, 1);
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    private void showAudioChooser(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), 2);
    }

    private void parse(File path){
        musicUri = Uri.parse(Uri.fromFile(path).toString());
    }

    private void getFileName(){
        File path = new File(musicUri.getPath());
        fileName = path.getName();
    }

    private void uploadfile(final Uri file){
        File path = new File(file.getPath());
        final StorageReference[] filepath = new StorageReference[1];
        final String[] filetype = {""};
        final Boolean[] isupload = {false,false,false,false};
        final String[] postUrl = {"",""};
        final String[] post_ID = {""};
        final ProgressDialog progressDialog = new ProgressDialog(this);
        mRef_user.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("基本資料").hasChild("貼文")){
                    Long post_count = dataSnapshot.child("貼文").getChildrenCount();
                    int post_count_int = post_count.intValue();
                    post_ID[0] = post_count_int + "";
                }else {
                    post_ID[0] = "0";
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //if there is a file to upload
        if (file != null) {
            if (file == imageUri){
                progressDialog.setTitle(filetype[0] +"上傳中");
                progressDialog.show();
                filepath[0] = mStorageRef_post.child("Photo").child(path.getName());
                filetype[0] = "相片";
                uploadTask_image = filepath[0].putFile(file);
                uploadTask_image.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();
                        Toast.makeText(PostUpload.this, "上傳完成", Toast.LENGTH_LONG).show();
                        @SuppressWarnings("VisibleForTests")String imageurl = taskSnapshot.getDownloadUrl().toString();
                        postUrl[0] = imageurl;
                        isupload[0] = true;
                        mRef_user.child(UID).child("貼文").child(post_ID[0]).child("image").setValue(postUrl[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isupload[1] = true;
                                if (isupload[0] && isupload[1] && isupload[2] && isupload[3]){
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"貼文已成功上傳",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                    finish();
                                }
                            }
                        });
                        point++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("已上傳" + ((int) progress) + "%...");
                    }
                });
            }else if (file == musicUri){
                progressDialog.setTitle(filetype[0] +"上傳中");
                progressDialog.show();
                filepath[0] = mStorageRef_post.child("Music").child(path.getName());
                filetype[0] = "音檔";
                uploadTask_music = filepath[0].putFile(file);
                uploadTask_music.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();
                        Toast.makeText(PostUpload.this, "上傳完成", Toast.LENGTH_LONG).show();
                        @SuppressWarnings("VisibleForTests")String musicurl = taskSnapshot.getDownloadUrl().toString();
                        postUrl[1] = musicurl;
                        isupload[2] = true;

                        mRef_user.child(UID).child("貼文").child(post_ID[0]).child("voice").setValue(postUrl[1]).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                isupload[3] = true;
                                if (isupload[0] && isupload[1] && isupload[2] && isupload[3]){
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"貼文已成功上傳",Toast.LENGTH_SHORT).show();
                                    mRef_user.child(UID).child("任務").child(time_str).child("上傳個性錄音").setValue(1);
                                    onBackPressed();
                                    finish();
                                }
                            }
                        });
                        point++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("已上傳" + ((int) progress) + "%...");
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        ContentResolver contentResolver  =getContentResolver();
        /**
         * 因為兩種方式都用到了startActivityForResult方法，這個方法執行完後都會執行onActivityResult方法，
         * 所以為了區別到底選擇了那個方式獲取圖片要進行判斷，這裡的requestCode跟startActivityForResult裡面第二個參數對應
         */
        if(resultCode == RESULT_OK) {
            if (requestCode == 0) {
                //方式一
                try {
                    //獲得圖片的uri
                    imageUri = data.getData();
                    //imageShow.setImageURI(imageUri); URI圖大小(較慢)
                        /*
                          //將圖片内容解析成字節數組
                        mContent = readStream(contentResolver.openInputStream(Uri.parse(imageUri.toString())));
                         //將字節數組轉換為ImageView可調用的Bitmap對象
                        myBitmap  =getPicFromBytes(mContent,null);*/
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ////把得到的圖片绑定在控件上顯示
                    imageShow.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }

                //方式二

                    /*try {

                        imageUri = data.getData();
                        imageShow.setImageURI(imageUri);
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        Cursor cursor = getContentResolver().query(imageUri,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        imageShow.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    */

            } else if (requestCode == 1) {
                try {
                    // Bundle extras = data.getExtras();
                    // Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ////把得到的圖片绑定在控件上顯示
                    imageShow.setImageBitmap(bitmap);
                        /*
                        txtRec.setText(getBitmapDegree(imageUri.getPath()));
                        Bundle extras = data.getExtras();
                        myBitmap = (Bitmap) extras.get("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.JPEG , 100, baos);
                        mContent=baos.toByteArray();*/
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
                //imageShow.setImageBitmap(myBitmap);
            } else if (requestCode == 2) {
                musicUri = data.getData();
                if (musicUri != null) {
                    getFileName();
                }

            } else if (requestCode == 3) {
            }
        }
    }

    private String getBitmapDegree(String path) {
        String degree = "0";
        try {
            // 從指定路徑下讀取圖片，並獲取其EXIF資訊
            ExifInterface exifInterface = new ExifInterface(path);
            // 獲取圖片的旋轉資訊
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = "90";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = "180";
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = "270";
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private TextView.OnClickListener test =new TextView.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.postupload_txtRec:
//                    Intent intent = new Intent();
//                    intent.setClass(PostUpload.this, HomePage.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("grade",point);
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent,3);

//                    finish();
                    break;
            }
        }
    };
    private void getDateInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        time_str = formatter.format(curDate);
    }
}
