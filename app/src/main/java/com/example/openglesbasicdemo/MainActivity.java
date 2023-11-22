package com.example.openglesbasicdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.opengl.GLSurfaceView;
import java.util.Timer;
import java.util.TimerTask;

import android.telephony.TelephonyCallback;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.ActionBar;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glViewport;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private BasicTools mBasicTools = null;
    private GLSurfaceView mGlView;
    private Timer mRefreshTimer;
    private Window mWindow = null;
    private ActionBar mActionBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWindow = getWindow();
        mActionBar = getSupportActionBar();
        if (mWindow != null) {
            mWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE); // 使用activity的window是隐藏虚拟按键。
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (mActionBar != null) {
            mActionBar.hide();
        }

        mGlView = findViewById(R.id.glview);
        mGlView.setEGLContextClientVersion(3);
        mGlView.setRenderer(this);
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRefreshTimer = new Timer(true);
        mRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mGlView.requestRender();
            }
        }, 33, 33);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlView.queueEvent(new Runnable() {
            public void run() {
                if(mBasicTools != null) {
                    mBasicTools.uninit();
                    mBasicTools = null;
                }
            }
        });
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        if (null == mBasicTools) {
            mBasicTools = new BasicTools();
        }
    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        glViewport(0, 0, i, i1);
    }
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        mBasicTools.drawRect(-0.5f, 0.5f, 0.5f, -0.5f, 0, 1, 0, 1, 2);
        mBasicTools.drawLine(-0.5f, 0.5f, 0.5f, -0.5f, 0, 1, 0, 1, 2);
        mBasicTools.drawPoint(0.1f, 0, 0, 1, 0, 1, 8);
    }
}