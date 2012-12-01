package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SmsSettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_settings);
        
        final SharedPreferences sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        
        final EditText numberEditText = (EditText) findViewById(R.id.numberEditText);
        final EditText smsEditText = (EditText) findViewById(R.id.smsEditText);

        smsEditText.setText(sharedPreferences.getString("smsText", "HomeGuard detected movement!"));
        
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
				sharedPreferencesEditor.putString("receivingNumber", numberEditText.getText().toString());
				sharedPreferencesEditor.putString("smsText", smsEditText.getText().toString());
				sharedPreferencesEditor.commit();
				SmsSettingsActivity.this.finish();
			}
		});
    }
}
