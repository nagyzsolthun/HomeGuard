package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/** settings of email service*/
public class EmailSettingsActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_settings);
        
        final SharedPreferences sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        
        final EditText sendingEmailTextEdit = (EditText) findViewById(R.id.sendingEmailEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText receivingEmailEditText = (EditText) findViewById(R.id.receivingEmailEditText);

        sendingEmailTextEdit.setText(sharedPreferences.getString("sendingEmail", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));
        receivingEmailEditText.setText(sharedPreferences.getString("receivingEmail", ""));
        
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
				sharedPreferencesEditor.putString("sendingEmail", sendingEmailTextEdit.getText().toString());
				sharedPreferencesEditor.putString("password", passwordEditText.getText().toString());
				sharedPreferencesEditor.putString("receivingEmail", receivingEmailEditText.getText().toString());
				sharedPreferencesEditor.commit();
				EmailSettingsActivity.this.finish();
			}
		});
    }
}
