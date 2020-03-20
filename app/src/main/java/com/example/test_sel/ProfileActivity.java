package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    /////camera/////

    //permissions
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    //image pick constatn
    public static final int IMAGE_PICK_CAMERA_CODE = 300;
    public static final int IMAGE_PICK_GALLERY_CODE = 400;

    public TextView txt_fill_allCourses, txt_head_allCourses;

    //image picked will be in thie uri
    Uri image_uri = null;


    String[] cameraPermission;
    String[] StoragePermission;
/////camera/////

    private String[] engineeringArray,acadenyArray;
    private TextView txt_fill_academy, txt_fill_start_year, txt_fill_engineering,txt_Description,txt_fill_name, txt_fill_phone;
    private ImageView img_edit, img_camera, img_profile, ic_whatsapp,img_add,img_schedule;
    private Button btn_course,BTN_schedule,btn_add;
    private String currentPhotoPath,userId;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DatabaseReference userDBR;
    private StorageReference fstorage;
    private Toolbar toolBar;


    //  private DocumentReference documentRef;
    private ProgressDialog pd;

    private DatabaseReference refuser;
    private DatabaseReference refImage;
    //pgoto
    private StorageReference storageReferanc;
    //path where the image of the user will stored
    String storagePath = "user_profile_image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nice);

        //init
        findById();

//TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Profile");

/////camera premissions/////
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        StoragePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//arra from R
        engineeringArray = getResources().getStringArray(R.array.engineering);
        acadenyArray = getResources().getStringArray(R.array.academy);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance().getReference();
//        FirebaseStorage.getInstance().getReference();

        if(getIntent().getStringExtra("visiitUserId") != null){
            userId = getIntent().getExtras().getString("visiitUserId");
            img_edit.setVisibility(View.INVISIBLE);
            img_camera.setVisibility(View.INVISIBLE);
        } else {
            userId = fAuth.getCurrentUser().getUid();
        }


        //doc to get the data
//        documentRef = fStore.collection("users").document(userId);

        //init progress dialog
        //in his code: pd=new ProgressDialog(getActivity);
        pd = new ProgressDialog(ProfileActivity.this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(img_profile);
            }
        }


        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("updating profile picture");
                showPicDialog();
            }
        });

        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), UserSignUpActivity.class);
                i.putExtra("update", "update");
                i.putExtra("userid", userId);
                i.putExtra("type", "update");

                startActivity(i);
                finish();
               // showEditDialog();
            }
        });


        ic_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), allMyCoursesActivity.class);
                i.putExtra("visiitUserId", userId);
                startActivity(i);


            }
        });

        img_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ScheduleActivity.class);
                startActivity(i);

            }
        });
//        BTN_schedule.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), postActivity.class);
////                i.putExtra("kind", "addCourse");
////                i.putExtra("username", txt_fill_name.toString());
////                i.putExtra("userId", userId);
//
//                startActivity(i);
//
//            }
//        });
//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });


        //get data
        refuser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(int i = 0; i < dataSnapshot.getChildrenCount(); i++){
////                    Log.d("Users", dataSnapshot);
//                }
                txt_fill_name.setText(dataSnapshot.child("fullName").getValue().toString());
                txt_fill_phone.setText(dataSnapshot.child("phone").getValue().toString());
                txt_fill_academy.setText(dataSnapshot.child("academy").getValue().toString());
                txt_fill_start_year.setText(dataSnapshot.child("startYear").getValue().toString());
                txt_fill_engineering.setText(dataSnapshot.child("engineering").getValue().toString());
//                txt_fill_allCourses.setText(dataSnapshot.child("userCourses").getValue().toString());
                txt_Description.setText(dataSnapshot.child("description").getValue().toString());
                //image
                String imagePath = dataSnapshot.child("imagePath").getValue().toString();
                try {
                    //if image is recieves then set
                    Picasso.get().load(imagePath).into(img_profile);
                } catch (Exception e) {
                    //if ther is ane error while getting image
                    Picasso.get().load(R.drawable.ic_person).into(img_profile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //get data from doc
//        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                //if the doc exsist
//                if (documentSnapshot.exists()) {
//                    txt_fill_name.setText(documentSnapshot.getString("Full Name"));
////                    txt_fill_identity_number.setText(documentSnapshot.getString("Identity"));
//                    txt_fill_phone.setText(documentSnapshot.getString("Phone"));
////                    txt_fill_email.setText(documentSnapshot.getString("Email"));
//                    txt_fill_academy.setText(documentSnapshot.getString("Academy"));
//                    txt_fill_start_year.setText(documentSnapshot.getString("Start year"));
//                    txt_fill_engineering.setText(documentSnapshot.getString("Engineering"));
//                }
//            }
//        });

    }
    public void findById(){
        //init
        txt_fill_name = findViewById(R.id.txt_fill_name);
        txt_fill_phone = findViewById(R.id.txt_fill_phone);
        txt_fill_academy = findViewById(R.id.txt_fill_academy);
        txt_fill_start_year = findViewById(R.id.txt_fill_start_year);
        txt_fill_engineering = findViewById(R.id.txt_fill_engineering);
        txt_Description = findViewById(R.id.txt_Description);
        //txt_fill_allCourses = findViewById(R.id.txt_fill_allCourses);
        txt_head_allCourses = findViewById(R.id.txt_head_allCourses);
        img_edit = findViewById(R.id.img_edit);
        img_profile = findViewById(R.id.img_profile);
        img_camera = findViewById(R.id.img_camera);
        ic_whatsapp = findViewById(R.id.ic_whatsapp);
//        btn_course=findViewById(R.id.BTN_AllCourse);
//        BTN_schedule=findViewById(R.id.BTN_schedule);
//        btn_add=findViewById(R.id.BTN_add);
        img_add=findViewById(R.id.img_add);
        img_schedule=findViewById(R.id.img_schedule);


    }
    private void sendMessage() {
        Uri uri = Uri.parse("smsto:" + txt_fill_phone.getText().toString());
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));


    }

    //----------------START CAMERA METHODS------------------------------------------
