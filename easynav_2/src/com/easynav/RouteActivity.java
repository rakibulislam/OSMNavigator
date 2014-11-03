package com.easynav;

import java.util.Locale;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;

import com.easynav.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;


import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * Activity listing detailed itinerary as a list of nodes. 
 * @author M.Kergall
 */

public class RouteActivity extends Activity implements OnInitListener{

	private TextToSpeech myTTS;
//	private Button btnSpeakRoute;
    //status check code
	private int MY_DATA_CHECK_CODE = 0;
   
		@Override 
		public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);

        ListView list = (ListView) findViewById(R.id.items);
//        btnSpeakRoute = (Button) findViewById(R.id.btnSpeakRoute);

        Intent myIntent = getIntent();
        Road road = MapActivity.mRoad; //too big to pass safely in Extras
        final int currentNodeId = myIntent.getIntExtra("NODE_ID", -1);
        RoadNodesAdapter adapter = new RoadNodesAdapter(this, road);
   
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        
        
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
				Intent intent = new Intent();
	    		intent.putExtra("NODE_ID", position);
	    		
	    		String str =  MapActivity.mRoad.mNodes.get(position).mInstructions;	    		
	    		speakWords(str);
	       		
	    		setResult(RESULT_OK, intent);
			finish();
            }
        });

        list.setAdapter(adapter);
        list.setSelection(currentNodeId);
        
//		btnSpeakRoute.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				speakOutTheRoute();
//			}
//
//		});	
        
    }
	
	
	private void speakWords(String speech) {
        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}
	
//	private void speakOutTheRoute() {
//		for (int i = 0; i < MapActivity.mRoad.mNodes.size(); i++){
//			myTTS.speak(MapActivity.mRoad.mNodes.get(i).mInstructions, TextToSpeech.QUEUE_ADD, null);
//			myTTS.speak("..", TextToSpeech.QUEUE_ADD, null);			
//		}
//	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
            myTTS = new TextToSpeech(this, (OnInitListener) this);
            }
            else {
                    //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }


	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}

//    //setup TTS
//    public void onInit(int initStatus) {
//     
//            //check for successful instantiation
//        if (initStatus == TextToSpeech.SUCCESS) {
//            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
//                myTTS.setLanguage(Locale.US);
//            	
//        }
//        else if (initStatus == TextToSpeech.ERROR) {
//            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
//        }
//    }
	
	
	
}

class RoadNodesAdapter extends BaseAdapter implements OnClickListener {
    private Context mContext;
    private Road mRoad;
    TypedArray iconIds;
    
    public RoadNodesAdapter(Context context, Road road) {
        mContext = context;
        mRoad = road;
		iconIds = mContext.getResources().obtainTypedArray(R.array.direction_icons);
    }

    @Override public int getCount() {
    	if (mRoad == null || mRoad.mNodes == null)
    		return 0;
    	else
    		return mRoad.mNodes.size();
    }

    @Override public Object getItem(int position) {
    	if (mRoad == null || mRoad.mNodes == null)
    		return null;
    	else
    		return mRoad.mNodes.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup) {
        RoadNode entry = (RoadNode)getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_layout, null);
        }
        TextView tvTitle = (TextView)convertView.findViewById(R.id.title);
        String instructions = (entry.mInstructions==null ? "" : entry.mInstructions);
        tvTitle.setText("" + (position+1) + ". " + instructions);
        TextView tvDetails = (TextView)convertView.findViewById(R.id.details);
        tvDetails.setText(Road.getLengthDurationText(entry.mLength, entry.mDuration));
		int iconId = iconIds.getResourceId(entry.mManeuverType, R.drawable.ic_empty);
   		Drawable icon = mContext.getResources().getDrawable(iconId);
		ImageView ivManeuver = (ImageView)convertView.findViewById(R.id.thumbnail);
   		ivManeuver.setImageDrawable(icon);
        return convertView;
    }

	@Override public void onClick(View arg0) {
		//nothing to do.
	}
    
}
