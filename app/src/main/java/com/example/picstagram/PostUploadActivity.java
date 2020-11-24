package com.example.picstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PostUploadActivity extends AppCompatActivity {
    Button UploadBtn;
    private FusedLocationProviderClient client;
    private GeoPoint postLocation;

    ImageView PostImg;
    EditText CaptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        requestPermission();

        UploadBtn = (Button) findViewById(R.id.uploadBtn);
        PostImg = (ImageView) findViewById(R.id.postImageView);
        CaptionField = (EditText) findViewById(R.id.captionTextView);
        client = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation();
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("postImg");
        Bitmap postImg = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        PostImg.setImageBitmap(postImg);

        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostImg.invalidate();
                BitmapDrawable drawable = (BitmapDrawable) PostImg.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String caption = CaptionField.getText().toString();
                createPost(bitmap, caption, postLocation);
                Log.d("working", CaptionField.getText().toString());
                Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToHome);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }

    protected void createPost(Bitmap bitmap, String caption, GeoPoint postLocation) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imagesRef = storageReference.child("images" + UUID.randomUUID().toString());

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("fail", "error");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Post newPost = new Post(fileLink, caption, postLocation);
                                db.collection("posts")
                                        .add(newPost)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                Log.d("PostUploadActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                Log.w("PostUploadActivity", "Error adding document", e);
                                            }
                                        });
                            }
                        });
                // Do what you want
            }
        });
    }

    protected void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(PostUploadActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PostUploadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation()
                .addOnSuccessListener(PostUploadActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            GeoPoint instance = new GeoPoint(location.getLatitude(), location.getLongitude());
                            postLocation = instance;
                            Log.d("Success", String.valueOf(location.getLongitude()) + " " + String.valueOf(location.getLatitude()));
                        }
                        else {
                            Log.d("Failure", "Here");
                        }
                    }
                });
    }
}