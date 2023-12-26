package com.itschool.service_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    int i = 0;
    Button button;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);
        button.setOnClickListener(v -> {
            i++;
            textView.setText(String.valueOf(i));
        });

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWork.class).build();
        WorkManager.getInstance(this).enqueue(work);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(work.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    int value = workInfo.getOutputData().getInt("myKey", 0);
                    textView.setText(String.valueOf(value));
                }
            }
        });

    }


    public static class MyWork extends Worker {

        public MyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @SuppressLint("RestrictedApi")
        @NonNull
        @Override
        public Result doWork() {
            Log.i("MyWork", "Start working; Thread " + Thread.currentThread().getName());
            try {
                Thread.sleep(8_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return Result.success(new Data.Builder()
                    .putInt("myKey", new Random().nextInt() % 1000)
//                    .put("object", new MyResultData())
                    .build());
        }
    }
}


class Observable {
    List<Listener> subscribers = new ArrayList<>();

    void foo() {
        for (Listener l : subscribers) {
            l.onEvent(new MyResultData());
        }
    }

    public void subscribe(Listener listener) {
        subscribers.add(listener);
    }
}

interface Listener {
    void onEvent(MyResultData myResultData);
}


class MyResultData {
    int value;

    MyResultData() {
        this.value = new Random().nextInt() % 1000;
    }

}