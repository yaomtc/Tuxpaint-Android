package org.tuxpaint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.content.res.AssetManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

public class ConfigActivity extends Activity {
	private static final String TAG = ConfigActivity.class.getSimpleName();
   	String[] languages = null;
   	String [] langs = null;
   	String[] printdelays = null;
        String[] buttonsizes = null;
        String[] colors_rows = null;
	private Properties props = null;
	private Properties propsback = null;
    String locLanguage = null;
    String locRegion = null;
    String locScript = null;
    String locVariant = null;

	// current the configurable properties
	String autosave = null;
	String sound = null;
	String stereo = null;
	String language = null;
	String savedir = null;
	String datadir = null;
	String exportdir = null;
	String saveover = null;
	String startblank = null;
	String newcolorsfirst = null;
	String sysfonts = null;
	String print = null;
        String printdelay = null;
        String disablescreensaver = null;
        String orient = null;
        String buttonsize = null;
        String colorsrows = null;
        String lang = null;
        boolean cancel = false;

	EditText savedirView = null;
	EditText datadirView = null;
	EditText exportdirView = null;
	ToggleButton soundToggle = null;
	ToggleButton stereoToggle = null;
	ToggleButton autosaveToggle = null;
	ToggleButton startblankToggle = null;
	ToggleButton newcolorsfirstToggle = null;
	Spinner languageSpinner = null;
	RadioGroup saveoverGroup = null;
	ToggleButton sysfontsToggle = null;
	ToggleButton printToggle = null;
	Spinner printdelaySpinner = null;
        ToggleButton disablescreensaverToggle = null;
        ToggleButton orientToggle = null;
        Spinner buttonsizeSpinner = null;
        Spinner colorsrowsSpinner = null;
    Button okButton = null;
    Button cancelButton = null;
        AssetManager mgr;
    
    Resources res;
    Configuration conf;
    Locale localeback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");
	super.onCreate(savedInstanceState);

	res = getResources();
	conf = res.getConfiguration();
	localeback = conf.locale;

