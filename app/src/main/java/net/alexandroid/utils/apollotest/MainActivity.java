package net.alexandroid.utils.apollotest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.TasksQuery;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.type.TasksSearchInput;

import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("http://104.196.187.249:3000/api/")
                .okHttpClient(new OkHttpClient())
                .build();

        TasksSearchInput tasksSearchInput = TasksSearchInput.builder()
                .searchFromTimestamp((double) System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 10)
                .searchToTimestamp((double) System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10)
                .build();


        apolloClient.query(TasksQuery.builder().tasks(tasksSearchInput).build())
                .enqueue(new ApolloCall.Callback<TasksQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<TasksQuery.Data> response) {
                        Log.d("ZAQ", "Response");
                        TasksQuery.Data data = response.data();
                        if (data != null) {
                            List<TasksQuery.Task> list = data.tasks();
                            for (TasksQuery.Task task : list) {
                                Log.d("ZAQ", "" + task.title() + " - " + task);
                            }
                        } else {
                            Log.d("ZAQ", "Data null");
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.e("ZAQ", "onFailure: " + e.getMessage());
                    }
                });
    }
}