/////camera /////
    private boolean checkStoragePermissions() {
        //check if storage permission enale or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, StoragePermission, GALLERY_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {
        //check if camera permission enale or not
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        //intent to pick image from gallery
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/+");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this methos is called when user press Aloow or Deny from permmision request dialof
        //here we will handle with permmission cases(allower or denny)
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission enable
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Camera & storage Permissions are Required"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case GALLERY_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "storage Permission is Required "
                                , Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                img_profile.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                img_profile.setImageURI(image_uri);
            }
        }
        uploadImage(image_uri);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadImage(Uri uri) {
        pd.setMessage("upload image");
        pd.show();
        //uri= filepath
//        pd.setMessage("publish");
//        pd.show();
        String filePathName =  storagePath + "_" + userId;

        StorageReference storageReference2 = fstorage.child(filePathName);
        storageReference2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while ((!uriTask.isSuccessful())) ;
                Uri downloadUri = uriTask.getResult();
                //check if image is upload or not and uri is recieve
                if (uriTask.isSuccessful()) {
                    //image is upload
                    //add / update uri in user database
                    updateDataToDoc(downloadUri.toString(), "imagePath");
                } else {
                    //error
//                    pd.dismiss();
                  //  Toast.makeText(ProfileActivity.this, "some error", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

//                pd.dismiss();
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


//        fstorage.child("user_image" + userId);
//        fstorage.putFile(Uri.parse(String.valueOf(image_uri))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(ProfileActivity.this, "profile image uploads ", Toast.LENGTH_SHORT).show();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ProfileActivity.this, "profile no uploads ", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    public void showPicDialog() {
        //option to show in dialog
        String option[] = {"Camera", "Gallery"};
        //allert dialog
        // his line :AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        //set title
        addMytitle(builder, "Pick Image From");

        // set item to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user choose camera
                if (which == 0) {
                    Toast.makeText(ProfileActivity.this, "camera btn is clicked ", Toast.LENGTH_SHORT).show();
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions();
                    } else {
                        pickFromCamera();

                    }
//                    handLeImageClicike(img_camera);
                }
                //user choose gallery
                else if (which == 1) {
                    Toast.makeText(ProfileActivity.this, "gallery btn is clicked ", Toast.LENGTH_SHORT).show();
                    if (!checkStoragePermissions()) {
                        requestStoragePermissions();
                    } else {
                        pickFromGallery();


                    }
                    //                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                }
            }
        });
        builder.create().show();

    }

//----------------END CAMERA METHODS------------------------------------------


//----------------start details METHODS------------------------------------------

    private void addMytitle(AlertDialog.Builder builder, String s) {
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText(s);
        //TODO
        //  title.setBackgroundColor(R.color.colorheadDialog);
        title.setBackgroundColor(Color.MAGENTA);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);
    }

