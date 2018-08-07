package com.example.ready.studytimemanagement.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ready.studytimemanagement.R;
import com.example.ready.studytimemanagement.model.Data;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.File;
import java.util.ArrayList;

public class CheckActivity extends LoginController implements View.OnClickListener{

    final static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DataLog/datalog.txt";
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DataLog";
    final static String filename = "datalog.txt";

    private EditText category;
    private EditText date;
    private EditText time;
    private EditText appname;
    private TextView readcategory;
    private TextView readdate;
    private TextView readtime;

    ArrayList<Data> mData;

    LogfileController lfc;
    AppLockController alc;

    checkThread th;
    Activity act;

    boolean checkFlag;

    private class checkThread extends Thread{
        public void run() {
            int num = 1;
            while(checkFlag) {
                alc.CheckRunningApp(act);
                Log.d("Thread", "" + num++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_check);

        findViewById(R.id.logout).setOnClickListener(this);

        TextView nameText = (TextView)findViewById(R.id.name);
        TextView emailText = (TextView)findViewById(R.id.email);
        Intent intent = getIntent();
        nameText.setText(intent.getStringExtra("ID").toString() );
        emailText.setText(intent.getStringExtra("EMAIL").toString() );

        category = (EditText)findViewById(R.id.category);
        date = (EditText)findViewById(R.id.date);
        time = (EditText)findViewById(R.id.time);
        appname = (EditText)findViewById(R.id.appname);

        readcategory = (TextView)findViewById(R.id.readcategory);
        readdate = (TextView)findViewById(R.id.readdate);
        readtime = (TextView)findViewById(R.id.readtime);

        mData = new ArrayList<Data>();

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.load).setOnClickListener(this);

        findViewById(R.id.applist).setOnClickListener(this);

        findViewById(R.id.lock).setOnClickListener(this);

        lfc = new LogfileController();
        alc = new AppLockController();

        act = this;
        checkFlag = false;

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.logout) {
            //구글 로그아웃
            mAuth.signOut();
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.v("구글 완료","구글");
                        }
                    });

            //페이스북 로그아웃
            LoginManager.getInstance().logOut();

            //카카오 로그아웃
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Log.v("카카오 완료","카카오");
                }
            });

            //로그파일 삭제
            File file = new File(filePath);
            file.delete();

            final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }else if(i == R.id.save){
            String category_s = category.getText().toString();
            String date_s = date.getText().toString();
            String time_s = time.getText().toString();

            String content = "#####\r\n";
            content = content+"#CATEGORY=="+category_s+"\r\n"+"#DATE=="+date_s+"\r\n"+"#TIME=="+time_s+"\r\n";

            lfc.WriteLogFile(foldername,filename,content,true);
            Log.d("datalog","write complete!!");
        }else if(i==R.id.load){
            String line = lfc.ReadLogFile(filePath);

            String[] dataSet = line.split("#####");
            for(int j=1; j<dataSet.length; j++){
                String[] data = dataSet[j].split("#");
                for(int k=1; k<data.length; k++){               // category -> date -> time
                    data[k] = data[k].substring(data[k].indexOf("==")+2);
                }
                Data d = new Data();
                d.setCategory(data[1]);
                d.setDate(data[2]);
                d.setAmount(data[3]);
                d.setTarget_time("7");
                mData.add(d);
            }

            readcategory.setText(mData.get(mData.size()-1).getCategory());
            readdate.setText(mData.get(mData.size()-1).getDate());
            readtime.setText(mData.get(mData.size()-1).getAmount());
        }else if(i==R.id.applist){
            alc.LoadAppList(this);
            if(checkFlag==false) {
                th = new checkThread();
                th.start();
            }else {
                th = null;

                Log.d("Thread", "쓰레드 뿌셔");
            }
            checkFlag = !checkFlag;
        }else if(i==R.id.lock){
            String app = appname.getText().toString();
            alc.AppLock.add(app);
            Log.d("Add Lock APP",app);
        }
    }
}
