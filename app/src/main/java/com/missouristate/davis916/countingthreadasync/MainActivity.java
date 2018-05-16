package com.missouristate.davis916.countingthreadasync;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

/*
 * Laura Davis CIS 262-902
 * 28 March 2018
 * This app demonstrates threading using AsyncTask in Android.
 * The counter, running on a background AsyncTask
 * and output being updated in the UI, will start
 * incrementing when the user presses the start button.
 * The start button will be disabled on click to
 * prevent thread overload and resultant system crash.
 * When the user presses the stop button, the thread will
 * stop and the buttons will no longer function. This
 * design ensures that an IllegalThreadStateException
 * will not occur if the start button were clicked again.
 */
public class MainActivity extends Activity {
    //Declare variables
    //AsyncTask must be created as an object here for method use later
    private TextView countTextView;
    private Integer count;
    private Button btnStart, btnStop;
    private AsyncTask counter = new CounterAsync();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference the TextView UI element in the layout
        countTextView = (TextView) findViewById(R.id.textView);

        //Sets onClick listener for the start button
        //The command to start AsyncTask is asyncObject.execute()
        btnStart = (Button) findViewById(R.id.button);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                counter.execute();
                btnStart.setEnabled(false);
            }
        });

        //Sets onClick listener for the stop button
        //The command to stop AsyncTask is asyncObject.cancel()
        btnStop = (Button) findViewById(R.id.button2);
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View view){
                counter.cancel(true);
            }
        });
    }//end onCreate()

    public class CounterAsync extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            /* Initialize counter to zero before Async thread launch
             * This removes the need to initialize count to 0 in the
             * overridden onStart method.
             */
            count = 0;
            countTextView.setText(count.toString());
        }
        @Override
        protected Integer doInBackground(Object... params){
            //Increment the count during Async run
            while(!counter.isCancelled()) {
                count++;
                //Publish progress sends the count to onProgressUpdate
                //and allows the UI to be updated
                publishProgress(count);
                SystemClock.sleep(500);

                //isCancelled() check recommended by Android Developers
                //from https://developer.android.com/reference/android/os/AsyncTask.htm
                if(counter.isCancelled()) break;
            }
            return null;
        }//end doInBackground()

        @Override
        protected void onProgressUpdate(Integer... count){
            /* Integer count object must be set to count[0] in order
             * to be parsed and displayed properly. Without this, the
             * count is displayed as memory addresses.
             */
            super.onProgressUpdate(count[0]);
            //Update the UI from background
            countTextView.setText(count[0].toString());
        }

        /* According to Android developers, this method will never
         * be called if the thread is cancelled.
         * Leaving the method in place for future reference.
         */
        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
            //Final update of the UI from the background at stop
            countTextView.setText(count.toString());
        }

    }//end CounterAsync class

    //Menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }//end createOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar item clicks here. The action bar will
        //automatically handle clicks on the Home/Up button,
        //as long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected

}//end MainActivity class