//    public void showEditDialog() {
//        //option to show in dialog
//        String option[] = {"Edit Full Name", "Edit Phone", "Edit Strat year", "Edit Academy", "Edit engineering"};
//        //allert dialog
//
//        // his line :AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
//        //set title
//        //set
//        addMytitle(builder, "choose Action");
////        builder.
//        // set item to dialog
//        builder.setItems(option, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //handel dialog item click
//                if (which == 0) {
//                    //edit name
//                    //documentRef.update("fName",);
//                    pd.setMessage("Updating Full Name");
//                    //send key value of doc
//                    showEditValueDialog("Full Name", txt_fill_name);
//                } else if (which == 1) {
//                    //edit Phone
//                    pd.setMessage("Updating phone");
//                    showEditValueDialog("Phone", txt_fill_phone);
//
//                } else if (which == 2) {
//                    //edit Strat year
//                    pd.setMessage("Updating Strat year");
//                    showEditValueDialog("Start year", txt_fill_start_year);
//                } else if (which == 3) {
//                    //edit Academy
//                    pd.setMessage("Updating Academy");
//                    showEditValueDialog("Academy", txt_fill_academy);
//
//
//                } else if (which == 4) {
//                    //edit engineering
//                    pd.setMessage("Updating Engineering");
//                    showEditValueDialog("Engineering", txt_fill_engineering);
//
//                }
//            }
//        });
//        builder.create().show();
//
//    }
//
//
//    public void showEditValueDialog(final String key, final TextView textView) {
//        //custom dialog
//        // his line :AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
//        addMytitle(builder, "Update " + key);
//
//        //set layout of dialog
//        LinearLayout linearLayout = new LinearLayout((ProfileActivity.this));
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setPadding(10, 10, 10, 10);
//        //add edit text
//        final EditText editText = new EditText(ProfileActivity.this);
//        editText.setHint("Enter " + key);
//        linearLayout.addView(editText);
//
//        builder.setView(linearLayout);
//        //add button to update
//        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //input text from edit text
//                String value = editText.getText().toString().trim();
//                //check for engineering
//
//                if ((!TextUtils.isEmpty(value)) && (checkvalue(value, key))) {
//                    pd.show();
//                    updateDataToDoc(value, key);
//                } else {
//                    Toast.makeText(ProfileActivity.this, "plese enter " + key, Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        //add button to cancel
//        builder.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.create().show();
//
//    }
//
//    private boolean checkvalue(String value, String key) {
//        if (key.equals("Start year"))
//            return checksForYear(value);
//        if (key.equals("Engineering"))
//            return checksForEngineering(value);
//        if (key.equals("Academy"))
//            return checksForAcademy(value);
//        return true;
//    }
//
//    public static void setVisubleCourses() {
//        txt_fill_allCourses.setVisibility(View.VISIBLE);
//        txt_head_allCourses.setVisibility(View.VISIBLE);
//        txt_head_allCourses.setText("hello");
//    }
//
//    private boolean checksForYear(String value) {
//        try {
//            int year = Integer.parseInt(value);
//            if (year > 2020 || year < 2000) {
//                Toast.makeText(ProfileActivity.this, "the year that you start study is not legal", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } catch (NumberFormatException nfe) {
//            Toast.makeText(ProfileActivity.this, "the year that you start study is not legal", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return false;
//    }
//
//    private boolean checksForEngineering(String value) {
//        boolean t = false;
//        for (int i = 0; i < engineeringArray.length; i++) {
//            if (engineeringArray[i].equalsIgnoreCase(value)) {
//                t = true;
//                return true;
//            }
//        }
//        if (t == false) {
//
//            Toast.makeText(ProfileActivity.this, "only: Software, " +
//                    "Biomedical Basic Sciences, Medical,Electrical" +
//                    ", Machines \nare legal", Toast.LENGTH_LONG).show();
//            return false;
//
//        }
//        return false;
//    }
//
//    private boolean checksForAcademy(String value) {
//        boolean t = false;
//        for (int i = 0; i < acadenyArray.length; i++) {
//            if (acadenyArray[i].equalsIgnoreCase(value)) {
//                t = true;
//                return true;
//            }
//        }
//        if (t == false) {
//
//            Toast.makeText(ProfileActivity.this, "only: Afeka is legal", Toast.LENGTH_LONG).show();
//            return false;
//
//        }
//        return false;
//    }
//
    public void updateDataToDoc(final String newValue, String key) {
        pd.show();
        refuser.child(key).setValue(newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updat
                        pd.dismiss();
                        Toast.makeText(ProfileActivity.this, "Update ", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();

            }
        });
//
//
////        documentRef.update(key, newValue)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if (task.isSuccessful()) {
////                            textView.setText(newValue);
////                            pd.dismiss();
////                            Toast.makeText(ProfileActivity.this, "Update ", Toast.LENGTH_SHORT).show();
////
////                        } else {
////                            pd.dismiss();
////                            Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
////
////                        }
////                    }
////                });
    }

//----------------end details METHODS------------------------------------------

//----------------START top toolbar------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inFlater = getMenuInflater();
        inFlater.inflate(R.menu.side_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(getApplicationContext(), frag_activity.class));
            finish();
        }

        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(getApplicationContext(), homePageActivity.class));
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------
