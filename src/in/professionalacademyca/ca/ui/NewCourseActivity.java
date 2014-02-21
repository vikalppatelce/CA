package in.professionalacademyca.ca.ui;

import in.professionalacademyca.ca.R;
import in.professionalacademyca.ca.app.AppConstants;
import in.professionalacademyca.ca.app.CA;
import in.professionalacademyca.ca.ui.utils.CustomToast;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NewCourseActivity  extends SherlockFragmentActivity implements OnItemSelectedListener{

	Spinner spin_course,spin_city,spin_area,spin_batch;
	CheckBox chk_default;
	TextView header,txtcourse,txtbatch;
	Button go;
	ActionBar actionBar;
	
	ArrayAdapter adap_course,adap_city,adap_area,adap_batch;
	
	Typeface stylefont;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_course_select);
		
		stylefont = Typeface.createFromAsset(getAssets(), AppConstants.fontStyle);
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Course");
		
		fontActionBar(actionBar.getTitle().toString());
		actionBar.setIcon(android.R.drawable.ic_menu_set_as);
		
		
		spin_course = (Spinner)findViewById(R.id.spin_course);
		spin_batch = (Spinner)findViewById(R.id.spin_batch);
		
		chk_default = (CheckBox)findViewById(R.id.set_default);
		
		header = (TextView)findViewById(R.id.headercourse);
		txtbatch = (TextView)findViewById(R.id.batch);
		txtcourse = (TextView)findViewById(R.id.course);
		go =(Button)findViewById(R.id.go);
				
		chk_default.setTypeface(stylefont);
		header.setTypeface(stylefont);
		txtbatch.setTypeface(stylefont);
		txtcourse.setTypeface(stylefont);
		go.setTypeface(stylefont);
		
		adap_course = ArrayAdapter.createFromResource(this, R.array.arr_course_level, android.R.layout.simple_spinner_dropdown_item);
		
		spin_course.setOnItemSelectedListener(this);
		adap_course.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_course.setAdapter(adap_course);
		
		spin_batch.setOnItemSelectedListener(this);
		
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
	
	public void onGo(View v)
	{
		if(validate())
		{
			if(chk_default.isChecked())
			{
				CA.getPreferences().setDefault(true);
				CA.getPreferences().setBatch(spin_batch.getSelectedItem().toString().trim());
			}
			Intent timeTable = new Intent(this, TimeTableActivity.class);
			timeTable.putExtra("isBatch", spin_batch.getSelectedItem().toString().trim());
			startActivity(timeTable);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		}
		else
		{
			CustomToast.showToastMessage(this,"Please select Batch");
		}
	}
	
	public boolean validate()
	{
		if(spin_batch.getSelectedItem().toString()!=null && spin_batch.getSelectedItem().toString().length() > 0)
		{
		return true;	
		}
		return false;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		int pos_course,pos_city ,pos_area ,pos_batch;
		int which_spinner;
		
		pos_course = spin_course.getSelectedItemPosition();
		which_spinner = parent.getId();
		if(which_spinner == R.id.spin_course)
		{
			switch(pos_course)
			{
			case 0:
				adap_batch = ArrayAdapter.createFromResource(this, R.array.arr_level_batch, android.R.layout.simple_spinner_dropdown_item);
				break;
			case 1:
				adap_batch = ArrayAdapter.createFromResource(this, R.array.arr_level_batch2, android.R.layout.simple_spinner_dropdown_item);
				break;
			}
			adap_batch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_batch.setAdapter(adap_batch);
		}
		else if(which_spinner == R.id.spin_batch)
		{
			
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}