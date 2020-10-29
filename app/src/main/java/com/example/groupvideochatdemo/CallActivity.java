package com.example.groupvideochatdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.groupvideochatdemo.adapter.SubscribersAdapter;
import com.example.groupvideochatdemo.service.ForegroundServiceOngoingCall;
import com.example.groupvideochatdemo.utils.Utils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallActivity extends AppCompatActivity implements Session.SessionListener, Session.SignalListener,
        Session.StreamPropertiesListener, Session.ConnectionListener, Session.ReconnectionListener,
        PublisherKit.PublisherListener, SubscribersAdapter.RecyclerItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Session mSession;
    private Publisher mPublisher;

    private FrameLayout mPublisherViewContainerLarge, mPublisherViewContainerSmall;
    private FrameLayout mSubscriberViewContainerLarge, mSubscriberViewContainerSmall;
    private AppCompatImageView ivHangUp, ivSwitchCamera, ivMute, ivVideoIcon;
    private LinearLayout ltFlipIconPublisher;

    private RecyclerView recyclerView;
    private SubscribersAdapter subscribersAdapter;
    private ArrayList<Subscriber> mSubscribers = new ArrayList<>();
    private HashMap<Stream, Subscriber> mSubscriberStreams = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUi();
        initListener();
        initAdapter();
        requestCameraPermission();
    }

    private void initUi() {
        //publisher containers
        mPublisherViewContainerLarge = findViewById(R.id.publisherContainerFullScreen);
        mPublisherViewContainerSmall = findViewById(R.id.publisherContainerSmall);

        //subscriber container
        mSubscriberViewContainerLarge = findViewById(R.id.SubscriberContainerFullScreen);

        ivHangUp = findViewById(R.id.ivHangUp);
        ltFlipIconPublisher = findViewById(R.id.ltFlipIconPublisher);
        ivSwitchCamera = findViewById(R.id.ivSwitchCamera);
        ivMute = findViewById(R.id.ivMute);
        ivVideoIcon = findViewById(R.id.ivVideoIcon);
        ivSwitchCamera = findViewById(R.id.ivSwitchCamera);

        recyclerView=findViewById(R.id.recyclerView);

    }

    /**
     * set click events on hang-up & back icons
     */
    private void initListener() {

        /*ui.publisherContainer:  this container is used to hold view of publisher for both
        zoom & small view we will modify its layout params accordingly*/
//        ivHangUp.setOnClickListener(v ->);
//        ltFlipIconPublisher.setOnClickListener(v ->
//                smallToLargeView_Publisher(true));

        ivSwitchCamera.setOnClickListener(v -> {
            if (mPublisher != null)
                mPublisher.cycleCamera();
        });

        ivMute.setOnClickListener(v -> {
            if (mPublisher != null) {

                if (mPublisher.getPublishAudio()) {
                    mPublisher.setPublishAudio(false); //disable publisher audio
                    ivMute.setImageResource(R.drawable.ic_mike_off);
                } else {
                    mPublisher.setPublishAudio(true);  //enable publisher audio
                    ivMute.setImageResource(R.drawable.ic_mike_on);
                }
            }
        });

        ivVideoIcon.setOnClickListener(v -> {
            if (mPublisher != null) {
                if (mPublisher.getPublishVideo()) {
                    mPublisher.setPublishVideo(false); //disable publisher video
                    ivVideoIcon.setImageResource(R.drawable.ic_video_off);
                } else {
                    mPublisher.setPublishVideo(true);  //enable publisher video
                    ivVideoIcon.setImageResource(R.drawable.ic_video_on);
                }
            }
        });
    }

    /**
     * Method : to initialize adapter of recyclerView
     */
    private void initAdapter() {
        /*ui.recyclerView: this recycler is used to hold view of subscriber's
        which has to be shown as small view*/
        recyclerView.setLayoutManager(new LinearLayoutManager(CallActivity.this, RecyclerView.HORIZONTAL, true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        subscribersAdapter = new SubscribersAdapter(CallActivity.this);
        subscribersAdapter.setData(mSubscribers);
        subscribersAdapter.setCallback(this);
        recyclerView.setAdapter(subscribersAdapter);
    }


    /**
     * initialize openTok session - by consuming the session-id and token passed to establish connection
     */
    private void initSession(String API_KEY, String SESSION_ID, String TOKEN) {
        mSession = new Session.Builder(CallActivity.this, API_KEY, SESSION_ID)
                .build();

        mSession.setSessionListener(this);
        mSession.setSignalListener(this);
        mSession.setReconnectionListener(this);
        mSession.setStreamPropertiesListener(this);
        mSession.setConnectionListener(this);
        mSession.connect(TOKEN);

        // initialize Publisher and set this object to listen to Publisher events
        startPublisherPreview();
    }

    @Override
    public void onConnected(Session session) {
        Log.d(TAG, "onConnected: Connected to session: " + session.getSessionId());

        Log.i(TAG, "Session Connected - now publish mPublisher");
        if (mSession != null && mPublisher != null) {
            Log.i(TAG, "onConnected called and publisher published to session");
            mSession.publish(mPublisher);
        }
    }

    /**
     * method: to create publisher object and initialize listeners of publisherKit & startPreview
     */
    private void startPublisherPreview() {
        mPublisher = new Publisher.Builder(CallActivity.this)
                .name("Publisher")
                .resolution(Publisher.CameraCaptureResolution.LOW)
                .frameRate(Publisher.CameraCaptureFrameRate.FPS_7)
                .build();
        mPublisher.setPublisherListener(this);
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        if (mPublisher != null) {
            if (mPublisher.getView().getParent() != null)
                ((ViewGroup) mPublisher.getView().getParent()).removeView(mPublisher.getView()); // <- fix for crash occur while .addView(framelayout)
            mPublisherViewContainerLarge.addView(mPublisher.getView());
        }
    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

    }

    @Override
    public void onConnectionCreated(Session session, Connection connection) {

    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {

    }

    @Override
    public void onReconnecting(Session session) {

    }

    @Override
    public void onReconnected(Session session) {

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.d(TAG, "error");
    }

    @Override
    public void onSignalReceived(Session session, String s, String s1, Connection connection) {

    }

    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {

    }

    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {

    }

    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i1) {

    }

    @Override
    public void onStreamVideoTypeChanged(Session session, Stream stream, Stream.StreamVideoType streamVideoType) {

    }

    private void requestCameraPermission() {

        Dexter.withActivity(this).withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    // initialize and connect to the session- openTok
                    initSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN);
                } else {
                    showSetting();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).withErrorListener(error -> showSetting()).onSameThread().check();
    }

    /**
     * method: to open setting screen to grant permissions manually
     */
    private void showSetting() {
        Utils.showAlert(this, getString(R.string.permission_req), isTrue -> {
            if (isTrue) {
                launchSetting();
            } else {
                finish();
            }
        });
    }

    /**
     * method: to launch default setting screen on device
     */
    private void launchSetting() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", this.getPackageName(), (String) null);
        intent.setData(uri);
        startActivityForResult(intent, 233);
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundServiceOngoingCall.class);
        serviceIntent.putExtra("inputExtra", "You have a Ongoing Video Call");
        ContextCompat.startForegroundService(this, serviceIntent);
        Log.i(TAG, "service started");
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundServiceOngoingCall.class);
        stopService(serviceIntent);
        Log.i(TAG, "service stopped");
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onMuteSubscriberIconClick(Subscriber subscriber, int position, View view) {

    }

    @Override
    public void onRecyclerItemClick(Subscriber subscriber, int position, View view) {

    }
}