	if (android.os.Build.VERSION.SDK_INT > 22) {
	    if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
		this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
	    }
	}

		setContentView(R.layout.config);
		load ();
		
		savedirView = (EditText)this.findViewById(R.id.textSavedir);
		savedirView.setText(savedir);
		savedirView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				savedir = savedirView.getText().toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		datadirView = (EditText)this.findViewById(R.id.textDatadir);
		datadirView.setText(datadir);
		datadirView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				datadir = datadirView.getText().toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		exportdirView = (EditText)this.findViewById(R.id.textExportdir);
		exportdirView.setText(exportdir);
		exportdirView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				exportdir = exportdirView.getText().toString();
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		soundToggle = (ToggleButton)this.findViewById(R.id.toggleSound);
		if (sound.compareTo("yes") == 0)
			soundToggle.setChecked(true);
		else
			soundToggle.setChecked(false);
		soundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					sound = "yes";
				else
					sound = "no";
			}
		});

		stereoToggle = (ToggleButton)this.findViewById(R.id.toggleStereo);
		if (stereo.compareTo("stereo") == 0)
			stereoToggle.setChecked(true);
		else
			stereoToggle.setChecked(false);
		stereoToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					stereo = "yes";
				else
					stereo = "no";
			}
		});

       	autosaveToggle = (ToggleButton)this.findViewById(R.id.toggleAutosave);
		if (autosave.compareTo("yes") == 0)
			autosaveToggle.setChecked(true);
		else
			autosaveToggle.setChecked(false);
		autosaveToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					autosave = "yes";
				else
					autosave = "no";
			}
		});
		
       	startblankToggle = (ToggleButton)this.findViewById(R.id.toggleStartblank);
		if (startblank.compareTo("yes") == 0)
			startblankToggle.setChecked(true);
		else
			startblankToggle.setChecked(false);
		startblankToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					startblank = "yes";
				else
					startblank = "no";
			}
		});
		
       	newcolorsfirstToggle = (ToggleButton)this.findViewById(R.id.toggleNewcolorsfirst);
		if (newcolorsfirst.compareTo("yes") == 0)
			newcolorsfirstToggle.setChecked(true);
		else
			newcolorsfirstToggle.setChecked(false);
		newcolorsfirstToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					newcolorsfirst = "yes";
				else
					newcolorsfirst = "no";
			}
		});

		languages = getResources().getStringArray(R.array.languages);
		langs = getResources().getStringArray(R.array.languages);
		int index = 0;
		for (; index != languages.length; index++){
		      langs[index] = languages[index].split(",")[0];
		}

		index = 0;
		for (; index != languages.length; index++){
		    if (languages[index].split(",")[1].compareTo(lang) == 0)
				break;
		}
		if(index == languages.length)
		    index = 0;

		languageSpinner = (Spinner) findViewById(R.id.spinnerLanguage);

		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, langs)   ;
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(adapter);
		languageSpinner.setSelection(index);
		languageSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						language = languages[arg2].split(",")[0];
						lang = languages[arg2].split(",")[1];
						String loc;
						if (languages[arg2].split(",").length >2)
						    loc = languages[arg2].split(",")[2];
						/* This "else" should be removed when all languages in the list  have their Android counterpart */
						else
						    loc = "en";

						setLocale(loc);
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
                });
		
			saveoverGroup = (RadioGroup)this.findViewById(R.id.groupSaveover);
			if (saveover.compareTo("yes") == 0)
				((RadioButton)this.findViewById(R.id.radioYes)).setChecked(true);
			else if (saveover.compareTo("ask") == 0)
				 ((RadioButton)this.findViewById(R.id.radioAsk)).setChecked(true);
			else
				((RadioButton)this.findViewById(R.id.radioNew)).setChecked(true);
			saveoverGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					 int radioButtonId = group.getCheckedRadioButtonId();
					 if (radioButtonId == R.id.radioAsk)
						 saveover = "ask";
					 else if (radioButtonId == R.id.radioYes)
						 saveover = "yes";
					 else
						 saveover = "new";
				}
			});

		sysfontsToggle = (ToggleButton)this.findViewById(R.id.toggleSysfonts);
		if (sysfonts.compareTo("yes") == 0)
			sysfontsToggle.setChecked(true);
		else
			sysfontsToggle.setChecked(false);
		sysfontsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					sysfonts = "yes";
				else
					sysfonts = "no";
			}
		});

		printToggle = (ToggleButton)this.findViewById(R.id.togglePrint);
		if (print.compareTo("yes") == 0)
			printToggle.setChecked(true);
		else
			printToggle.setChecked(false);
		printToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					print = "yes";
				else
					print = "no";
			}
		});

		printdelays = getResources().getStringArray(R.array.printdelays);
		int indexpd = 0;
		for (; indexpd != printdelays.length; indexpd++){
			if (printdelays[indexpd].compareTo(printdelay) == 0)
				break;
		}
		if(indexpd==printdelays.length)
		    indexpd = 0;
       	printdelaySpinner = (Spinner) findViewById(R.id.spinnerPrintdelay);
		printdelaySpinner.setSelection(indexpd); 
		printdelaySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						printdelay = printdelays[arg2];
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
                });
		
	disablescreensaverToggle = (ToggleButton)this.findViewById(R.id.toggleDisablescreensaver);
		if (disablescreensaver.compareTo("yes") == 0)
			disablescreensaverToggle.setChecked(true);
		else
			disablescreensaverToggle.setChecked(false);
		disablescreensaverToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					disablescreensaver = "yes";
				else
					disablescreensaver = "no";
			}
		});

	orientToggle = (ToggleButton)this.findViewById(R.id.toggleOrient);
		if (orient.compareTo("landscape") == 0)
			orientToggle.setChecked(true);
		else
			orientToggle.setChecked(false);
		orientToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					orient = "landscape";
				else
					orient = "portrait";
			}
		});

		okButton = (Button)this.findViewById(R.id.buttonOk);

		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View buttonView) {
			    finish();
			}
		    });

		cancelButton = (Button)this.findViewById(R.id.buttonCancel);

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View buttonView) {
			    cancel = true;
			    finish();
			}
		    });

		buttonsizes = getResources().getStringArray(R.array.buttonsizes);
		indexpd = 0;
		for (; indexpd != buttonsizes.length; indexpd++){
			if (buttonsizes[indexpd].compareTo(buttonsize) == 0)
				break;
		}
		if(indexpd==buttonsizes.length)
		    indexpd = 0;
                buttonsizeSpinner = (Spinner) findViewById(R.id.spinnerButtonsize);
		buttonsizeSpinner.setSelection(indexpd);
		buttonsizeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						buttonsize = buttonsizes[arg2];
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
                });

		colors_rows = getResources().getStringArray(R.array.colors_rows);
		indexpd = 0;
		for (; indexpd != colors_rows.length; indexpd++){
			if (colors_rows[indexpd].compareTo(colorsrows) == 0)
				break;
		}
		if(indexpd==colors_rows.length)
		    indexpd = 0;
                colorsrowsSpinner = (Spinner) findViewById(R.id.spinnerColorsrows);
		colorsrowsSpinner.setSelection(indexpd);
		colorsrowsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						colorsrows = colors_rows[arg2];
					}
					public void onNothingSelected(AdapterView<?> arg0) {
					}
                });

	}

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        save ();
        super.onDestroy();
    }

    /* Load first the defaults from the .cfg file inside assets, then try to overwrite them with the user defined config */
    private void load (){
	Log.v(TAG, "load()");
	props = new Properties();
	propsback = new Properties();

	try {
	    mgr = getResources().getAssets();
	    InputStream inassetscfg = mgr.open ("etc/tuxpaint.cfg");
	    props.load(inassetscfg);
	    inassetscfg.close();
	    InputStream inassetscfgback = mgr.open ("etc/tuxpaint.cfg");
	    propsback.load(inassetscfgback);
	    inassetscfgback.close();
	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	File external = getExternalFilesDir(null);
	File cfg =  new File (external, "tuxpaint.cfg");
	try {
	    InputStream in = new FileInputStream(cfg);
	    props.load(in);
	    in.close();
	    InputStream inback = new FileInputStream(cfg);
	    propsback.load(inback);
	    inback.close();
	} catch (FileNotFoundException el){ /* do nothing, defaults have already been loaded */
	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}


	autosave = props.getProperty("autosave", "no");
	sound = props.getProperty("sound", "no");
	stereo = props.getProperty("stereo", "yes");
	saveover = props.getProperty("saveover", "ask");
	startblank = props.getProperty("startblank", "no");
	newcolorsfirst = props.getProperty("newcolorsfirst", "yes");
	savedir = props.getProperty("savedir", external.getAbsolutePath());
	datadir = props.getProperty("datadir", external.getAbsolutePath());
	exportdir = props.getProperty("exportdir", external.getAbsolutePath());
	lang = props.getProperty("lang", "en");
	sysfonts = props.getProperty("sysfonts", "no");
	print = props.getProperty("print", "no");
	printdelay = props.getProperty("printdelay", "0");
	disablescreensaver = props.getProperty("disablescreensaver", "no");
	orient = props.getProperty("orient", "landscape");
	buttonsize = props.getProperty("buttonsize", "48");
	colorsrows = props.getProperty("colorsrows", "1");
	    	 
	Log.v(TAG, autosave + " " + sound + " " + stereo + " " + saveover + " " + savedir + " " + datadir + " " + exportdir + " " + lang + " " + sysfonts + " " + print + " " + printdelay + " " + disablescreensaver + " " + orient + " " + buttonsize + " " + colorsrows);
    }

    private void save () {
    	File external = getExternalFilesDir(null);
     	File cfg =  new File (external, "tuxpaint.cfg");
        props.put("autosave", autosave);
        props.put("sound", sound);
        props.put("stereo", stereo);
        props.put("saveover", saveover);
        props.put("startblank", startblank);
        props.put("newcolorsfirst", newcolorsfirst);
        props.put("savedir", savedir);
        props.put("datadir", datadir);
        props.put("exportdir", exportdir);
        props.put("lang", lang);
	props.put("sysfonts", sysfonts);
	props.put("print", print);
        props.put("printdelay", printdelay);
	props.put("disablescreensaver", disablescreensaver);
	props.put("orient", orient);
	props.put("buttonsize", buttonsize);
	props.put("colorsrows", colorsrows);

	try {
	    OutputStream	out = new FileOutputStream(cfg);
	    if (cancel == true){
		propsback.store(out,"");
	    }
	    else {
		props.store(out, "");
	    }

	    out.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void setLocale(String lang) {
	String[] langsplit = lang.split("_");
	locLanguage = langsplit[0];

	if (langsplit.length > 1)
	    locRegion = langsplit[1];
	else
	    locRegion = "";

	if (langsplit.length > 2)
	    locScript = langsplit[2];
	else
	    locScript = "";

	if (langsplit.length > 3)
	    locVariant = langsplit[3];
	else
	    locVariant = "";

	Locale myLocale = new Locale.Builder().setLanguage(locLanguage).setRegion(locRegion).setScript(locScript).setVariant(locVariant).build();
	if (!localeback.equals(myLocale))
	    {
		conf.locale = myLocale;

		/* FIXME: Ugly hack to deal with shrink changes every time updateConfiguration/recreate is called.
		   Unsetting android:anyDensity="false" in AndroidManifest.xml would be the right fix, but can't do it right 
		   now as buttons in the main program would be too small on many devices. Pere - November 2020*/
		//		conf.densityDpi=360;

		res.updateConfiguration(conf, res.getDisplayMetrics());
		recreate();
	    }
    }
}
