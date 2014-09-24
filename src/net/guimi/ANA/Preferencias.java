package net.guimi.ANA;
/* basado en http://www.kaloer.com/android-preferences */

import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class Preferencias extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
	}
}