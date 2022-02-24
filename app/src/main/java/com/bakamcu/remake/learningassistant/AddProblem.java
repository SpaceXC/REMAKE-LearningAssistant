package com.bakamcu.remake.learningassistant;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bakamcu.remake.learningassistant.databinding.ActivityAddProblemBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.leancloud.LCFile;
import cn.leancloud.ops.Utils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

enum PhotoType {
    PROBLEM,
    WRONG_ANSWER,
    CORRECT_ANSWER
}

public class AddProblem extends AppCompatActivity {
    final static String TAG = "TAG";    //日志的标签
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    ActivityAddProblemBinding binding;  //DataBinding视图
    String problemImagePath = "";
    String wrongAnswerImagePath = "";
    String correctAnswerImagePath = "";
    ProblemsListViewModel viewModel;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY = 2;
    String currentPhotoPath;
    PhotoType currentPhotoType;
    //用于保存拍照图片的uri
    private Uri photoURI;

    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            //TODO
            TakePhoto();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                //TODO
                TakePhoto();

            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_problem);  //DataBinding加载视图
        viewModel = new ViewModelProvider(this).get(ProblemsListViewModel.class);


        //----------------------------UI交互监听设置区域----------------------------
        binding.ratingBar2.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            binding.ratingPercent.setText(v * 20 + "%");    //改变textview里的掌握百分比
        });

        binding.submit.setOnClickListener(view -> {
            RadioButton subjectButton = findViewById(binding.subjects.getCheckedRadioButtonId());
            if (subjectButton == null || TextUtils.isEmpty(Objects.requireNonNull(binding.problemSrc.getText()).toString())) {
                Toast.makeText(AddProblem.this, "请填写带星号的信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            String subject = subjectButton.getText().toString();
            Problem problem = new Problem(subject,
                    binding.problemSrc.getText().toString().trim(),
                    Objects.requireNonNull(binding.problem.getText()).toString().trim(),
                    Objects.requireNonNull(binding.wrongAnswer.getText()).toString().trim(),
                    Objects.requireNonNull(binding.correctAnswer.getText()).toString().trim(),
                    problemImagePath,
                    wrongAnswerImagePath,
                    correctAnswerImagePath,
                    Objects.requireNonNull(binding.reason.getText()).toString().trim(),
                    System.currentTimeMillis(),
                    false,
                    binding.ratingBar2.getRating());
            AddProblemToDB(problem);
        });

        binding.problemPhoto.setOnClickListener(view -> {
            currentPhotoType = PhotoType.PROBLEM;
            SetupDialogue();
        });
        binding.wrongAnswerPhoto.setOnClickListener(view -> {
            currentPhotoType = PhotoType.WRONG_ANSWER;
            SetupDialogue();
        });
        binding.correctAnswerPhoto.setOnClickListener(view -> {
            currentPhotoType = PhotoType.CORRECT_ANSWER;
            SetupDialogue();
        });
        //---------------------------UI交互监听设置区域结束---------------------------
    }

    void AddProblemToDB(Problem problem) {
        viewModel.insertProbs(problem);
        finish();
    }

    private void SetupDialogue() {
        final String[] ways = new String[]{"拍照", "从相册选择"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddProblem.this);

        builder.setItems(ways, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ways[which].equals("拍照")) {
                    Log.d(TAG, "onClick: 打开相机");
                    checkPermissionAndCamera();
                } else {
                    Log.d(TAG, "onClick: 打开相册");
                    ChooseFromGallery();
                    //Toast.makeText(AddProblem.this, "还没做！期待一下吧", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //--------------------图片相关--------------------
    void ChooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    void TakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(false);
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG, "TakePhoto: Here I come!" + photoURI.getPath());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile(boolean isCropping) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "";
        if (!isCropping) {
            imageFileName = "orig" + timeStamp + "_";
        } else {
            imageFileName = "crop" + timeStamp + "_";

        }
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                Log.d(TAG, "onActivityResult: REQUEST_TAKE_PHOTO");
                File cropFile = null;

                if (resultCode == RESULT_OK) {
                    Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir("image"), System.currentTimeMillis() + "-croped.png"));
                    UCrop.of(photoURI, destinationUri)
                            .start(AddProblem.this);
                    Log.d(TAG, "onActivityResult: Attempting to crop image");
                }
                break;
            case REQUEST_GALLERY:
                Log.d(TAG, "onActivityResult: REQUEST_GALLERY");
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir("image"), System.currentTimeMillis() + "-croped.png"));
                    UCrop.of(uri, destinationUri).start(AddProblem.this);
                    Log.d(TAG, "onActivityResult: Requesting Crop Activity");
                }
                break;
            case UCrop.REQUEST_CROP:
                Log.d(TAG, "onActivityResult: REQUEST_CROP");
                if (resultCode == RESULT_OK) {
                    Uri cropResultUri = UCrop.getOutput(data);
                    switch (currentPhotoType) {
                        case PROBLEM:
                            binding.problemPicture.setImageURI(cropResultUri);
                            UploadPictureToLC(cropResultUri.getPath());
                            break;
                        case WRONG_ANSWER:
                            binding.wrongAnswerPicture.setImageURI(cropResultUri);
                            UploadPictureToLC(cropResultUri.getPath());
                            break;
                        case CORRECT_ANSWER:
                            binding.correctAnswerPicture.setImageURI(cropResultUri);
                            UploadPictureToLC(cropResultUri.getPath());
                            break;
                    }
                }

            case UCrop.RESULT_ERROR:
                break;
        }
    }

    Uri GetImageUriFromGallery(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);  //获取照片路径
        cursor.close();
        File pathFile = new File(path);
        return Uri.fromFile(pathFile);
    }

    //-------------------图片相关结束------------------

    void UploadPictureToLC(String path) {
        binding.submit.setEnabled(false);
        AlertDialog alertDialog = LoadingDialog();
        Log.d(TAG, "UploadPictureToLC: ");
        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath(currentPhotoType.name() + ".png", path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        file.saveInBackground().subscribe(new Observer<LCFile>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(LCFile file) {
                binding.submit.setEnabled(true);
                alertDialog.dismiss();
                switch (currentPhotoType) {
                    case PROBLEM:
                        problemImagePath = file.getUrl();
                        System.out.println("problem文件保存完成。URL：" + problemImagePath);
                        break;
                    case WRONG_ANSWER:
                        wrongAnswerImagePath = file.getUrl();
                        System.out.println("wrongAnswer文件保存完成。URL：" + wrongAnswerImagePath);
                        break;
                    case CORRECT_ANSWER:
                        correctAnswerImagePath = file.getUrl();
                        System.out.println("CorrectAnswer文件保存完成。URL：" + correctAnswerImagePath);
                        break;
                }
            }

            public void onError(Throwable throwable) {
                // 保存失败，可能是文件无法被读取，或者上传过程中出现问题
                Log.d(TAG, "onError: " + throwable.getMessage());
                alertDialog.dismiss();
                binding.submit.setEnabled(true);
            }

            public void onComplete() {
            }
        });
    }

    public AlertDialog LoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.loading_dialog, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }
}