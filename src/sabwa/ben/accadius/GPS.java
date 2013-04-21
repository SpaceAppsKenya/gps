package sabwa.ben.accadius;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
@SuppressWarnings("unused")
public class GPS extends Activity implements Runnable   {

	   public  byte[] data;
	   public  HttpPost httppost;
	   public  StringBuffer buffer;
	   public  HttpResponse response;
	   public  HttpClient httpclient;
	
	   
	//private Handler handler;
    //text views to display latitude and longitude
	   static TextView latituteField;
	   static TextView longitudeField;
	   static TextView altitudeField;
	   static TextView currentSpeedField;
	   static TextView kmphSpeedField;
	   static TextView avgSpeedField;
	   static TextView avgKmphField;
	   static TextView topSpeedField;
	   static TextView topKmphField;
	   static TextView simuz;
	   private ProgressDialog pd;
	   public String phoneNumber;
	   public String lati;
	   public  String longi;
	   public String alti;
	   public  String curkmph;
	   public  String curmps;
	   public  String avgkmph;
	   public  String avgmps;
	   public  String topkmph;
	   public  String topmps;
	   public String answers;
	   public Thread thread;
	   
	   int CAMERA_PIC_REQUEST = 2;
	   private Uri imageUri;
	   String imageString;
	
	   String URL = "http://sabwa.jumanjiinc.com/mars_rover_coders/db/db1.php";
	   
	   
	   
   //objects to store positional information
    static double lat;
    static double lon;
    static double alt;
   

   //objects to store values for current and average speed
   protected static double simuzz;
   protected static double currentSpeed;
   protected static double kmphSpeed;
   protected static double avgSpeed;
   protected static double avgKmph;
   protected static double totalSpeed;
   protected static double totalKmph;
   protected static double topSpeed=0;
   protected static double topKmph=0;
   private static String url;
   public String deviceId;
   public Button login, photoBtn;
   public ProgressDialog dialog;
   public Bitmap photo;
   public ImageView image;
   public static String answer, longi2, lati2, alti2;
   public static List<NameValuePair> nameValuePairs;
  
   
  // MapController mc;
   //GeoPoint p;
   public Location loc;
  // double longitude, latitude;


   //counter that is incremented every time a new position is received, used to calculate average speed
   static int counter = 0;
  // private LocationManager locmgr = null;

   /** Called when the activity is first created. 
 * @return */
   @Override
   public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      //map starts 
      


		//locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		login = (Button) findViewById(R.id.login);
		
		deviceId = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		
		simuz = (TextView) findViewById(R.id.simu);
		simuz.setText("IMEI: "+String.valueOf(deviceId));
		 
      PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
      PowerManager.WakeLock wL = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"My Tag");

      wL.acquire();
//    locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    longitude = loc.getLongitude();
//    latitude = loc.getLatitude();
//    longi = Double.toString(longitude);
//	  lati = Double.toString(latitude);
//    lati = latituteField.getText().toString();
//    longi = longitudeField.getText().toString();
     
      
      
     
      startService(new Intent(this, Calculations.class));

      latituteField = (TextView) findViewById(R.id.lat);
      longitudeField = (TextView) findViewById(R.id.lon);   
      altitudeField= (TextView) findViewById(R.id.alt);
      currentSpeedField = (TextView) findViewById(R.id.speed);
      kmphSpeedField = (TextView) findViewById(R.id.kmph);
      avgSpeedField = (TextView) findViewById(R.id.avgspeed);
      avgKmphField = (TextView) findViewById(R.id.avgkmph);
      topSpeedField = (TextView) findViewById(R.id.topspeed);
      topKmphField = (TextView) findViewById(R.id.topkmph);
     
  
   image = (ImageView) findViewById(R.id.ImageView);
   photoBtn = (Button) findViewById(R.id.photo);
   photoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
		        
				takePhoto();
				
			}
   });
  
   }
   
   private void takePhoto(){
//	   try{
//	        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//	        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//	        
//			}catch(Exception e){
//				Toast.makeText(GPS.this, "Problem loading camera", Toast.LENGTH_LONG);
//			}
	   Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
       startActivityForResult(intent, 0);
   }
   
