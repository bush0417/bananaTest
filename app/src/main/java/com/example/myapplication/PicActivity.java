package com.example.myapplication;

import static com.example.myapplication.R.id.btnimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PicActivity extends AppCompatActivity {

    Button btnimage;
    ImageView imageview;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        btnimage = findViewById(R.id.btnimage);
        imageview = findViewById(R.id.imageview);
        registerResult();

        btnimage.setOnClickListener(view -> pickimage());

    }

    private void pickimage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher =registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();

                            Bitmap bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                            Bitmap bitmap = Bitmap.createScaledBitmap(bitmap2, 224, 224 ,false);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                            byte[] compressedBitmap = outputStream.toByteArray();

                            Intent intent = new Intent();
                            intent.setClass(PicActivity.this, TakePhoto.class);
                            intent.putExtra("image", compressedBitmap);
                            startActivity(intent);
                            imageview.setImageURI(imageUri);

                        }catch (Exception e){
                            Toast.makeText(PicActivity.this, "No Select Image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}