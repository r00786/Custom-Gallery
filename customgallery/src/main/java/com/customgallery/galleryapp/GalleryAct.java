package com.customgallery.galleryapp;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class GalleryAct extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {
    public static final String IMAGE_KEY = "IMAGE";
    public static final String SHOULD_OPEN_GALLERY = "OPEN_GALLERY";
    public static final String SELECTION_COUNT = "SEL_COUNT";
    public final int STORAGE_PERMISSION = 109;
    public final int SETTINGS_CODE = 101;
    public final int SELECTION_STATE_SINGLE = 0;
    public final int SELECTION_STATE_MULTIPLE = 1;
    public int selectionState;
    private boolean mOpenGallery;
    private int selectionCount;
    private RecyclerView rvImage;
    private View bottomSheet;
    private CameraView camera;
    private ImageView ivCapture;
    private ImageView ivFlashOn;
    private ImageView ivFlashOff;
    private ImageView ivSwitchCamera;
    private ImageView ivDone;
    private Toolbar toolBar;
    private ImageView ivBack;
    private List<GalleryData> DCIMFolderList;
    private List<GalleryData> picturesFolderList;
    private List<GalleryData> downloadFolderList;
    private List<GalleryData> newList;
    private ImageAdapter imageAdapter;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean invertCamera;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Integer> selectedPositions = new ArrayList<>();

    public static void openGallery(Activity activity, int requestCode, boolean vOpenGallery, int selectionCount) {
        Intent intent = new Intent(activity, GalleryAct.class);
        intent.putExtra(SHOULD_OPEN_GALLERY, vOpenGallery);
        intent.putExtra(SELECTION_COUNT, selectionCount);
        intent.putExtra(SELECTION_COUNT, selectionCount);
        activity.startActivityForResult(intent, requestCode);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_act);
        camera = findViewById(R.id.camera_custom_gallery);
        rvImage = findViewById(R.id.rv_images_custom_gallery);
        bottomSheet = findViewById(R.id.bottom_sheet_custom_gallery);
        ivCapture = findViewById(R.id.iv_capture_custom_gallery);
        ivFlashOn = findViewById(R.id.iv_flashOn_custom_gallery);
        ivFlashOff = findViewById(R.id.iv_flashOff_custom_gallery);
        ivSwitchCamera = findViewById(R.id.iv_switchCamera_custom_gallery);
        toolBar = findViewById(R.id.toolBar_custom_gallery);
        ivBack = findViewById(R.id.iv_back_custom_gallery);
        ivDone = findViewById(R.id.iv_done);
        checkForPermission();
        initClicks();
        mOpenGallery = getIntent().getBooleanExtra(SHOULD_OPEN_GALLERY, false);
        selectionCount = getIntent().getIntExtra(SELECTION_COUNT, 1);
        rvImage.addOnItemTouchListener(new RecyclerItemClickListener(this, rvImage, this));
    }

    public void setUpRecyclerView() {
        DCIMFolderList = new ArrayList<>();
        picturesFolderList = new ArrayList<>();
        downloadFolderList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this);
        gridLayoutManager = new GridLayoutManager(GalleryAct.this, ImageAdapter.SPAN_COUNT_IMAGES);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (imageAdapter.getItemViewType(position) == ImageAdapter.IMAGE_TILE) {
                    return ImageAdapter.SPAN_COUNT_DATE;
                }
                return ImageAdapter.SPAN_COUNT_IMAGES;

            }
        });
        rvImage.setLayoutManager(gridLayoutManager);
        rvImage.setAdapter(imageAdapter);
        rvImage.addItemDecoration(new HeaderItemDecoration(rvImage, imageAdapter));

    }


    public void checkForPermission() {
        if (PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            init();
        } else {
            if (PermissionUtils.shouldShowRational(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Display UI and wait for user interaction
                PermissionUtils.requestPermissions(
                        this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
                PermissionUtils.markedPermissionAsAsked(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                if (PermissionUtils.hasAskedForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "you need to provide permissions to proceed further", Toast.LENGTH_SHORT).show();
                    PermissionUtils.goToAppSettings(this, SETTINGS_CODE);
                } else {
                    PermissionUtils.requestPermissions(
                            this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION);
                }

            }

        }
    }

    public void init() {
        setUpRecyclerView();
        getAllImages();
        setBottomSheetBehavior();
        loadAnimations();
    }

    private void getAllImages() {
        File DCIMFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        DCIMFolderList = imageReader(DCIMFolder);
        downloadFolderList = imageReader(downloadFolder);
        picturesFolderList = imageReader(picturesFolder);
        newList = new ArrayList<>();
        newList.addAll(DCIMFolderList);
        newList.addAll(downloadFolderList);
        newList.addAll(picturesFolderList);
        setDateInItems();
    }

    private ArrayList<GalleryData> imageReader(File root) {
        ArrayList<GalleryData> a = new ArrayList<>();
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    GalleryData galleryData = new GalleryData();
                    Date date = new Date(file.lastModified());
                    galleryData.setFile(file);
                    galleryData.setDate(date);
                    a.addAll(imageReader(file));
                } else {
                    if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".gif")) {
                        GalleryData galleryData = new GalleryData();
                        Date date = new Date(file.lastModified());
                        galleryData.setFile(file);
                        galleryData.setDate(date);
                        a.add(galleryData);
                    }
                }
            }
        }
        return a;
    }


    public void onPress(int position) {
        File photo = newList.get(position).getFile();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(photo.getAbsolutePath());
        sendResultBackToActivity(arrayList);
    }

    private void setBottomSheetBehavior() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) (Utility.convertDpToPixel(150, this)));
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Utility.manipulateVisibility(slideOffset, rvImage, toolBar, ivCapture, ivFlashOn, ivFlashOff, ivSwitchCamera);
                if (slideOffset >= 1) {
                    camera.stop();
                } else if (slideOffset == 0) {
                    camera.start();
                    removeSelection();
                    selectedPositions.clear();
                    ivDone.setVisibility(View.GONE);
                }
            }
        });
        if (mOpenGallery) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    public void removeSelection() {
        for (Integer selectedPosition : selectedPositions)
            imageAdapter.removeSelection(selectedPosition);
    }

    void initClicks() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        ivCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        ivFlashOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashOn();
            }
        });
        ivFlashOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashOff();
            }
        });
        ivSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSelectedFiles();
            }
        });

    }

    void fetchSelectedFiles() {
        ArrayList<String> filesSelected = new ArrayList<>();
        for (Integer item : selectedPositions) {
            filesSelected.add(newList.get(item).getFile().getAbsolutePath());
        }
        sendResultBackToActivity(filesSelected);
    }


    void goBack() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    void captureImage() {
        camera.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                if (cameraKitImage.getJpeg() != null) {
                    synchronized (cameraKitImage) {
                        File photo = Utility.writeImage(cameraKitImage.getJpeg());
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(photo.getAbsolutePath());
                        sendResultBackToActivity(arrayList);
                        Toast.makeText(GalleryAct.this, "Image Clicked", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GalleryAct.this, "Unable to Get The Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void flashOn() {
        camera.setFlash(1);
        ivFlashOn.setVisibility(View.GONE);
        ivFlashOff.setVisibility(View.VISIBLE);
    }

    void flashOff() {
        camera.setFlash(0);
        ivFlashOn.setVisibility(View.VISIBLE);
        ivFlashOff.setVisibility(View.GONE);
    }

    void switchCamera() {
        if (!invertCamera) {
            mSetRightOut.setTarget(ivSwitchCamera);
            mSetLeftIn.setTarget(ivSwitchCamera);
            mSetRightOut.start();
            mSetLeftIn.start();
            camera.toggleFacing();
            invertCamera = true;
        } else {
            mSetRightOut.setTarget(ivSwitchCamera);
            mSetLeftIn.setTarget(ivSwitchCamera);
            mSetRightOut.start();
            mSetLeftIn.start();
            camera.toggleFacing();
            invertCamera = false;
        }
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                checkForPermission();
                // Permission was denied or request was cancelled
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_CODE) {
            checkForPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.stop();
    }

    public void setDateInItems() {
        Collections.sort(newList, new Comparator<GalleryData>() {
            @Override
            public int compare(GalleryData p, GalleryData q) {
                if (p.getDate().before(q.getDate())) {
                    return 1;
                } else if (p.getDate().after(q.getDate())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < newList.size(); i++) {
            if (i == 0) {
                GalleryData galleryData = new GalleryData();
                galleryData.setFile(newList.get(i).getFile());
                galleryData.setDate(newList.get(i).getDate());
                newList.add(1, galleryData);
                newList.get(0).setHeader(true);
            } else if (Utility.getDateInString(newList.get(i).getDate()).compareTo(Utility.getDateInString(newList.get(i - 1).getDate())) > 0) {
                GalleryData galleryData = new GalleryData();
                galleryData.setFile(newList.get(i).getFile());
                galleryData.setDate(newList.get(i).getDate());
                newList.add(i + 1, galleryData);
                newList.get(i).setHeader(true);
            }
        }

        imageAdapter.addData(newList);
    }


    @Override
    public void onItemClick(View view, int position) {
        if (imageAdapter.isItemSelected(position)) {
            selectedPositions = imageAdapter.selectUnselectItem(selectedPositions, selectionCount, position);
            if (!selectedPositions.isEmpty()) {
                selectionState = SELECTION_STATE_MULTIPLE;
                ivDone.setVisibility(View.VISIBLE);
            } else {
                selectionState = SELECTION_STATE_SINGLE;
                ivDone.setVisibility(View.GONE);
            }

        } else {
            if (selectionState == SELECTION_STATE_SINGLE)
                onPress(position);
        }

    }

    @Override
    public void onLongItemClick(View view, int position) {

        //TODO: for adding multiple items
        selectedPositions = imageAdapter.selectUnselectItem(selectedPositions, selectionCount, position);
        if (!selectedPositions.isEmpty()) {
            selectionState = SELECTION_STATE_MULTIPLE;
            ivDone.setVisibility(View.VISIBLE);
        } else {
            selectionState = SELECTION_STATE_SINGLE;
            ivDone.setVisibility(View.GONE);
        }


    }

    public void sendResultBackToActivity(ArrayList<String> files) {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra(IMAGE_KEY, files);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}