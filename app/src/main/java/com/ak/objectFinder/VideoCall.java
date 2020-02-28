package com.ak.objectFinder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
//import com.vidyo.VidyoClient.Connector.VidyoConnector;
import com.vidyo.VidyoClient.Connector.Connector;

public class VideoCall extends AppCompatActivity implements Connector.IConnect{

//    private VidyoConnector vidyoConnector;
    private FrameLayout videoFrame;
    private Connector mVidyoConnector = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout)findViewById(R.id.video_frame);
    }

    public void onClickStart(View view) {
        mVidyoConnector = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default,
                7,
                "",
                "",
                0);
        mVidyoConnector.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
    }

    public void onClickConnect(View view) {
        String token = "cHJvdmlzaW9uAEV5ZVNweUAxNjRiOTUudmlkeW8uaW8ANjM3NTAxMTg3MDQAADZmNGY2MTRjMWFkZWUwMTE3ZDA3Y2E0NmE2ODJkZjM1YjZlMDZhMWQzMTliNDUzZjJjM2FkMTVlYWZjMThkMTE1ZTg0YTVkNDI2YWYyODllNzVlYTJiOTQ0MGNiNDFjYg==";
        mVidyoConnector.connect("prod.vidyo.io", token, "Dmo", "DemoRoom", this);
    }

    public void onClickDisconnect(View view) {
        mVidyoConnector.disconnect();
    }

    public void onSuccess() {
        Log.d("videoStatus", "success");
    }

    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {
        Log.d("videoStatus", "failure");
    }

    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {
        Log.d("videoStatus", "success");
    }
}
