package com.ak.objectFinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoAgorio extends AppCompatActivity {

    private static final String TAG = VideoAgorio.class.getSimpleName();

    private static final int PERMISSION_RED_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine rtcEngine;
    private FrameLayout localContainer;
    private RelativeLayout remoteContainer;
    private SurfaceView localView;
    private SurfaceView remoteView;

    private ImageView callBtn;
    private ImageView muteBtn;
    private ImageView switchCameraBtn;

    private boolean mCallEnd;
    private boolean mMuted;

    private IRtcEngineEventHandler RtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "User offline, uid: " + (uid & 0xFFFFFFFFL));
                    removeRemoteVideo();
                }
            });
        }

//        @Override
//        public void onFirstRemoteVideoFrame(final int uid, int width, int height, int elapsed) {
//            super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("agora", "First remote video frame, uid: " + (uid & 0xFFFFFFFFL));
//                    setupRemoteVideo(uid);
//                }
//            });
//        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_agorio);
        String callId = getIntent().getStringExtra("callId");
        if (callId != null) {
            FirebaseAPI.joinCall(callId);
        } else {
            FirebaseAPI.startCall();
        }
        
        initUi();

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_RED_ID) &&
            checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_RED_ID) &&
            checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_RED_ID)) {
            initEngineAndJoinChannel();
        }
        initEngineAndJoinChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
    }

    private void initUi() {
        localContainer = findViewById(R.id.local_video_vc);
        remoteContainer = findViewById(R.id.remote_video_vc);
        callBtn = findViewById(R.id.btn_call);
        muteBtn = findViewById(R.id.btn_mute);
        switchCameraBtn = findViewById(R.id.btn_switch_camera);
    }

    private Boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), RtcEventHandler);
        }
        catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("Need to check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        rtcEngine.enableVideo();
        rtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ));
    }

    private void setupLocalVideo() {
        rtcEngine.enableVideo();
        localView = RtcEngine.CreateRendererView(getBaseContext());
        localView.setZOrderMediaOverlay(true);
        localContainer.addView(localView);

        VideoCanvas localVideoCanvas = new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        rtcEngine.setupLocalVideo(localVideoCanvas);
    }

    private void setupRemoteVideo(int uid) {
        int count = remoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i<count; i++) {
            View v = remoteContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        remoteView = RtcEngine.CreateRendererView(getBaseContext());
        remoteContainer.addView(remoteView);
        rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        remoteView.setTag(uid);
    }

    private void removeRemoteVideo() {
        if (remoteView != null) {
            remoteContainer.removeView(remoteView);
        }
        remoteView = null;
    }

    private void joinChannel() {
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token)) {
            token = null;
        }
        rtcEngine.joinChannel(token, "EyeSpy65", "", 0);
    }

    private void leaveChannel() {
        rtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        rtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        muteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            callBtn.setImageResource(R.drawable.btn_endcall);
        }
        else {
            endCall();
            mCallEnd = true;
            callBtn.setImageResource(R.drawable.btn_startcall);
        }

        showButtons(!mCallEnd);
    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
    }

    private void removeLocalVideo() {
        if (localView != null) {
            localContainer.removeView(localView);
        }
        localView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        muteBtn.setVisibility(visibility);
        switchCameraBtn.setVisibility(visibility);
    }

}