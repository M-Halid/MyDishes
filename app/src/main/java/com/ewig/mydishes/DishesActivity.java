package com.ewig.mydishes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.ewig.mydishes.databinding.ActivityDishesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class DishesActivity extends AppCompatActivity {

    private ActivityDishesBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDishesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        database = this.openOrCreateDatabase("Dishes", MODE_PRIVATE, null);
        registerLauncher();

        Intent intent =getIntent();
        String info= intent.getStringExtra("info");

        if(info.equals("new")){
            binding.foodName.setText("");
            binding.foodIngr.setText("");
            binding.foodLoc.setText("");
            binding.save.setVisibility(View.VISIBLE);
            binding.selectImage.setImageResource(R.drawable.select1);
        }else{

            int dishId=intent.getIntExtra("dishId",1);
            binding.save.setVisibility(View.INVISIBLE);

            try {
            Cursor cursor=database.rawQuery("SELECT * FROM dishes WHERE id=?",new String[]{String.valueOf(dishId)});
            int foodNameIx=cursor.getColumnIndex("foodname");
            int foodLocIx=cursor.getColumnIndex("foodingredients");
            int foodIgrIx=cursor.getColumnIndex("foodlocation");
            int foodImageIx=cursor.getColumnIndex("foodimage");

            while (cursor.moveToNext()){
                binding.foodName.setText(cursor.getString(foodNameIx));
                binding.foodLoc.setText(cursor.getString(foodLocIx));
                binding.foodIngr.setText(cursor.getString(foodLocIx));

                byte[] bytes=cursor.getBlob(foodImageIx);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                binding.selectImage.setImageBitmap(bitmap);
            }
            cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }
        public void save(View view) {
            String name = binding.foodName.getText().toString();
            String ingredients = binding.foodIngr.getText().toString();
            String location = binding.foodLoc.getText().toString();

            Bitmap smalledImage = imageSmaller(selectedImage, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smalledImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS dishes(id INTEGER PRIMARY KEY, foodname VARCHAR, " +
                        "foodingredients VARCHAR, foodlocation VARCHAR, foodimage BLOB)");
                String sqlString = "INSERT INTO dishes(foodname, foodingredients,foodlocation, foodimage) VALUES(?,?,?,?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, name);
                sqLiteStatement.bindString(2, ingredients);
                sqLiteStatement.bindString(3, location);
                sqLiteStatement.bindBlob(4, byteArray);
                sqLiteStatement.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(DishesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        public Bitmap imageSmaller(Bitmap image, int maximumSize){
        float width=(float) image.getWidth();
        float height=(float)image.getHeight();
        float bitMapRatio=width/height;
        if (bitMapRatio>1){
             //Landscape
            width=maximumSize;
            height=(int)(width/bitMapRatio);
        }else{//Portrait
                height=maximumSize;
                width=(int)(height*bitMapRatio);
            }
        return image.createScaledBitmap(image,100,100 ,true);
        }
        public void selectImage(View view){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(DishesActivity.this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
            //Retionale
            if(ActivityCompat.shouldShowRequestPermissionRationale(DishesActivity.this,Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"Permission needed for Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Request Permission!
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                    }
                }).show();
            }else{

                //Request Permission!
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }

        }else {
            // Go to Gallery!
            Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

        }else{
        if(ContextCompat.checkSelfPermission(DishesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //Retionale
            if(ActivityCompat.shouldShowRequestPermissionRationale(DishesActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    //Request Permission!
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else{

            //Request Permission!
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else {
            // Go to Gallery!
            Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }}
        }

        public void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                Intent intentFromResult=result.getData();
                if (intentFromResult!=null){
                    Uri imageData=intentFromResult.getData();
                    binding.selectImage.setImageURI(imageData);
                    try {
                        if(Build.VERSION.SDK_INT>=28){
                        ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imageData);
                        selectedImage = ImageDecoder.decodeBitmap(source);
                        binding.selectImage.setImageBitmap(selectedImage);
                        }else {
                            selectedImage=MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
                            binding.selectImage.setImageBitmap(selectedImage);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //Permission Granted! -> Go to Gallery
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
                }else{
                    //Permission Denied
                    Toast.makeText(DishesActivity.this, "Permission Needed!", Toast.LENGTH_LONG).show();


                }

            }
        });
        }
}