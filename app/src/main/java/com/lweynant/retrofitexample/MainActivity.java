package com.lweynant.retrofitexample;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity implements Callback<StackOverflowQuestions> {

    public static final String TAG = "MainActivity";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<Question> arrayAdapter = new ArrayAdapter<Question>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<Question>());
        listView.setAdapter(arrayAdapter);
        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);

    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected");
        doNetworkCall();
        return super.onOptionsItemSelected(item);
    }

    private void doNetworkCall() {
        setProgressBarIndeterminateVisibility(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StackOverflowAPI stackOverflowAPI = retrofit.create(StackOverflowAPI.class);
        Call<StackOverflowQuestions> call = stackOverflowAPI.loadQuestions("android");
        //synchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<StackOverflowQuestions> call, Response<StackOverflowQuestions> response) {
        setProgressBarVisibility(false);
        ArrayAdapter<Question> adapter = (ArrayAdapter<Question>) listView.getAdapter();
        adapter.clear();
        adapter.addAll(response.body().items);
    }

    @Override public void onFailure(Call<StackOverflowQuestions> call, Throwable t) {
        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
}
