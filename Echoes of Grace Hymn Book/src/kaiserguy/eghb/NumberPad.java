package kaiserguy.eghb;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NumberPad extends Activity {
	 private TextView txtInput=null;
	 private Button btnZero=null;
	 private Button btnOne=null;
	 private Button btnTwo=null;
	 private Button btnThree=null;
	 private Button btnFour=null;
	 private Button btnFive=null;
	 private Button btnSix=null;
	 private Button btnSeven=null;
	 private Button btnEight=null;
	 private Button btnNine=null;
	 private Button btnGo=null;
	 private Button btnGo2=null;

	 /** Called when the activity is first created. */
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setTheme(android.R.style.Theme_Black);
	  setContentView(R.layout.number_pad);

	  initControls();
	  initScreenLayout();
	  reset();
	 }

		@Override
		public boolean onSearchRequested() {
		     launchSearch();
		     return true;
		}

	    private void launchSearch() {
	    	Intent next = new Intent();
			next.setClass(this, SearchableHymnBook.class);
			next.setAction(Intent.ACTION_SEARCH);
			next.putExtra(SearchManager.QUERY,"");
			startActivity(next);
	    }
	 private void initScreenLayout() {
	  
	  /*
	   * The following three command lines you can use depending
	   * upon the emulator device you are using.
	   */
	  
	  // 320 x 480 (Tall Display - HVGA-P) [default]
	  // 320 x 240 (Short Display - QVGA-L)
	  // 240 x 320 (Short Display - QVGA-P)

	  DisplayMetrics dm = new DisplayMetrics();
	  getWindowManager().getDefaultDisplay().getMetrics(dm);

	  // this.showAlert(dm.widthPixels +" "+ dm.heightPixels, dm.widthPixels
	  // +" "+ dm.heightPixels, dm.widthPixels +" "+ dm.heightPixels, false);

	  int height = dm.heightPixels;
	  int width = dm.widthPixels;
	  int numberSize = 80;
	  int goSize = 60;
	  int inputSize = 50;

	  if (height > 600 && width > 600) {
	   txtInput.setTextSize(inputSize);
	   btnGo.setTextSize(goSize);
	   btnGo2.setTextSize(goSize);
	   btnNine.setTextSize(numberSize);
	   btnEight.setTextSize(numberSize);
	   btnSeven.setTextSize(numberSize);
	   btnSix.setTextSize(numberSize);
	   btnFive.setTextSize(numberSize);
	   btnFour.setTextSize(numberSize);
	   btnThree.setTextSize(numberSize);
	   btnTwo.setTextSize(numberSize);
	   btnOne.setTextSize(numberSize);
	   btnZero.setTextSize(numberSize);
	  }

	  /*btnZero.setTextColor(Color.MAGENTA);
	  btnOne.setTextColor(Color.MAGENTA);
	  btnTwo.setTextColor(Color.MAGENTA);
	  btnThree.setTextColor(Color.MAGENTA);
	  btnFour.setTextColor(Color.MAGENTA);
	  btnFive.setTextColor(Color.MAGENTA);
	  btnSix.setTextColor(Color.MAGENTA);
	  btnSeven.setTextColor(Color.MAGENTA);
	  btnEight.setTextColor(Color.MAGENTA);
	  btnNine.setTextColor(Color.MAGENTA);

	  btnGo.setTextColor(Color.rgb(0, 100, 0));
	  btnAppendix.setTextColor(Color.DKGRAY);*/
	 }

	 private void initControls() {
	  txtInput = (TextView) findViewById(R.id.txtInput);
	  btnZero = (Button) findViewById(R.id.btnZero);
	  btnOne = (Button) findViewById(R.id.btnOne);
	  btnTwo = (Button) findViewById(R.id.btnTwo);
	  btnThree = (Button) findViewById(R.id.btnThree);
	  btnFour = (Button) findViewById(R.id.btnFour);
	  btnFive = (Button) findViewById(R.id.btnFive);
	  btnSix = (Button) findViewById(R.id.btnSix);
	  btnSeven = (Button) findViewById(R.id.btnSeven);
	  btnEight = (Button) findViewById(R.id.btnEight);
	  btnNine = (Button) findViewById(R.id.btnNine);
	  btnGo = (Button) findViewById(R.id.btnGo);
	  btnGo2 = (Button) findViewById(R.id.btnGo2);

	  btnZero.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(0);
	   }
	  });
	  btnOne.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(1);
	   }
	  });
	  btnTwo.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(2);
	   }
	  });
	  btnThree.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(3);
	   }
	  });
	  btnFour.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(4);
	   }
	  });
	  btnFive.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(5);
	   }
	  });
	  btnSix.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(6);
	   }
	  });
	  btnSeven.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(7);
	   }
	  });
	  btnEight.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(8);
	   }
	  });
	  btnNine.setOnClickListener(new Button.OnClickListener() {
	   public void onClick(View v) {
	    handleNumber(9);
	   }
	  });
	  btnGo.setOnClickListener(new Button.OnClickListener() {
		   public void onClick(View v) {
			   launchHymn(Integer.parseInt(txtInput.getText().toString()));
		   }
	  });
	  btnGo2.setOnClickListener(new Button.OnClickListener() {
		   public void onClick(View v) {
			   launchHymn(Integer.parseInt(txtInput.getText().toString()));
		   }
	  });
	 }

	 private void handleNumber(int num) {
	  String strValue = txtInput.getText().toString().trim();
	  int intValue = Integer.parseInt(strValue + Integer.toString(num));
	  
	  if (intValue > 379){
		  intValue = num;
	  }

	  if (intValue > 0){
		  txtInput.setText(Integer.toString(intValue));
	  } else {
		  reset();
	  }
	  
	  if (intValue > 99){
		  launchHymn(intValue);
	  }
	 }

	 private void reset() {
	  txtInput.setText(" ");
	 }
	 
	    private void launchHymn(int number) {
			Intent next = new Intent();
			next.setClass(this, HymnActivity.class);
			next.putExtra("hymnNumber", number);
			startActivity(next);
		}
	 @Override
     public boolean onKeyDown(int keyCode, KeyEvent event)  {
         if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                 && keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
             // Take care of calling this method on earlier versions of
             // the platform where it doesn't exist.
             onBackPressed();
         }

         return super.onKeyDown(keyCode, event);
     }
     
     @Override
     public void onBackPressed (){
     	if (txtInput.getText().toString().equals(" ") == false){
     		reset();
     	}else{
     		this.finish();
     	}
     	return;
     }
	}