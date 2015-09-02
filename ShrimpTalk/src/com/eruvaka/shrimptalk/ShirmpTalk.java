package com.eruvaka.shrimptalk;




import java.io.BufferedInputStream;
import java.io.File;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.ipaulpro.afilechooser.utils.FileUtils;









import android.R.color;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


public class ShirmpTalk extends Activity {
	private static final int REQUEST_PATH = 1;
	private static final String TAG = "ShrimpTalk";

	EditText edittext;
	Context context;
	File file;
	
	static int inputsize;
	static long outputsize;
	ProgressDialog progressDialog;
	FileInputStream inputfile;
	long numOfBytesRead;
	  int hL = 62;
		final int h[] = {
		       22,     45,    -47,    -86,    112,     78,   -215,     58,    316,
		     -325,   -336,    578,    227,   -554,    -45,     62,    -49,    779,
		      -72,  -1457,    306,   1290,   -266,    108,   -511,  -2452,   2171,
		     4824,  -4269,  -6160,   5890,   5890,  -6160,  -4269,   4824,   2171,
		    -2452,   -511,    108,   -266,   1290,    306,  -1457,    -72,    779,
		      -49,     62,    -45,   -554,    227,    578,   -336,   -325,    316,
		       58,   -215,     78,    112,    -86,    -47,     45,     22
		};
		
		

