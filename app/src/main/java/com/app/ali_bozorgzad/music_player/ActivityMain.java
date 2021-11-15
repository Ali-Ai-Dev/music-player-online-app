package com.app.ali_bozorgzad.music_player;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    public static ArrayList<String> musicInfoID = new ArrayList<>();
    public static ArrayList<String> musicName = new ArrayList<>();
    public static ArrayList<String> artistName = new ArrayList<>();
    public static ArrayList<String> musicPicture = new ArrayList<>();
    public static ArrayList<String> musicFile = new ArrayList<>();

    private AdapterMusicList adapterMusicList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvMusicList = (RecyclerView) findViewById(R.id.rvMusicList);
        rvMusicList.setLayoutManager(new LinearLayoutManager(ActivityMain.this,LinearLayoutManager.VERTICAL,false));
        adapterMusicList = new AdapterMusicList(ActivityMain.this);
        rvMusicList.setAdapter(adapterMusicList);

        checkConnectionAndReceiveData();
    }

    private void checkConnectionAndReceiveData() {
        if (ConnectedToInternet()){
            receiveDataFromServer();
        }else{
            dialogErrorConnection();
        }
    }

    private boolean ConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if(connectivityManager.getActiveNetworkInfo() != null){
                return true;
            }
        }
        return false;
    }

    private void dialogErrorConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
        builder.setTitle("عدم اتصال به اینترنت");
        builder.setMessage("لطفا اتصال اینترنت خود را بررسی کنید");
        builder.setIcon(R.mipmap.offline);

        builder.setPositiveButton("تلاش دوباره", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
                checkConnectionAndReceiveData();
            }
        });

        builder.setNegativeButton("خروج از برنامه", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    private void receiveDataFromServer(){
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("password", "passwordMusicPlayerOnline");

        //new WebHttpConnection("http://192.168.13.169/music-player-online/fetchData.php","POST" , builder)
        new WebHttpConnection("http://music-player-online.alibozorgzadarbab.ir/fetchData.php","POST" , builder)
        {
            @Override public void onPostExecute(String result)
            {
                if(result != null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            musicInfoID.add(object.getString("musicInfo_id"));
                            musicName.add(object.getString("musicName"));
                            artistName.add(object.getString("artistName"));
                            musicPicture.add(object.getString("musicPicture"));
                            musicFile.add(object.getString("musicFile"));
                        }
                        adapterMusicList.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    dialogErrorConnection();
                }
            }
        }.execute();
    }
}