//   @Override
//   protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//   {
//       if( requestCode == CAMERA_PIC_REQUEST)
//       {
//       //  data.getExtras()
//           Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//           ImageView image =(ImageView) findViewById(R.id.ImageView);
//           image.setImageBitmap(thumbnail);
//       }
//       else 
//       {
//           Toast.makeText(GPS.this, "Picture NOt taken", Toast.LENGTH_LONG);
//       }
//       super.onActivityResult(requestCode, resultCode, data);
//   }
   BitmapFactory.Options bitmapOptions;
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
	   super.onActivityResult(requestCode, resultCode, data);
	   if (requestCode == 0 && resultCode == RESULT_OK) {
           if (data != null) {
        	   Log.d("camera ---- > ", "" + data.getExtras().get("data"));
        	   Toast.makeText(getApplicationContext(), getLastImageId(), Toast.LENGTH_LONG).show();
               bitmapOptions = new BitmapFactory.Options();

               photo = BitmapFactory.decodeFile( (String) getLastImageId(),bitmapOptions);
               image.setImageBitmap(photo);
               httppost = new HttpPost("http://sabwa.jumanjiinc.com/mars_rover_coders/db/db1.php");
        	   
           }
           
        // sendImg();
         dialog = ProgressDialog.show(GPS.this, "", "Uploading file...", true);
          new Thread(new Runnable() {
                 public void run() {
                      runOnUiThread(new Runnable() {
                             public void run() {

                             }
                         });                      
                      sendImg();
                //  Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();

                 }

				
               }).start();    }
	   
          }

	   
   
   private String getLastImageId() {
       final String[] imageColumns = { MediaStore.Images.Media._ID,
               MediaStore.Images.Media.DATA };
       final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
       Cursor imageCursor = managedQuery(
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
               null, null, imageOrderBy);
       if (imageCursor.moveToFirst()) {
           int id = imageCursor.getInt(imageCursor
                   .getColumnIndex(MediaStore.Images.Media._ID));
           String fullPath = imageCursor.getString(imageCursor
                   .getColumnIndex(MediaStore.Images.Media.DATA));

           imageCursor.close();
           return fullPath;
       } else {
           return "no path";
       }
   }

     InputStream inputStream;
   File f;
     public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

       String res = "";
       StringBuffer buffer = new StringBuffer();
       inputStream = response.getEntity().getContent();
       int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
       Toast.makeText(GPS.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
       if (contentLength < 0){
       }
       else{
              byte[] data = new byte[512];
              int len = 0;
              try
              {
                  while (-1 != (len = inputStream.read(data)) )
                  {
                      buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                  }
              }
              catch (IOException e)
              {
                  e.printStackTrace();
              }
              try
              {
                  inputStream.close(); // closing the stream…..
              }
              catch (IOException e)
              {
                  e.printStackTrace();
              }
              res = buffer.toString();     // converting stringbuffer to string…..

            //  Toast.makeText(MainActivity.this, "Result : " + res, Toast.LENGTH_LONG).show();
             }
       return res;
  }
    public void sendImg(){

         ByteArrayOutputStream stream = new ByteArrayOutputStream();

           photo.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.

               byte [] data =  stream.toByteArray();
               int offset = 0, len = 0, flags = 0;
			   imageString = Base64.encodeToString(data, offset, len, flags);
               nameValuePairs = new  ArrayList<NameValuePair>();
               nameValuePairs.add(new BasicNameValuePair("imgdata",imageString));

               try{
                   HttpClient httpclient = new DefaultHttpClient();
                   HttpPost httppost = new HttpPost(URL);
                   httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                   HttpResponse response = httpclient.execute(httppost);
                   String the_string_response = convertResponseToString(response);

                  // editor.putString("imgRes", the_string_response);editor.commit();
                   Toast.makeText(GPS.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
               }catch(Exception e){
                     //Toast.makeText(GPS.this, "ERROR" + e.getMessage(), Toast.LENGTH_LONG).show();

                    }


              dialog.dismiss();   
              this.finish();

     }
	static void jah(){
	  longi2 = Double.toString(lon);
	  lati2 = Double.toString(lat);
	  alti2= Double.toString(alt);
      latituteField.setText(String.valueOf(lati2));
      longitudeField.setText(String.valueOf(longi2));
      altitudeField.setText(String.valueOf(alti2));
      currentSpeedField.setText(String.valueOf(currentSpeed)+"(m/s)");
      kmphSpeedField.setText(String.valueOf(kmphSpeed)+"(km/h)");
      avgSpeedField.setText("Average Speed (m/s):      "+String.valueOf(avgSpeed));
      avgKmphField.setText("Average Speed (kmph):      "+String.valueOf(avgKmph));
      topSpeedField.setText("Top Speed (m/s):      "+String.valueOf(topSpeed));
      topKmphField.setText("Top Speed (kmph):      "+String.valueOf(topKmph));
           
      
   
   
   
  
   }
   public void sendin(View v)
   {
	   
	   lati = latituteField.getText().toString();
	   longi = longitudeField.getText().toString();  
	   alti=altitudeField.getText().toString();
	                 	
	          	//create a new thread to run this process 
	          	
	          	

	          	if(lati.equals("")||longi.equals("")||alti.equals("") )
	            {
	              // answers="Please Wait for GPS to acquire signal..."; 
	                //Toast.makeText(Tdbase.this, "Blank Field..Please Enter", Toast.LENGTH_LONG).show();
	          	  Toast.makeText(GPS.this, "Please Wait for GPS to acquire signal...", Toast.LENGTH_LONG).show();
	            }else
	            {
	           // 	pd = ProgressDialog.show(GPS.this, "Please Wait...", "Validating..", true,
		      		//		false);
	      		Thread thread = new Thread(GPS.this);
	      		thread.start();
	      
	            }
	          	
	          	/// ends the thread
	      		
	      
   }
  

   public   void run(){
	   
	url="http://sabwa.jumanjiinc.com/mars_rover_coders/db/db1.php";
   	lati = latituteField.getText().toString();
   	longi = longitudeField.getText().toString();
   	alti= altitudeField.getText().toString();
   	curmps = currentSpeedField.getText().toString();
   	curkmph = kmphSpeedField.getText().toString();
   	avgmps = avgSpeedField.getText().toString();
   	avgkmph = avgKmphField.getText().toString();
   	topmps = topSpeedField.getText().toString();
   	topkmph = topKmphField.getText().toString();
   	
   //	simuz = deviceId.getText().toString();
	   
      //send http data
      
      		                
      	 
      	                try {
      	                	// preparing data to send
      	                	          	                	        		                   
      	                	     		                   
      		                   
      	                       httpclient = new DefaultHttpClient();	            	                      
      		                   httppost = new HttpPost(url);
      		                   
      		                   /* add this code to be able to catch the ConnectionTimeoutExeption and sockettimeoutExeption*/
      		                   
      		                   HttpParams params = httpclient.getParams();
          	                   HttpConnectionParams.setConnectionTimeout(params,3000);
          		               HttpConnectionParams.setSoTimeout(params, 3000);
          		                 
      		                    // Add your data
      		                    nameValuePairs = new ArrayList<NameValuePair>(2);
      		                   nameValuePairs.add(new BasicNameValuePair("xkod", lati));
      		                    nameValuePairs.add(new BasicNameValuePair("ykod", longi));
      		                  nameValuePairs.add(new BasicNameValuePair("Zkod", alti2));
      		                    nameValuePairs.add(new BasicNameValuePair("curmps", curmps.trim()));
      		                    nameValuePairs.add(new BasicNameValuePair("curkmph", curkmph.trim()));
      		                  nameValuePairs.add(new BasicNameValuePair("avgmps", avgmps.trim()));
      		                nameValuePairs.add(new BasicNameValuePair("avgkmph", avgkmph.trim()));
      		              nameValuePairs.add(new BasicNameValuePair("topmps", topmps.trim()));
      		            nameValuePairs.add(new BasicNameValuePair("topkmph", topkmph.trim()));
      		         // nameValuePairs.add(new BasicNameValuePair("simu", phoneNumber.trim()));
      		        nameValuePairs.add(new BasicNameValuePair("simu", deviceId.trim()));
      		          
      		                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      		 
      		                    // Execute HTTP Post Request
      		                    response = httpclient.execute(httppost);
      		                    inputStream = response.getEntity().getContent();
      		 
      		                    data = new byte[256];
      		 
      		                    buffer = new StringBuffer();
      		                    int len = 0;
      		                    while (-1 != (len = inputStream.read(data)) )
      		                    {
      		                        buffer.append(new String(data, 0, len));
      		                    }
      		 
      		                    inputStream.close();
      		                    
      		                    if(buffer.charAt(0)=='Y')
          		                {
      		                    	
          		                	answers="Update successfull";
          		                	
          		                	
          		                  //  Toast.makeText(GPS.this, "Update successfull", Toast.LENGTH_LONG).show();
          		                }
          		                else
          		                {
          		                	answers="Update failed!!!";
          		                    //Toast.makeText(mydatabaseProject.this, "Invalid Username or password", Toast.LENGTH_LONG).show();
          	                  // finish();
          		                	 
          		                	
          		                	
										// finish();
          		                	
      		                            		                    
          		                }
      		                  
      		                  
      		                  
      		              
      		              
      	                }// end try
      	                
      	                   catch (ConnectTimeoutException cte ){
      	                	  //Took too long to connect to remote host
      	                	   answers="Error: Could not conect to the Host ";//+cte.toString();
      	                	}
      	                	catch (SocketTimeoutException ste){
      	                	  //Remote host didn’t respond in time
      	                		answers=" Error in internet Connection";//+ste.toString();
      	                	}
      		                catch (Exception e)
      	                    {
      		                	// General Error when no response at all.
      		                	answers="Iko Error Mahali";
      		                	
      		                } //end of  try - catches
      		             // pd.dismiss();
      		              handler.postDelayed(this, 5000);  	                  
      		            
      		        //end else
      		                
      		                
      
      //end of the code connecting to the HTTP request
                          	
	//end send http data
      
       

   
   
}
   private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			//After the background process terminates it 
			
			//tv.setText(pi_string);
			//tv.setText(names);
			
			Toast.makeText(GPS.this, answers , Toast.LENGTH_LONG).show();
       
			pd.dismiss();
			dialog.dismiss();
			
			 
	

		}
	};
   
 


}