package com.bakamcu.remake.learningassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.leancloud.LCFile;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class AddProblem extends AppCompatActivity {
    final static String TAG = "TAG";    //日志的标签
    final static int CAMERA_REQUEST_CODE = 0;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    ActivityAddProblemBinding binding;  //DataBinding视图
    String problemImagePath = "";
    String wrongAnswerImagePath = "";
    String correctAnswerImagePath = "";
    ProblemsListViewModel viewModel;
    //用于保存拍照图片的uri
    private Uri mCameraUri;
    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;
    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

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
            SetupDialogue("PROBLEM");
        });
        binding.wrongAnswerPhoto.setOnClickListener(view -> {
            SetupDialogue("WRONG_ANSWER");
        });
        binding.correctAnswerPhoto.setOnClickListener(view -> {
            SetupDialogue("CORRECT_ANSWER");
        });
        //---------------------------UI交互监听设置区域结束---------------------------
    }

    void AddProblemToDB(Problem problem) {
        viewModel.insertProbs(problem);
        finish();
    }

    private void SetupDialogue(String picName) {
        final String[] ways = new String[]{"拍照", "从相册选择"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddProblem.this);

        builder.setItems(ways, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ways[which].equals("拍照")) {
                    Log.d(TAG, "onClick: 打开相机");
                    Toast.makeText(AddProblem.this, "还没做！期待一下吧", Toast.LENGTH_SHORT);
                    //checkPermissionAndCamera();
                } else {
                    Log.d(TAG, "onClick: 打开相册");
                    Toast.makeText(AddProblem.this, "还没做！期待一下吧", Toast.LENGTH_SHORT);
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            openCamera();
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
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "openCamera: Opening Camera");
        // 判断是否有相机

        File photoFile = null;
        Uri photoUri = null;
        Log.d(TAG, "openCamera: " + isAndroidQ);
        if (isAndroidQ) {
            // 适配android 10
            photoUri = createImageUri();
        } else {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                mCameraImagePath = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                    photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                } else {
                    photoUri = Uri.fromFile(photoFile);
                }
            }
        }
        mCameraUri = photoUri;
        if (photoUri != null) {
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
        }
    }


    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    binding.problemPicture.setImageURI(mCameraUri);
                    Log.d(TAG, "onActivityResult: " + mCameraUri.getPath());
                    UploadPictureToLC(mCameraUri.getPath());
                } else {
                    // 使用图片路径加载
                    binding.problemPicture.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePath));
                    Log.d(TAG, "onActivityResult: " + mCameraImagePath);
                    UploadPictureToLC(mCameraImagePath);
                }
            } else {
                Toast.makeText(this, "取消", Toast.LENGTH_LONG).show();
            }
        }
    }

    void UploadPictureToLC(String path) {
        Log.d(TAG, "UploadPictureToLC: ");
        LCFile file = new LCFile("problem.png", path);
        file.saveInBackground().subscribe(new Observer<LCFile>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(LCFile file) {
                System.out.println("文件保存完成。URL：" + file.getUrl());
            }

            public void onError(Throwable throwable) {
                // 保存失败，可能是文件无法被读取，或者上传过程中出现问题
                Log.d(TAG, "onError: " + throwable.getMessage());
            }

            public void onComplete() {
            }
        });
    }
}