		long lenthoffile;
	    long outpt; 
		byte[]  readBytesData;
		short[]  buff16; 
        double[] frame; 
        double[] frame2;
		double[] output;	
		int count;
		
		
		
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shirmp_talk);

	
	    edittext = (EditText)findViewById(R.id.editText);
	}

	public void getfile(View view){ 
		 showChooser();
	    }
	 
	 private void showChooser() {
	        // Use the GET_CONTENT intent from the utility class
	        Intent target = FileUtils.createGetContentIntent();
	        // Create the chooser Intent
	        Intent intent = Intent.createChooser(
	                target, getString(R.string.chooser_title));
	        try {
	            startActivityForResult(intent, REQUEST_PATH);
	        } catch (ActivityNotFoundException e) {
	            // The reason for the existence of aFileChooser
	        }
	    }
	
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch (requestCode) {
	            case REQUEST_PATH:
	                // If the file selection was successful
	                if (resultCode == RESULT_OK) {
	                    if (data != null) {
	                        // Get the URI of the selected file
	                        final Uri uri = data.getData();
	                        Log.i(TAG, "Uri = " + uri.toString());
	                        try {
	                        	
	                        	 final String path = FileUtils.getPath(this, uri);
	                            
	                             edittext.setText(path);
	                            
	                           
	                        } catch (Exception e) {
	                            Log.e("FileSelectorTestActivity", "File select error", e);
	                        }
	                    }
	                }
	                break;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	    
	    
	    
	    
	public void generateGraph(View view) {

		String path = edittext.getText().toString();
		new Mytask().execute(path);

	}	    
	   
	   
	   
	   
	   private class Mytask extends AsyncTask<String, Void, Void> {	
			
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
	          super.onPreExecute();
	      	progressDialog = new ProgressDialog(ShirmpTalk.this);
			progressDialog.setMessage("Processing Data. please wait...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			}
			
			protected void onPostExecute(Void res) {
				// TODO Auto-generated method stub
			
				System.out.println("before calling generategraph");
				try {
					generategrath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		 		/*progressDialog.cancel();
				progressDialog.dismiss();
				*/
                 
				
			}	
			

			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
			    Calendar cal = Calendar.getInstance();
		        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		        System.out.println("Starting time of execution"+ sdf.format(cal.getTime()) );
				
				String path = params[0]; // edittext.getText().toString();
				System.out.println("doinbackground path="+ path );
				
				Byte Headersize = 44;
				file = new File(path);
				
				lenthoffile = file.length();
			    outpt = lenthoffile/(2*1024*2);
				readBytesData = new byte[16 * 1024 * 4 ];
				buff16 = new short[16 * 1024 * 2]; // 65536 bytes array
		        frame = new double[(2 * hL) + (16 * 1024)];
		        frame2 = new double[16 * 1024];
				
				output = new double[(int)outpt];		
				
				// calling header here
				System.out.println("fileLength = "+lenthoffile);

				
				try {
					inputfile = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedInputStream buffStream = new BufferedInputStream(inputfile);			
				
		     			   
				int count2 = 0;
				count = 0;

				for (count = 0; count < (int) (outpt / 8); count++) {
					output[count] =  0.0;
				}

				for (count = 0; count < 16 * 1024; count++) {
					frame2[count] = 0.0;
				}
				    
				double[] L1 = new double[hL - 1];
				double[] L2 = new double[hL - 1];

				for (count = 0; count < hL - 1; count++) {
					L1[count] =  0;
					L2[count] =  0;
				}
				count = 0;

				
			
				if (lenthoffile > Headersize) {
					
					try {
						buffStream.skip(Headersize);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 

				
				
				
				
				
				FileChannel ch = inputfile.getChannel( );
				ByteBuffer bb = ByteBuffer.wrap( readBytesData );
				int nRead;
				

				try {
					while ( (nRead=ch.read(bb)) != -1 )
					{
						int j=0;
						
						System.out.println("nRead" +nRead );
					    for (int i = 0; i < nRead; i=i+2, j++) {
					       short MSB = (short) readBytesData[i+1];
					       short LSB = (short) readBytesData[i];
					       buff16[j] = (short) (MSB << 8 | (255 & LSB));
					    }			
					    
					    
					    while(count2<((16*1024)+hL-1))
					        {
					            if(count2>=(16*1024))
					            {
					                L1[count2-(16*1024)]=buff16[(2*(count2-hL+1))]*Math.pow(2,-15);
					            }
					            if(count2<hL-1)
					            {
					               frame[count2]=L1[count2];
					            }
					            else
					            {
					                frame[count2]=buff16[2*(count2-hL+1)]*Math.pow(2,-15);
					            }
					            count2++;
					        }

					        if(count2==16*1024+hL-1)
					        {
					            int time;
					            for(time=0;time<16*1024;time++)
					            {
					                int tou;
					                
					                for(tou=time;tou<time+hL;tou++)
					                {
					                        frame2[time]=frame2[time]+frame[tou]*h[time+hL-1-tou];
					                }
					                output[count]=(output[count]+Math.pow(frame2[time],2));
					                frame2[time]= 0;

					            }
					            count2=0;
					       }
					        
					        
					        
					    
					    if(output[count]>100000000)
					    {
					        output[count]=100000000;
					    }
					        count++;
					        
						
					        
					        bb.clear();
					        
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				 Calendar cal2 = Calendar.getInstance();
			        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
			        System.out.println("Ending Time execution" +sdf2.format(cal2.getTime()) );
			        
			        System.out.println("count=" +count );
				    	return null;

			}
			
			
		}
	
	   private void generategrath() throws IOException {
			// TODO Auto-generated method stub
		try{
			
			 XYSeries feedSeries = new XYSeries("Normal Feed Consumtion");
		      int x[] = new int [output.length];
		        // Adding data to Income and Expense Series
		        for(int i=0;i<count;i++){
		            feedSeries.add(x[i], output[i]);
		         }

		        
			
		        // Creating a dataset to hold each series
		        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		        // Adding Income Series to the dataset
		        dataset.addSeries(feedSeries);
		      
		
		        // Creating XYSeriesRenderer to customize incomeSeries
		        XYSeriesRenderer feedRenderer = new XYSeriesRenderer();
		        feedRenderer.setColor(Color.GREEN);
		        feedRenderer.setPointStyle(PointStyle.DIAMOND);
		        feedRenderer.setFillPoints(true);
		        feedRenderer.setLineWidth(2);
		        feedRenderer.setDisplayChartValues(true);

		        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		        multiRenderer.setXLabels(0);
		      
		       
		       
		        multiRenderer.setXTitle("Normal Feed Consumtion");
		    	multiRenderer.setZoomButtonsVisible(true);    
		       	multiRenderer.setZoomEnabled(true);
		        multiRenderer.setXTitle("Time");
		        multiRenderer.setPanEnabled(true, true);
		  		multiRenderer.setClickEnabled(false);
		  		multiRenderer.setLabelsTextSize(15);
		    	multiRenderer.setMarginsColor(color.transparent);
		       multiRenderer.setBackgroundColor(color.transparent);
		      //multiRenderer.setShowLegend(false);
		       multiRenderer.setShowGridY(true);
		       multiRenderer.setShowGridX(true);
		       multiRenderer.setChartValuesTextSize(25);
		       multiRenderer.setAxisTitleTextSize(35);
		      
		        multiRenderer.addSeriesRenderer(feedRenderer);
		      
		        
		        LinearLayout chartContainer = (LinearLayout)findViewById(R.id.chart);
		        
		 	   // remove any views before u paint the chart
		 	    chartContainer.removeAllViews();
		 	    final GraphicalView linechart = ChartFactory.getLineChartView(getBaseContext(), dataset, multiRenderer);
		        // Creating an intent to plot line chart using dataset and multipleRenderer
		      
		 		progressDialog.cancel();
				progressDialog.dismiss();
		       
		 	   chartContainer.addView(linechart);
		 	   
			
		   
			      
	}catch(Exception e){
		e.printStackTrace();
	}
	
	}

	

}
