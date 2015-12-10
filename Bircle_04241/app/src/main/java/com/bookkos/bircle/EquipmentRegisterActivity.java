package com.bookkos.bircle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class EquipmentRegisterActivity extends Activity {
    public static Activity activity;
    public static final String PREFERENCES_FILE_NAME = "user_preference";
    public static int userId;
    public static int groupId;
    private String regId;
    private String groupName;
    private JSONArray mFormJArr;
    NumberPicker numberpicker;
    public int registerNumber;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    static  final int register_row_id_start = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_equipment_register);
        getUserData();
        activity=this;
        numberpicker=(NumberPicker)findViewById(R.id.numberPicker);
        numberpicker.setMinValue(1);
        numberpicker.setMaxValue(100);
        numberpicker.setValue(1);
        numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {


            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                // TODO Auto-generated method stub
                registerNumber = numberpicker.getValue();

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                final TableLayout registerTable = (TableLayout)findViewById(R.id.register_table);
                registerTable.removeAllViews();
                for(int i = 0; i < numberpicker.getValue(); i++){
                    View rowView = inflater.inflate(R.layout.test, null);
                    rowView.setId(register_row_id_start + i);
                    registerTable.addView(rowView);
                }
            }

        });
        Button locationActivitySwitchButton=(Button)findViewById(R.id.button);
        locationActivitySwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                EditText editText=(EditText)findViewById(R.id.Text_name);
                String strname=editText.getText().toString();
                final TableLayout registerTable = (TableLayout)findViewById(R.id.register_table);

                int rowCount = numberpicker.getValue();
                JSONArray jarr = new JSONArray();
                JSONObject jobj = new JSONObject();

                for(int i = 0; i < rowCount; i++){
                    final TableRow row = (TableRow)registerTable.findViewById(register_row_id_start + i);
                    final EditText cellID = (EditText)row.findViewById(R.id.cellID);
                    final EditText cellLoc = (EditText)row.findViewById(R.id.cellLoc);
                    final EditText cellDesc = (EditText)row.findViewById(R.id.cellDesc);

                    try{
                        JSONObject jobj1 = new JSONObject();
                        jobj1.put("id", cellID.getText());
                        jobj1.put("loc", cellLoc.getText());
                        jobj1.put("desc", cellDesc.getText());
                        jobj1.put("userid",userId);
                        jobj1.put("groupid",groupId);
                        jobj1.put("equipmentname",strname);
                        jobj1.put("registernumber",registerNumber);
                        jarr.put(jobj1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.v("TEST", jarr.toString());
                }

                Log.v("TEST", jarr.toString());

                mFormJArr = jarr;
                //HttpConnectPost register = new HttpConnectPost(activity,"http://130.158.80.42:80/picupload/oneupload",this);
                Intent intent = new Intent(getApplicationContext(),ContinuosShooting.class);
                intent.putExtra("strname",strname);
                startActivityForResult(intent, 110011);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(110011 == requestCode){
            try {
                post("http://130.158.80.42:80/equipmentregistration/getjson",mFormJArr.toString());
                finish();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    private void getUserData() {
        // preferenceフォルダにあるxmlファイルから値を取得する
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        //
        if(settings == null) {
            return ;
        }

        userId = (int) settings.getLong("user_id", 0);
        groupId = (int) settings.getLong("group_id", 0);
        regId = settings.getString("reg_id", "");
        groupName = settings.getString("group_name", "");

    }

}
