package com.android.study;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private String TAG = "wangxin666";
    private Button btn1;
    private EditText et1;
    private ProgressBar pb1;
    private Toolbar tb1;
    private CustomChildThread customThread;
    private Notification notification;
    private NotificationManager manager;

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String type = (String) msg.obj;
            switch (type) {
                case "modify_i":
                    //获取 ProgressBar 的进度，然后显示进度值
                    int process = (int) msg.what;
                    pb1.setProgress(process);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        createNotification();
        //实例化customThread
        customThread = new CustomChildThread(mHandler);
    }

    private void findView() {
        btn1 = findViewById(R.id.btn_1);
        btn1.setOnClickListener(this);
        btn1.setOnLongClickListener(this);
        btn1.setOnTouchListener(this);

        et1 = findViewById(R.id.et_1);
        pb1 = findViewById(R.id.pb_1);
        tb1 = findViewById(R.id.tb_1);
        tb1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Toolbar is clicked");
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                //popupView
                popupView(v);
                break;
            default:
                break;
        }
    }
    //长按事件
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                progressBar();
                cancelNotification();
                Log.d(TAG, "onLongClick: " + v.getId());
                return true;
            default:
                return true;
        }
    }
    //触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btn_1:
                Log.d(TAG, "onTouch: " + event.getAction());
                return false;
            default:
                return false;
        }
    }

    //进度条显示
    public void isPbShow() {
        if (pb1.getVisibility() == View.GONE) {
            pb1.setVisibility(View.VISIBLE);
        }else {
            pb1.setVisibility(View.GONE);
        }
    }
    //进度条累加
    private void progressBar() {
        if (!customThread.isAlive()) {
            customThread = new CustomChildThread(mHandler);
            customThread.start();
        }
    }

    //创建Notification
    private void createNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //开启通知 状态栏显示 提示语 弹出
            NotificationChannel channel = new NotificationChannel("sue", "test", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        //跳转用的是PendingIntent类型
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //channelId要与channel中的id保持一致
        notification = new NotificationCompat.Builder(this, "sue")
                .setContentTitle("标题")
                .setContentText("内容")
                .setSmallIcon(R.drawable.ic_notifications_1)//小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_notifications_2))//大图标
                .setColor(Color.parseColor("#19ABED"))//设置小图标颜色
                .setContentIntent(pendingIntent)//跳转
                .setAutoCancel(true)//点击消失
                .build();
    }
    //发送通知
    public void sendNotification() {
        manager.notify(1,notification);
    }
    //取消通知，id要与发送的id一致
    private void cancelNotification() {
        manager.cancel(1);
    }

    private void popupView(View v) {
        //绑定布局文件
        View popupView = getLayoutInflater().inflate(R.layout.popup_view, null);

        Button btn1 = popupView.findViewById(R.id.btn_pop1);
        Button btn2 = popupView.findViewById(R.id.btn_pop2);
        Button btn3 = popupView.findViewById(R.id.btn_pop3);

        //设置弹出框大小，点击空白取消
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.showAsDropDown(v,v.getWidth()+300,-v.getHeight());

        //按钮
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知栏
                sendNotification();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进度条
                isPbShow();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框内容
                String et = et1.getText().toString();
                Log.d(TAG, "onClick: " + et);
                //关闭popupWindow
                popupWindow.dismiss();
            }
        });
    }

}