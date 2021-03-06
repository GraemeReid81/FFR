package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.ffr.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.InterfaceAugmentations.ActivitySwipeDetector;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener.OnDismissCallback;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.socialize.ActionBarUtils;
import com.socialize.entity.Entity;

/**
 * Handles the player info function
 * @author Jeff
 *
 */
public class PlayerInfo 
{
	Rankings obj = new Rankings();
	double aucFactor;
	PlayerObject searchedPlayer;
	List<Map<String, String>> data;
	Storage holder;
	Dialog dialog;
	SimpleAdapter adapter;
	public static Button ranking;
	public static Button info;
	public static Button team;
	public static Button other;
	/**
	 * Abstracted out of the menu handler as this could get ugly
	 * once the stuff is added to the dropdown
	 * @throws IOException
	 */ 
	public void searchCalled(final Context oCont) throws IOException
	{
		Rankings.matchedPlayers = new ArrayList<String>(15);
		Rankings.newCont = oCont;
		ReadFromFile.fetchNames(obj.holder, Rankings.newCont);
		final Dialog dialog = new Dialog(Rankings.newCont, R.style.RoundCornersFull);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
	
		dialog.setContentView(R.layout.search_players);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    
		Rankings.voice = (Button) dialog.findViewById(R.id.speakButton);
	    Rankings.textView = (AutoCompleteTextView)(dialog).findViewById(R.id.player_input);
	    Rankings.voice.setOnClickListener(new OnClickListener() {
	
		    @Override
		    public void onClick(final View v) {
		            ((Rankings)Rankings.newCont).speakButtonClicked(v);
		            
		    }
		});
	    if(Rankings.matchedPlayers.size() == 0)
	    {
	    	ManageInput.setupAutoCompleteSearch(obj.holder, obj.holder.players, Rankings.textView, Rankings.newCont);
	    }
	    Button searchDismiss = (Button)dialog.findViewById(R.id.search_cancel);
		searchDismiss.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button searchSubmit = (Button)dialog.findViewById(R.id.search_submit);
		Rankings.textView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem)arg1).getText1().getText().toString();
				Rankings.textView.setText(text);
			}
		});
		searchSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				/**
				 * On item select, get the name
				 */
				if(obj.holder.parsedPlayers.contains(Rankings.textView.getText().toString()))
				{
					dialog.dismiss();
					outputResults(Rankings.textView.getText().toString(), false, (Rankings)Rankings.newCont, obj.holder, false, true);
				}
				else
				{
					Toast.makeText(oCont, "Not a valid player name", Toast.LENGTH_SHORT).show();
				}
			}
		});
		dialog.show();		
	}
	
	/**
	 * outputs the results to the search dialog
	 * @param namePlayer
	 */
	public void outputResults(final String namePlayer, boolean flag, 
			final Activity act, final Storage hold, final boolean watchFlag, boolean draftable)
	{
		holder = hold;
		dialog = new Dialog(act);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		dialog.setContentView(R.layout.search_output);
		aucFactor = ReadFromFile.readAucFactor(act);
		if(ManageInput.confirmInternet(act))
		{
			// Your entity key. May be passed as a Bundle parameter to your activity
			String entityKey = "http://www.fantasyfootballdraftmanager.com/" + namePlayer + "/player_info";

							// Create an entity object including a name
							// The Entity object is Serializable, so you could also store the whole object in the Intent
			Entity entity = Entity.newInstance(entityKey, namePlayer);

							// Wrap your existing view with the action bar.
							// your_layout refers to the resource ID of your current layout.
			View actionBarWrapped = ActionBarUtils.showActionBar(act, R.layout.search_output, entity);

							// Now set the view for your activity to be the wrapped view.
			dialog.setContentView(actionBarWrapped);
		}
		Button addWatch = (Button)dialog.findViewById(R.id.add_watch);
		//If the add to list boolean exists
		if(!watchFlag)
		{
	    	addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//Check if the player is in the watchList
					int i = -1;
					for(String name : Rankings.watchList)
					{ 
						if(name.equals(namePlayer))
						{
							i++;
							break;
						}
					}
					//if not, add him on the click of the button
					if(i == -1)
					{
						Rankings.watchList.add(namePlayer);
						WriteToFile.writeWatchList(act, Rankings.watchList);
						Toast.makeText(act, namePlayer + " added to watch list", Toast.LENGTH_SHORT).show();
					}
					else//if so, ignore the click
					{
						Toast.makeText(act, namePlayer + " already in watch list", Toast.LENGTH_SHORT).show();
					}
				}
	    	});
		}
		//Otherwise, the call is from the watch list, so it gives the option to remove it
		else
		{
			addWatch.setText("Oust");
			addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(Rankings.context, namePlayer + " removed from watch list", Toast.LENGTH_SHORT).show();
					Rankings.watchList.remove(namePlayer);
					WriteToFile.writeWatchList((Context)act, Rankings.watchList);
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, Rankings.watchList);
				}
			});
		}
		//Create the output, make sure it's valid
		List<String>output = new ArrayList<String>(12);
		final TextView name = (TextView)dialog.findViewById(R.id.name);
		if(namePlayer.equals(""))
		{
			return;
		}
		//Set up the header, and make a mock object with the set name
		name.setText(namePlayer);
		searchedPlayer = new PlayerObject("","","",0);
		for(PlayerObject player : holder.players)
		{
			if(player.info.name.equals(namePlayer))
			{
				searchedPlayer = player;
				break;
			}
		}
		final PlayerObject copy = searchedPlayer;
		final Roster r = ReadFromFile.readRoster(act);
		if(draftable && !holder.isRegularSeason)
		{
			name.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					if(r.isRostered(copy))
					{
						String nameStr = name.getText().toString();
						if(!Draft.isDrafted(nameStr, holder.draft))
						{
							int index = 0;
			                for(int i = 0; i < holder.players.size(); i++)
			                {
	
			               	 	if(Rankings.data.get(i).get("main").contains(namePlayer))
			               	 	{
			               	 		index = i;
			               	 		break;
			               	 	}
			                } 
			               	DecimalFormat df = new DecimalFormat("#.##");
			               	Rankings.data.remove(index);
			               	Rankings.adapter.notifyDataSetChanged();
			               	Map<String, String> datum = new HashMap<String, String>();
			               	boolean isAuction = ReadFromFile.readIsAuction(act);
			               	if(isAuction)
			               	{
			               		datum.put("main", df.format(copy.values.secWorth) + ":  " + copy.info.name);
			               	}
			               	else if(!isAuction && copy.values.ecr != -1)
			               	{
			               		datum.put("main", df.format(copy.values.ecr) + ":  " + copy.info.name);
			               	}
			               	else if(!isAuction && copy.values.ecr == -1)
			               	{
			               		datum.put("main", copy.info.name);
			               	}
			               	datum.put("sub", copy.info.position + " - " + copy.info.team + "\n" + "Bye: " + holder.bye.get(copy.info.team));
							Rankings.handleDrafted(datum, 
									holder, (Activity)Rankings.context, dialog, index);
							return true;
						}
						else
						{
							Toast.makeText(act, "That player is already drafted", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(act, "Your league has no roster spots for this position. Are the roster settings up to date?", Toast.LENGTH_SHORT).show();
					}
					return true;
				}
			});
		}
		//If it's called from trending or watch list, ignore back
		if(flag)
		{
			Button backButton = (Button)dialog.findViewById(R.id.search_back);
			backButton.setVisibility(Button.GONE);
			View backView = (View)dialog.findViewById(R.id.back_view);
			backView.setVisibility(View.GONE);
		}
		//Set the data in the list
		data = new ArrayList<Map<String, String>>();
		RelativeLayout base = (RelativeLayout)dialog.findViewById(R.id.info_sub_header);
		if(searchedPlayer.info.position.length() >= 1 && searchedPlayer.info.team.length() > 1)
		{
			Button leftPos = (Button)dialog.findViewById(R.id.dummy_btn_left);
			leftPos.setText("Age:\n" + searchedPlayer.info.age);
			Button rightPos = (Button)dialog.findViewById(R.id.dummy_btn_right);
			rightPos.setText("Bye:\n" + holder.bye.get(searchedPlayer.info.team));
			Button centerPos = (Button)dialog.findViewById(R.id.dummy_btn_center);
			centerPos.setText(searchedPlayer.info.team + "\n" + searchedPlayer.info.position);
			if(searchedPlayer.info.position.equals("D/ST") || searchedPlayer.info.age == null || searchedPlayer.info.age.length() <= 1)
			{
				leftPos.setText("Age:\nN/A");
			}
			if(searchedPlayer.info.team.equals("None") || searchedPlayer.info.team.equals("---") || searchedPlayer.info.team.equals("FA"))
			{
				if(searchedPlayer.info.position.length() >= 1)
				{
					centerPos.setText(searchedPlayer.info.position);
					rightPos.setText("Bye:\nN/A");
				}
				else
				{
					base.setVisibility(View.GONE);
				}
			}
		} 
		else
		{
			base.setVisibility(View.GONE);
		}
		adapter = new SimpleAdapter(act, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		setSearchContent(searchedPlayer, data, holder, dialog, adapter, act);
		//Show the dialog, then set the list
		dialog.show();
		BounceListView results = (BounceListView)dialog.findViewById(R.id.listview_search);
		results.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String input = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				//Show tweets about the player
				if(input.contains("See tweets about"))
				{
					playerTweetSearchInit(namePlayer, act);
				}
				//Bring up the interweb to show highlights of the player
				else if(input.contains("See highlights of"))
				{
					String[] nameArr = namePlayer.split(" ");
					String url = "http://www.youtube.com/results?search_query=";
					for(String name : nameArr)
					{
						url += name + "+";
					}
					url += "highlights";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					act.startActivity(i);
				}
				else if(input.contains("Risk"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("The 'Risk' of a player is the relative variation in the total set of expert rankings. The logic is, the less established the value of the player is (a higher variance), the riskier he is.\n\n");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("Project"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This projection is the weighted average of a series of experts.\n\n");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("PAA"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("PAA attempts to quantify the value has cross-positions. It means points above average. For example, tight ends are generally not highly valued. There are a few, though, that give such a large edge over the alternative that they should be valued highly. Their PAA is high.\n\n");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("Average Draft Position"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This is the average draft position of a player over thousands and thousands of mock drafts.\n\n");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
				else if(input.contains("Ranking"))
				{
					final Dialog popUp = new Dialog(act, R.style.RoundCornersFull);
				    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
					popUp.setContentView(R.layout.tweet_popup);
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(popUp.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.FILL_PARENT;
				    popUp.getWindow().setAttributes(lp);
				    popUp.show();
				    TextView tv = (TextView)popUp.findViewById(R.id.tweet_field);
				    tv.setText("This is the weighted average ranking (ECR) of many different experts, hosted on FantasyPros\n\n");
				    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
				    close.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							popUp.dismiss();
						}
				    });
				}
			}
		});
		//ManageInput.handleArray(output, results, act);
	    results.setAdapter(adapter);
	    ActivitySwipeDetector asd = new ActivitySwipeDetector(act, this);
	    asd.origin = "Popup";
	    results.setOnTouchListener(asd);
		Button back = (Button)dialog.findViewById(R.id.search_back);
		//If it isn't gone, set that it goes back
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					dialog.dismiss();
					searchCalled(Rankings.newCont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); 
		//Setting up close
		Button close = (Button)dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener()
		{ 
			public void onClick(View v) {
				//If it is called from trending or rankings, dismiss it
				if(!watchFlag)
				{
					dialog.dismiss();
				}
				else //otherwise it was from watch list, so call back from there
				{
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, Rankings.watchList);
				}
			}
		}); 
	}

	/**
	 * Sets the output of the search
	 * @param searchedPlayer
	 * @param data
	 * @param holder 
	 * @param dialog 
	 * @param adapter 
	 */
	public void setSearchContent(PlayerObject searchedPlayer, List<Map<String, String>> data, Storage holder, Dialog dialog, SimpleAdapter adapter, final Context cont)
	{
	   	ranking = (Button)dialog.findViewById(R.id.category_ranking);
	   	info = (Button)dialog.findViewById(R.id.category_info);
	   	team = (Button)dialog.findViewById(R.id.category_team);
	   	other = (Button)dialog.findViewById(R.id.category_other);
	   	ranking.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				contentRankings();
				ranking.setTypeface(null,Typeface.BOLD);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(14);
				info.setTextSize(13);
				team.setTextSize(13);
				other.setTextSize(13);
			}
	   	});
	   	info.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				contentInfo();
				ranking.setTypeface(null,Typeface.NORMAL);
				info.setTypeface(null, Typeface.BOLD);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(13);
				info.setTextSize(14);
				team.setTextSize(13);
				other.setTextSize(13);
			}
	   	});
	   	team.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				contentTeam(); 
				ranking.setTypeface(null,Typeface.NORMAL);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.BOLD);
				other.setTypeface(null, Typeface.NORMAL);
				ranking.setTextSize(13);
				info.setTextSize(13);
				team.setTextSize(14);
				other.setTextSize(13);
			}
	   	});
	   	other.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				contentOther();
				ranking.setTypeface(null,Typeface.NORMAL);
				info.setTypeface(null, Typeface.NORMAL);
				team.setTypeface(null, Typeface.NORMAL);
				other.setTypeface(null, Typeface.BOLD);
				ranking.setTextSize(13);
				info.setTextSize(13);
				team.setTextSize(13);
				other.setTextSize(14);
			}
	   	});
	   	contentRankings();
	}
	
	public void swipeLeftToRight()
	{
		if(isMax(ranking))
		{
			contentOther();
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.BOLD);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(14);
		}
		else if(isMax(info))
		{
			contentRankings();
			ranking.setTypeface(null,Typeface.BOLD);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(14);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(13);
		}
		else if(isMax(team))
		{
			contentInfo();
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.BOLD);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(14);
			team.setTextSize(13);
			other.setTextSize(13);
		}
		else if(isMax(other))
		{
			contentTeam(); 
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.BOLD);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(14);
			other.setTextSize(13);
		}
	}
	
	/**
	 * Determines if a button passed in is currently selected
	 * @param b
	 * @return
	 */
	public boolean isMax(Button b)
	{
		if(b.getTextSize() >= ranking.getTextSize() &&
				b.getTextSize() >= info.getTextSize() &&
				b.getTextSize() >= team.getTextSize() &&
				b.getTextSize() >= other.getTextSize())
		{
			return true;
		}
		return false;
	}
	
	public void swipeRightToLeft()
	{
		if(isMax(ranking))
		{
			contentInfo();
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.BOLD);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(14);
			team.setTextSize(13);
			other.setTextSize(13);
		}
		else if(isMax(info))
		{
			contentTeam();
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.BOLD);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(14);
			other.setTextSize(13);
		}
		else if(isMax(team))
		{
			contentOther(); 
			ranking.setTypeface(null,Typeface.NORMAL);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.BOLD);
			ranking.setTextSize(13);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(14);
		}
		else if(isMax(other))
		{
			contentRankings();
			ranking.setTypeface(null,Typeface.BOLD);
			info.setTypeface(null, Typeface.NORMAL);
			team.setTypeface(null, Typeface.NORMAL);
			other.setTypeface(null, Typeface.NORMAL);
			ranking.setTextSize(14);
			info.setTextSize(13);
			team.setTextSize(13);
			other.setTextSize(13);
		}
	}
	
	/**
	 * Fills the adapter with ranking based information
	 */
	public void contentRankings()
	{
		data.clear();
	   	DecimalFormat df = new DecimalFormat("#.##");
		//Worth
		if(!holder.isRegularSeason)
		{
			Map<String, String> datumWorth = new HashMap<String, String>(2);
			if(searchedPlayer.values.secWorth <= 0.0 && !(aucFactor == 0.0))
			{
				datumWorth.put("main", "$" + df.format(searchedPlayer.values.worth / aucFactor));
			}
			else
			{
				datumWorth.put("main", "$" + df.format(searchedPlayer.values.secWorth));
			}
			if(searchedPlayer.info.position.length() >= 1)
			{
				datumWorth.put("sub", "Ranked " + rankCostPos(searchedPlayer, holder) + " positionally, " + rankCostAll(searchedPlayer, holder) + " overall"
						+ "\nShowed up in " + searchedPlayer.values.count + " rankings");
			}
			else
			{
				datumWorth.put("sub", "Ranked " + rankCostAll(searchedPlayer, holder) + " overall\n" + "Showed up in " + searchedPlayer.values.count + " rankings");
			}
			data.add(datumWorth);
		}
		if(holder.isRegularSeason)
		{
			if(searchedPlayer.values.rosRank > 0)
			{
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("main", "ROS Positional Ranking: " + searchedPlayer.values.rosRank);
				datum.put("sub", "");
				data.add(datum);
			}
		}
		//Rank ecr
		if(searchedPlayer.values.ecr != -1)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(!holder.isRegularSeason)
			{
				datum.put("main", "Preseason Ranking: " + searchedPlayer.values.ecr);
				if(rankECRPos(searchedPlayer, holder) != -1 && searchedPlayer.info.position.length() >= 1)
				{
					datum.put("sub", "Ranked " + rankECRPos(searchedPlayer, holder) + " positionally");
				}
				else
				{
					datum.put("sub", "");
				}
			}
			else
			{
				datum.put("main", "Weekly Positional Ranking: " + searchedPlayer.values.ecr.intValue());
				datum.put("sub", "");
			}
			data.add(datum);
		}
		//ADP Rankings
		if(!searchedPlayer.info.adp.equals("Not set"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(!holder.isRegularSeason)
			{
				if(rankADPPos(searchedPlayer, holder) != -1)
				{
					datum.put("main", "Average Draft Position: " + searchedPlayer.info.adp);
					datum.put("sub", "Ranked " + rankADPPos(searchedPlayer, holder) + " positionally");
					data.add(datum);
				}
				else
				{
					datum.put("main", "Average Draft Position: " + searchedPlayer.info.adp);
					datum.put("sub", "");
					data.add(datum);
				}
			}
			else
			{
				datum.put("main", "Playing The " + searchedPlayer.info.adp);
				datum.put("sub", "Positional SOS: " + holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position) + 
							"\n1 is Easiest, 32 Hardest");
				data.add(datum);
			}
		}
		//Projections
		if(searchedPlayer.values.points >= 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			if(!holder.isRegularSeason)
			{
				datum.put("main", searchedPlayer.values.points + " Projection This Year");
			}
			else
			{
				datum.put("main", searchedPlayer.values.points + " Projection This Week");
			}
			if(searchedPlayer.info.position.length() >= 1)
			{
				datum.put("sub", "Ranked " + rankProjPos(searchedPlayer, holder)  + " positionally");
			}
			else
			{
				datum.put("sub", "");
			}
			data.add(datum);
		}
		//PAA and PAAPD
		if(searchedPlayer.values.paa != 0.0 && searchedPlayer.values.points != 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", df.format(searchedPlayer.values.paa) + " PAA");
			if(searchedPlayer.info.position.length() >= 1)
			{
				datum.put("sub", "Ranked " + rankPAAPos(searchedPlayer, holder) + " positionally, " + rankPAAAll(searchedPlayer, holder) +
							 " overall");
			}
			else
			{
				datum.put("sub", "Ranked " + rankPAAAll(searchedPlayer, holder) + " overall");
			}
			data.add(datum);
		} 
		//Risk
		if(searchedPlayer.risk > 0.0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			double riskVal = posRiskVal(searchedPlayer, holder);
			datum.put("main", searchedPlayer.risk + " Risk (" + rankRiskAll(searchedPlayer, holder) + ")");
			if(searchedPlayer.info.position.length() >= 1)
			{
				datum.put("sub", df.format(riskVal) + " relative to his position (" + rankRiskPos(searchedPlayer, holder, riskVal) + ")");
			}
			else 
			{
				datum.put("sub", "");
			}
			data.add(datum);
		}
		//Positional SOS
		if(holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position)!= null && 
				holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position) > 0 && !holder.isRegularSeason)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "Positional SOS: " + holder.sos.get(searchedPlayer.info.team + "," + searchedPlayer.info.position));
			datum.put("sub", "1 is Easiest, 32 Hardest");
			data.add(datum);
		}	
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Fills the pop up up with basic player information
	 */
	public void contentInfo()
	{
		data.clear();
		Map<String, String> basicDatum = new HashMap<String, String>(2);
		basicDatum.put("main", searchedPlayer.info.position + " - " + searchedPlayer.info.team);
		String sub = "";
		if(ManageInput.isInteger(searchedPlayer.info.age) && Integer.valueOf(searchedPlayer.info.age)> 0)
		{
			sub += "Age: " + searchedPlayer.info.age + "\n";
		}
		if(searchedPlayer.info.team.length() > 4)
		{
			sub += "Bye: " + holder.bye.get(searchedPlayer.info.team);
		}
		basicDatum.put("sub", sub);
		data.add(basicDatum);
		//Contract status
		DecimalFormat df = new DecimalFormat("#.##");
		if(Draft.draftedMe(searchedPlayer.info.name, obj.holder.draft))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED BY YOU");
			data.add(datum);
		}
		//See if they're drafted by someone else
		else if(Draft.isDrafted(searchedPlayer.info.name, obj.holder.draft))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "DRAFTED");
			data.add(datum);
		}
		//Is he in the watch list?
		if(Rankings.watchList.contains(searchedPlayer.info.name))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "He is in your watch list");
			datum.put("sub", "");
			data.add(datum);
		}
		if(!searchedPlayer.injuryStatus.contains("Healthy"))
		{
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", searchedPlayer.injuryStatus);
			datum2.put("sub", "");
			data.add(datum2);
		}
		if(!searchedPlayer.info.position.equals("D/ST") && 
				!searchedPlayer.info.contractStatus.contains("Under Contract"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.info.contractStatus);
			datum.put("sub", "");
			data.add(datum);
		} 
		//Injury status and stats
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST")
				&& !searchedPlayer.stats.equals(" ") && searchedPlayer.stats.length() > 5)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", searchedPlayer.stats);
			datum.put("sub", "");
			if(searchedPlayer.stats.contains("Pass Attempts") && searchedPlayer.stats.contains("Interceptions"))
			{
				String intsA = searchedPlayer.stats.split("Interceptions: ")[1].split("\n")[0];
				int intA = Integer.parseInt(intsA);
				String compA = searchedPlayer.stats.split("Completion Percentage: ")[1].split("\n")[0].replace("%", "");
				double compPercent = Double.parseDouble(compA)/100.0;
				double attempts = Double.parseDouble(searchedPlayer.stats.split("Pass Attempts: ")[1].split("\n")[0]);
				double completions = attempts * compPercent;
				double aDiff = (completions)/((double)intA);
				int rank = rankIntComp(holder, aDiff);
				datum.put("sub", df.format(aDiff) + " completion to interception ratio (" + rank + ")");
			}
			data.add(datum);

				}	
		if(data.size() == 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No information available for this player");
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Notifies the adapter that team information is desired
	 */
	public void contentTeam()
	{
		data.clear();
		//O line data
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
		{ 
			if(holder.oLineAdv.get(searchedPlayer.info.team)!= null && holder.oLineAdv.get(searchedPlayer.info.team).length() > 3)
			{
				Map<String, String> datum = new HashMap<String, String>(2);
				String oLineAdv = holder.oLineAdv.get(searchedPlayer.info.team);
				String oLinePrimary = oLineAdv.split("~~~~")[0];
				String oLineRanks = oLineAdv.split("~~~~")[1];
				datum.put("main", "Offensive Line Data:\n\n" + oLinePrimary);
				datum.put("sub", oLineRanks);
				data.add(datum);
			}
		}
		//Draft class
		if(holder.draftClasses.get(searchedPlayer.info.team) != null && !holder.draftClasses.get(searchedPlayer.info.team).contains("null") &&
				!searchedPlayer.info.position.equals("K") && holder.draftClasses.get(searchedPlayer.info.team).length() > 4)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			String draft = holder.draftClasses.get(searchedPlayer.info.team).substring(holder.draftClasses.get(searchedPlayer.info.team).indexOf('\n'), holder.draftClasses.get(searchedPlayer.info.team).length());
			String gpa = holder.draftClasses.get(searchedPlayer.info.team).split("\n")[0];
			datum.put("main", "Draft Recap:\n" + draft);
			datum.put("sub", gpa);
			data.add(datum);
		}
		//Free agency classes
		List<String> fa = holder.fa.get(searchedPlayer.info.team);
		if(fa != null)
		{
			if(fa.size() > 1)
			{
		    	if(fa.get(0).contains("\n"))
		    	{
	                Map<String, String> datum = new HashMap<String, String>(2);
	                datum.put("main", "Signed Free Agents:\n\n" + fa.get(0).split(": ")[1]);
		    		datum.put("sub", "");
		    		data.add(datum);
		    	}
		    	if(fa.get(1).contains("\n"))
		    	{
	                Map<String, String> datum = new HashMap<String, String>(2);
	                datum.put("main",  "Departing Free Agents:\n\n" + fa.get(1).split(": ")[1]);
		    		datum.put("sub", "");
		    		data.add(datum);
		    	}
			}
		}
		if(data.size() == 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No team information available for this player");
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Notifies the adapter that other data is desired
	 */
	public void contentOther()
	{
		data.clear();
		if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "See tweets about this player");
			datum.put("sub", "");
			data.add(datum);
			Map<String, String> datum2 = new HashMap<String, String>(2);
			datum2.put("main", "See highlights of this player");
			datum2.put("sub", "");
			data.add(datum2);
		}
		if(data.size() == 0)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", "No additional information available about this player");
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Gets the risk relative to a position
	 * @param player
	 * @param holder
	 * @return
	 */
	public static double posRiskVal(PlayerObject player, Storage holder)
	{
		double riskPos = 0.0;
		double posCounter = 0.0;
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position))
			{
				if(iter.risk > 0.0)
				{
					riskPos += iter.risk;
					posCounter++;
				}
			}
		}
		return player.risk - riskPos/posCounter;
	}

	
	/**
	 * Ranks risk relative to position
	 * @param riskVal 
	 */
	public static int rankRiskPos(PlayerObject player, Storage holder, double riskVal)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position) && iter.risk < player.risk && iter.risk > 0)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks risk relative to all
	 */
	public static int rankRiskAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.risk < player.risk && iter.risk >0)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks ECR positionally
	 */
	public static int rankECRPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		if(player.values.ecr == -1)
		{
			return -1;
		}
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.ecr == -1)
			{
				continue;
			}
			if(iter.info.position.equals(player.info.position)&&iter.values.ecr < player.values.ecr)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the adp positionally
	 */
	public static int rankADPPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		if(player.info.adp.equals("Not set"))
		{
			return -1;
		}
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.adp.equals("Not set"))
			{
				continue;
			}
			if(iter.info.position.equals(player.info.position) && Double.parseDouble(iter.info.adp)
					< Double.parseDouble(player.info.adp))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the projections positionally
	 */
	public static int rankProjPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.points != 0.0 && iter.values.points > player.values.points && 
					iter.info.position.equals(player.info.position))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paa among positional players
	 */
	public static int rankPAAPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paa != 0.0 && iter.info.position.equals(player.info.position) && 
					iter.values.paa > player.values.paa)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks paa among all players
	 */
	public static int rankPAAAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.paa != 0.0 && iter.values.paa > player.values.paa)
			{
				rank++;
			}
		}
		return rank;
	}

	public static int rankIntComp(Storage holder, double aDiff2)
	{
		int rank = 1;
		for(PlayerObject searchedPlayer : holder.players)
		{
			if(searchedPlayer.info.position.equals("QB") && 
					searchedPlayer.stats.contains("Pass Attempts") && searchedPlayer.stats.contains("Interceptions"))
			{
				String intsA = searchedPlayer.stats.split("Interceptions: ")[1].split("\n")[0];
				int intA = Integer.parseInt(intsA);
				String compA = searchedPlayer.stats.split("Completion Percentage: ")[1].split("\n")[0].replace("%", "");
				double compPercent = Double.parseDouble(compA)/100.0;
				double attempts = Double.parseDouble(searchedPlayer.stats.split("Pass Attempts: ")[1].split("\n")[0]);
				double completions = attempts * compPercent;
				double aDiff = (completions)/((double)intA);
				if(aDiff > aDiff2)
				{
					rank++;
				}
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the worth positionally
	 */
	public static int rankCostPos(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position) && iter.values.worth > player.values.worth)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the worth overall
	 */
	public static int rankCostAll(PlayerObject player, Storage holder)
	{
		int rank = 1;
		for(PlayerObject iter : holder.players)
		{
			if(iter.values.worth > player.values.worth)
			{
				rank++;
			}
		}
		return rank;
	}
 
	/**
	 * Calls the asynchronous search of tweets about a player
	 */
	public static void playerTweetSearchInit(String name, Activity act)
	{
		TwitterWork obj = new TwitterWork();
		obj.twitterInitial(act, 3, name, false);
		//ParseNews.startTwitterSearchAsync(act, name, "Twitter Search: " + name, false, name, obj);
	}

	/**
	 * Displays the output of the tweets about the player
	 * @param result
	 * @param act
	 */
	public static void playerTweetSearch(List<NewsObjects> result, final Activity act, String name)
	{
		final Dialog dialog = new Dialog(act);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.player_tweet_search); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    TextView header = (TextView)dialog.findViewById(R.id.name);
	    header.setText("Twitter Search: \n" + name);
	    BounceListView tweetResults = (BounceListView)dialog.findViewById(R.id.listview_search);
	    Button close = (Button)dialog.findViewById(R.id.search_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	    for(NewsObjects newsObj : result)
	    {
	    	Map<String, String> datum = new HashMap<String, String>();
	    	datum.put("news", newsObj.news + "\n\n" + newsObj.impact);
	    	datum.put("date", newsObj.date);
	    	data.add(datum);
	    }
	    final SimpleAdapter adapter = new SimpleAdapter(act, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"news", "date"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    tweetResults.setAdapter(adapter);
	    SwipeDismissListViewTouchListener touchListener =
	            new SwipeDismissListViewTouchListener(
	                    tweetResults,
	                    new SwipeDismissListViewTouchListener.OnDismissCallback() {
	                        @Override
	                        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
	                            for (int position : reverseSortedPositions) {
	                                data.remove(position);
	                            }
	                            adapter.notifyDataSetChanged();
	                            Toast.makeText(Rankings.context, "Temporarily hiding this news piece", Toast.LENGTH_SHORT).show();
	                            if(adapter.isEmpty())
	                            {
	                            	dialog.dismiss();
	                            	Toast.makeText(Rankings.context, "You've hidden all of the tweets", Toast.LENGTH_SHORT).show();
	                            }
	                        }
	                    });
	    tweetResults.setOnTouchListener(touchListener);
	    tweetResults.setOnScrollListener(touchListener.makeScrollListener());
	}
}
