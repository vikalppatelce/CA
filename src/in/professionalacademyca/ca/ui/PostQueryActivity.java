package in.professionalacademyca.ca.ui;

import in.professionalacademyca.ca.R;
import in.professionalacademyca.ca.app.AppConstants;
import in.professionalacademyca.ca.dto.QueryDTO;
import in.professionalacademyca.ca.service.RequestBuilder;
import in.professionalacademyca.ca.service.ServiceDelegate;
import in.professionalacademyca.ca.sql.DBConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PostQueryActivity extends SherlockFragmentActivity{

	Button post;
	EditText query;
	
	ActionBar actionBar;
	Typeface stylefont;
	
	ArrayList<QueryDTO> queryDTOs;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_query);
		stylefont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Post Query");
		
		fontActionBar(actionBar.getTitle().toString());
		actionBar.setIcon(android.R.drawable.ic_menu_help);
		
		post= (Button)findViewById(R.id.post);
		query = (EditText)findViewById(R.id.query);
		
		post.setTypeface(stylefont);
		query.setTypeface(stylefont);
	}
	
	public void fontActionBar(String str)
	{
		try {
			int titleId = getResources().getIdentifier("action_bar_title",
					"id", "android");
			TextView yourTextView = (TextView) findViewById(titleId);
			yourTextView.setText(str);
			yourTextView.setTypeface(stylefont);
		} catch (Exception e) {
			Log.e("ActionBar Style", e.toString());
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	       inflater.inflate(R.menu.main, menu);
		 return true;
	}
	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_right);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            return true;
        case R.id.notification: 
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public void onPost(View v)
	{
      if(validate())
      {
    	  Log.e("Saving", "Saving");
          saveQuery(query.getText().toString().trim());
          if (isNetworkAvailable()) {
              loadQueryData();
              uploadQueryData();
  		}
  		else
  		{
  			Toast.makeText(PostQueryActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
  		}
      }
      else
      {
    	  query.setError("Please enter query");
      }
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	public void uploadQueryData()
	{
		JSONObject finalJSON = new JSONObject();
		JSONObject tables = new JSONObject();
		try
		{
			if(queryDTOs != null && queryDTOs.size() > 0)
			{
				JSONArray exp = RequestBuilder.getQueryDetails(queryDTOs);
				tables.put("data", exp);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.e("jSON - Query", e.toString());
		}		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String currentSIMImsi = mTelephonyMgr.getDeviceId();
		
		JSONObject jsonObject = RequestBuilder.getQueryData(currentSIMImsi, tables);
		Log.e("VIKALP--------------->>>>>>>>>>", jsonObject.toString());
		Log.e("VIKALP--------------->>>>>>>>>>", tables.toString());
		UploadTask uploadTask = new UploadTask();
		uploadTask.execute(new JSONObject[]{jsonObject});
	}
	
	public void clearQuery()
	{
		query.setText("");
	}
	
	public void saveQuery(String str)
	{
		Bundle b = new Bundle();
		b.putString("message", "Saving");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));
		if (date.contains("/")) {
			date = date.replace("/", "-");
		}
		
		ContentValues dataValues = new ContentValues();
		dataValues.put(DBConstant.Query_Columns.COLUMN_QUERY, str);
		dataValues.put(DBConstant.Query_Columns.COLUMN_DATE, date);
		dataValues.put(DBConstant.Query_Columns.COLUMN_SYNC_STATUS, "0");
		getContentResolver().insert(DBConstant.Query_Columns.CONTENT_URI,dataValues);		
		clearQuery();
	}
	
	public boolean validate()
	{
		if(!TextUtils.isEmpty(query.getText().toString()))
		{
			return true;
		}
		return false;
	}
	
	public void loadQueryData()
	{
		String _id;
		String query;
		String date;

		Cursor c = getContentResolver().query(DBConstant.Query_Columns.CONTENT_URI, null, DBConstant.Query_Columns.COLUMN_SYNC_STATUS +"=?", new String[]{"0"}, null);
		if( c != null && c.getCount() > 0)
		{
			queryDTOs = new ArrayList<QueryDTO>();
			while(c.moveToNext())
			{
				_id = c.getString(c.getColumnIndex(DBConstant.Query_Columns.COLUMN_ID));
				query = c.getString(c.getColumnIndex(DBConstant.Query_Columns.COLUMN_QUERY));
				date = c.getString(c.getColumnIndex(DBConstant.Query_Columns.COLUMN_DATE));
				queryDTOs.add(new QueryDTO(_id, query, null, date,null));
			}
			c.close();

		}
	}

	
	
	private class UploadTask extends AsyncTask<JSONObject, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = params[0];
			String jsonString = dataToSend.toString();
			try {
				String str = ServiceDelegate.postData(AppConstants.URLS.BASE_URL, dataToSend);
				/*				
				//str = "{\"tables\":{\"service\":[]},\"lov\":{\"location\":[\"L 1\"],\"expense_category\":[],\"patient_type\":[\"OPD\",\"IPD\",\"SX\"],\"payment_mode\":[\"M2\",\"M1\"],\"diagnose_procedure\":[],\"referred_by\":[\"R2\",\"R1\"],\"start_time\":[],\"surgery_level\":[\"Level : 7\",\"Level : 6\",\"Level : 5\",\"Level : 4\",\"Level : 3\",\"Level : 2\",\"Level : 1\"],\"team_member\":[],\"ward\":[]}}";
				UploadDataResponseDTO responseDTO = ResponseParser.getUploadDataResponse(str);
				
				if(responseDTO != null)
				{
					// update services
					
					String _data = responseDTO.getData();
					if(_data!= null && !_data.equals("[]") && !_data.equals(""))
					{
						_data = _data.substring(1, _data.length() - 1);
						{
							ContentValues contentValues = new ContentValues();
							contentValues.put(DBConstant.Data_Columns.COLUMN_SYNC_STATUS, 1);
							String[] data =  _data.split(",");
							if(data.length > 0)
							{
								for(int i = 0; i < data.length; i++)
								{
									String s = data[i];
									if(data[i].startsWith("\"") && data[i].endsWith("\""))
									{
										s = data[i].substring(1, data[i].length() - 1);
									}
									try
									{
										int col = getContentResolver().update(DBConstant.Data_Columns.CONTENT_URI, contentValues, DBConstant.Data_Columns.COLUMN_ID +"=?", new String[]{s});
										Log.e("ROWS UPDATED : data : ", col +"");
									}
									catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
								}
							}
							
						}
					}	
				}*/
				Log.e("UploadTask","");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}
