package sabwa.ben.accadius;

import java.math.BigDecimal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Calculations extends Service implements LocationListener  {

   static LocationManager locationManager;
   LocationListener locationListener;

   private static final String TAG = "Calculations";

   @Override
   public IBinder onBind(Intent intent) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void onCreate() {
      Toast.makeText(this, "Service Created: Getting GPS", Toast.LENGTH_LONG).show();
      Log.d(TAG, "onCreate");

     // Thread thread = new Thread(Calculations.this);
	//	thread.start();
run();
   }

   public void run(){

      final Criteria criteria = new Criteria();

      criteria.setAccuracy(Criteria.ACCURACY_FINE);
      criteria.setSpeedRequired(true);
      criteria.setAltitudeRequired(false);
      criteria.setBearingRequired(false);
      criteria.setCostAllowed(true);
      criteria.setPowerRequirement(Criteria.POWER_LOW);
      //Acquire a reference to the system Location Manager

      locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

      // Define a listener that responds to location updates
      locationListener = new LocationListener() {

         public void onLocationChanged(Location newLocation) {

            GPS.counter++;

            //current speed for the GPS device
            GPS.currentSpeed = round(newLocation.getSpeed(),3,BigDecimal.ROUND_HALF_UP);
            GPS.kmphSpeed = round((GPS.currentSpeed*3.6),3,BigDecimal.ROUND_HALF_UP);

            if (GPS.currentSpeed>GPS.topSpeed) {
               GPS.topSpeed=GPS.currentSpeed;
            }
            if (GPS.kmphSpeed>GPS.topKmph) {
               GPS.topKmph=GPS.kmphSpeed;
            }

            //all speeds added together
            GPS.totalSpeed = GPS.totalSpeed + GPS.currentSpeed;
            GPS.totalKmph = GPS.totalKmph + GPS.kmphSpeed;

            //calculates average speed
            GPS.avgSpeed = round(GPS.totalSpeed/GPS.counter,3,BigDecimal.ROUND_HALF_UP);
            GPS.avgKmph = round(GPS.totalKmph/GPS.counter,3,BigDecimal.ROUND_HALF_UP);

            //gets position
            GPS.lat = round(((double) (newLocation.getLatitude())),10,BigDecimal.ROUND_HALF_UP);
            GPS.lon = round(((double) (newLocation.getLongitude())),10,BigDecimal.ROUND_HALF_UP);
            GPS.alt = round(((double)(newLocation.getAltitude())), 10, BigDecimal.ROUND_HALF_UP);

            GPS.jah();
         }

         //not entirely sure what these do yet
         public void onStatusChanged(String provider, int status, Bundle extras) {}
         public void onProviderEnabled(String provider) {}
         public void onProviderDisabled(String provider) {}

      };

      // Register the listener with the Location Manager to receive location updates
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
     // handler.postDelayed(this, 5000);
     // handler.postDelayed(this, 3000);
   }
  /* private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			//After the background process terminates it 
			
			//tv.setText(pi_string);
			//tv.setText(names);
			
			//Toast.makeText(C.this, answers , Toast.LENGTH_LONG).show();
      
		//	pd.dismiss();
			
			 
	

		}
	};*/


   //Method to round the doubles to a max of 3 decimal places
   public static double round(double unrounded, int precision, int roundingMode)
   {
      BigDecimal bd = new BigDecimal(unrounded);
      BigDecimal rounded = bd.setScale(precision, roundingMode);
      return rounded.doubleValue();
   }


   public void onLocationChanged(Location location) {
      // TODO Auto-generated method stub

   }

   public void onProviderDisabled(String provider) {
      // TODO Auto-generated method stub

   }

   public void onProviderEnabled(String provider) {
      // TODO Auto-generated method stub

   }

   public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

   }



}