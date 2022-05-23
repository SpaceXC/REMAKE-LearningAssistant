package com.bakamcu.remake.learningassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bakamcu.remake.learningassistant.databinding.ActivityUpdateProblemBinding;
import com.bakamcu.remake.learningassistant.utils.DialogUtils;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import cn.leancloud.LCFile;
import cn.leancloud.LCObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class UpdateProblemActivity extends AppCompatActivity {
    final static String TAG = "TAG";    //日志的标签
    //LiveQueryViewModel viewModelLiveQuery = new LiveQueryViewModel(getApplication());
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY = 2;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    ActivityUpdateProblemBinding binding;  //DataBinding视图
    String problemImagePath = "";
    String wrongAnswerImagePath = "";
    String correctAnswerImagePath = "";
    ProblemsListViewModel viewModel;
    String currentPhotoPath;
    PhotoType currentPhotoType;
    Problem origProblem;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_problem);  //DataBinding加载视图
        viewModel = new ViewModelProvider(this).get(ProblemsListViewModel.class);
        //viewModelLiveQuery = new ViewModelProvider(this).get(LiveQueryViewModel.class);
        origProblem = (Problem) getIntent().getSerializableExtra("problem");
        RecoverProblem();
        //----------------------------UI交互监听设置区域----------------------------
        binding.ratingBar2.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            binding.ratingPercent.setText(v * 20 + "%");    //改变textview里的掌握百分比
        });

        binding.update.setOnClickListener(view -> {

            if (TextUtils.isEmpty(Objects.requireNonNull(Objects.requireNonNull(binding.subjects.getText()).toString())) || TextUtils.isEmpty(Objects.requireNonNull(binding.problemSrc.getText()).toString())) {
                Toast.makeText(UpdateProblemActivity.this, "请填写带星号的信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            String subject = binding.subjects.getText().toString();
            binding.update.setEnabled(false);
            AlertDialog alertDialog = DialogUtils.getInstance(UpdateProblemActivity.this).LoadingDialog();
            Problem problem = new Problem(subject,
                    binding.problemSrc.getText().toString().trim(),
                    Objects.requireNonNull(binding.problem.getText()).toString().trim(),
                    Objects.requireNonNull(binding.wrongAnswer.getText()).toString().trim(),
                    Objects.requireNonNull(binding.correctAnswer.getText()).toString().trim(),
                    problemImagePath,
                    wrongAnswerImagePath,
                    correctAnswerImagePath,
                    Objects.requireNonNull(binding.reason.getText()).toString().trim(),
                    getCurrentTime(),
                    String.valueOf(System.currentTimeMillis()),
                    false,
                    String.valueOf(binding.ratingBar2.getRating()));
            LCObject problemLC = viewModel.BuildLeanCloudObject(problem);
            problemLC.setObjectId(origProblem.problemID);
            problemLC.saveInBackground().subscribe(new Observer<LCObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(LCObject lcObject) {
                    alertDialog.dismiss();
                    problem.setProblemID(lcObject.getObjectId());
                    AddProblemToDB(problem);
                }

                @Override
                public void onError(Throwable e) {
                    alertDialog.dismiss();
                    Toast.makeText(UpdateProblemActivity.this, "更新失败！原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.update.setEnabled(true);
                }

                @Override
                public void onComplete() {

                }
            });

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

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProblemActivity.this);

        builder.setItems(ways, (dialog, which) -> {
            if (ways[which].equals("拍照")) {
                Log.d(TAG, "onClick: 打开相机");
                checkPermissionAndCamera();
            } else {
                Log.d(TAG, "onClick: 打开相册");
                ChooseFromGallery();
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

    @SuppressLint("QueryPermissionsNeeded")
    void TakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
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

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName;
        imageFileName = "orig" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("SetTextI18n")
    void RecoverProblem() {
        binding.subjects.setText(origProblem.subject);
        binding.problemSrc.setText(origProblem.problemSource);
        binding.problem.setText(origProblem.problem);
        binding.wrongAnswer.setText(origProblem.wrongAnswer);
        binding.correctAnswer.setText(origProblem.correctAnswer);
        binding.reason.setText(origProblem.reason);
        binding.ratingBar2.setRating(Float.parseFloat(origProblem.probRate));
        binding.ratingPercent.setText(Float.parseFloat(origProblem.probRate) * 20 + "%");

        if (!TextUtils.isEmpty(origProblem.problemImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(origProblem.problemImgPath))
                    .dontAnimate()
                    .into(binding.problemPicture);
            problemImagePath = origProblem.problemImgPath;
        }
        if (!TextUtils.isEmpty(origProblem.wrongAnswerImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(origProblem.wrongAnswerImgPath))
                    .dontAnimate()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.wrongAnswerPicture);
            wrongAnswerImagePath = origProblem.wrongAnswerImgPath;
        }
        if (!TextUtils.isEmpty(origProblem.correctImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(origProblem.correctImgPath))
                    .dontAnimate()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.correctAnswerPicture);
            correctAnswerImagePath = origProblem.correctImgPath;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                Log.d(TAG, "onActivityResult: REQUEST_TAKE_PHOTO");

                if (resultCode == RESULT_OK) {
                    Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir("image"), System.currentTimeMillis() + "-cropped.png"));
                    UCrop.of(photoURI, destinationUri)
                            .start(UpdateProblemActivity.this);
                    Log.d(TAG, "onActivityResult: Attempting to crop image");
                }
                break;
            case REQUEST_GALLERY:
                Log.d(TAG, "onActivityResult: REQUEST_GALLERY");
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir("image"), System.currentTimeMillis() + "-cropped.png"));
                    UCrop.of(uri, destinationUri).start(UpdateProblemActivity.this);
                    Log.d(TAG, "onActivityResult: Requesting Crop Activity");
                }
                break;
            case UCrop.REQUEST_CROP:
                Log.d(TAG, "onActivityResult: REQUEST_CROP");
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri cropResultUri = UCrop.getOutput(data);
                    switch (currentPhotoType) {
                        case PROBLEM:
                            //binding.problemPicture.setImageURI(cropResultUri);
                            Glide.with(this)
                                    .load(cropResultUri)
                                    .dontAnimate()
                                    .into(binding.problemPicture);
                            assert cropResultUri != null;
                            UploadPictureToLC(cropResultUri.getPath());
                            break;
                        case WRONG_ANSWER:
                        case CORRECT_ANSWER:
                            assert cropResultUri != null;
                            UploadPictureToLC(cropResultUri.getPath());
                            break;
                    }
                }

            case UCrop.RESULT_ERROR:
                break;
        }
    }

    //-------------------图片相关结束------------------

    void UploadPictureToLC(String path) {
        binding.update.setEnabled(false);
        AlertDialog alertDialog = DialogUtils.getInstance(UpdateProblemActivity.this).LoadingDialog();
        Log.d(TAG, "UploadPictureToLC: ");
        LCFile file = null;
        try {
            file = LCFile.withAbsoluteLocalPath(currentPhotoType.name() + ".png", path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert file != null;
        file.saveInBackground().subscribe(new Observer<LCFile>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(LCFile file) {
                binding.update.setEnabled(true);

                switch (currentPhotoType) {
                    case PROBLEM:
                        problemImagePath = file.getUrl();
                        System.out.println("problem文件保存完成。URL：" + problemImagePath);
                        alertDialog.dismiss();
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
                binding.update.setEnabled(true);
                Toast.makeText(UpdateProblemActivity.this, "图片上传失败！请检查网络连接并重试！", Toast.LENGTH_SHORT).show();
            }

            public void onComplete() {
            }
        });
    }


    public String getCurrentTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }
}