package AsyncTasks;

import java.io.IOException;








import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;






















import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlcleaner.XPatherException;
import org.jsoup.HttpStatusException;
import org.xml.sax.SAXException;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.TwitterWork;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPN;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPNadv;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseJuanElway;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFantasyPros;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTheFakeFootball;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNFL;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseOLineAdvanced;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePFF;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePlayerNames;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseSI;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseYahoo;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.Pages.News;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile; 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
/**
 * A library of all the asynctasks involving parsing
 * @author Jeff
 *
 */
public class ParsingAsyncTask 
{
		/**
		 * This handles the running of the rankings in the background
		 * such that the user can't do anything until they're fetched
		 * @author Jeff
		 *
		 */
		long start;
		long all;
		public class ParseRanks extends AsyncTask<Object, String, Void> 
		{
			ProgressDialog pdia;
			Activity act;
			Storage hold;
		    public ParseRanks(Activity activity, Storage holder) 
		    {
		        pdia = new ProgressDialog(activity);
		        pdia.setCancelable(false);
		        act = activity;
		        hold = holder;
		    }

			@Override
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia.setMessage("Please wait, fetching the rankings...(0/24)");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(Void result){
			   super.onPostExecute(result);
			   pdia.dismiss();
			   if(hold.players.size() > 1)
			   {
				   ((Rankings) act).intermediateHandleRankings(act);
			   }
			}

		    @Override
		    protected Void doInBackground(Object... data) 
		    {
		    	Storage holder = (Storage) data[0];
		    	Context cont = (Context) data[1];
		    	Map<String, List<String>> fa = new HashMap<String, List<String>>();
		    	Map<String, String> draftClasses = new HashMap<String, String>(); 
		    	if(holder.isRegularSeason)
		    	{
		    		fa = holder.fa;
		    		draftClasses = holder.draftClasses;
		    	}
				holder.players.clear();
		    	holder.parsedPlayers.clear();
	    		all = System.nanoTime();
	    		System.out.println("Before WF");
	    		try {
					ParseWF.wfRankings(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e15) {

				} 
		        publishProgress("Please wait, fetching the rankings...(3/24)");
				System.out.println("Before CBS");
				try {
					ParseCBS.cbsRankings(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e14) {

				}
				System.out.println("Before ESPN ADV");
		        publishProgress("Please wait, fetching the rankings...(6/24)");
				
				try {
					ParseESPNadv.parseESPNAggregate(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e13) {
					// TODO Auto-generated catch block
				} catch (XPatherException e13) {
					// TODO Auto-generated catch block
					e13.printStackTrace();
				}
		        publishProgress("Please wait, fetching the rankings...(7/24)");
		        System.out.println("Before FFTB");
				try {
					ParseFFTB.parseFFTBRankingsWrapper(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (MalformedURLException e12) {
					// TODO Auto-generated catch block
					e12.printStackTrace();
				} catch (IOException e12) {
				} catch (XPatherException e12) {
					// TODO Auto-generated catch block
					e12.printStackTrace();
				}
		        publishProgress("Please wait, fetching the rankings...(8/24)");
		        System.out.println("Before espn");
				try {
					ParseESPN.parseESPN300(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e11) {
				}
		        publishProgress("Please wait, fetching the rankings...(10/24)");
		        System.out.println("Before Yahoo");
				try {
					ParseYahoo.parseYahooWrapper(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e9) {
				}
		        publishProgress("Please wait, fetching the rankings...(12/24)");
		        System.out.println("Before Fantasy Pros");
				try {
					ParseFantasyPros.parseFantasyProsAgg(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e8) {
				}
				publishProgress("Please wait, fetching the rankings...(18/24)");
				System.out.println("Before PFF");
				try {
					ParsePFF.parsePFFWrapper(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e7) {
					// TODO Auto-generated catch block
					e7.printStackTrace();
				}
				publishProgress("Please wait, fetching the rankings...(19/24)");
				System.out.println("Before TFF");
				try {
					ParseTheFakeFootball.parseTheFakeFootballVals(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e6) {
				} 
				publishProgress("Please wait, fetching the rankings...(21/24)");
				System.out.println("Before NFL AAV");
				try {
					ParseNFL.parseNFLAAVWrapper(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e3) {
				}
				System.out.println("Before Juan Elway");
				publishProgress("Please wait, fetching the rankings...(22/24)");
				try { 
					ParseJuanElway.parseJuanElwayVals(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
				publishProgress("Please wait, fetching the rankings...(23/24)");
				System.out.println("Before SI");
		    	try {
					ParseSI.parseSIWrapper(holder);
					publishProgress("Please wait, fetching the rankings...(24/24)");
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}

	    		publishProgress("Please wait, calculating relative risk...");
	    		try {
					HighLevel.parseECRWrapper(holder, cont);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
		    	
		    	start = System.nanoTime(); 
	    		publishProgress("Please wait, fetching player stats...");
				try {
					HighLevel.setStats(holder, cont);
				}catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e) {
				}

	    		publishProgress("Please wait, fetching team data...");
	    		if(!holder.isRegularSeason || (holder.isRegularSeason && (fa.size() < 5 || draftClasses.size() < 5)))
	    		{
					try {
						HighLevel.setTeamInfo(holder, cont);
					} catch (HttpStatusException e2)
					{
						System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
					} catch (IOException e1) {
					}
	    		}
	    		else
	    		{
	    			holder.fa = fa;
	    			holder.draftClasses = draftClasses;
	    		}

	    		
				publishProgress("Please wait, fetching positional SOS...");
				
				try {
					if(!holder.isRegularSeason)
					{
						HighLevel.getSOS(holder);
					}
					else
					{
						ParseFFTB.parseSOSInSeason(holder);
					}
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
				
				publishProgress("Please wait, fetching player contract status...");
	    		try {
					HighLevel.setContractStatus(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}

			    publishProgress("Please wait, setting specific player info...");
	    		try {
					HighLevel.parseSpecificData(holder, cont);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}

	    		publishProgress("Please wait, getting advanced line stats...");
	    		try {
					ParseOLineAdvanced.parsePFOLineData(holder);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}

	    		publishProgress("Please wait, getting projected points...");
	    		try {
					HighLevel.projPointsWrapper(holder, cont);
				} catch (HttpStatusException e2)
				{
					System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
				} catch (IOException e1) {
				}
	    		publishProgress("Please wait, normalizing projections...");
	    		HighLevel.getPAA(holder, cont);
	    		
	    		if(holder.isRegularSeason)
	    		{
	    			publishProgress("Please wait, getting rest of season rankings...");
	    			try {
						HighLevel.getROSRankingsWrapper(holder, cont);
					} catch (HttpStatusException e2)
					{
						System.out.println(e2.getStatusCode() + ", " + e2.getUrl());
					} catch (IOException e1) {
					}
	    		}
				return null;
		    }

		    @Override
		    public void onProgressUpdate(String... values)
		    {
		    	super.onProgressUpdate(values);
		    	pdia.setMessage((String) values[0]);
		    }
		  }

		/**
		 * Re-calls projections, and stores changes.
		 * @author Jeff
		 *
		 */
		public class ParseProjections extends AsyncTask<Object, String, Void> 
		{
			Activity act;
			Storage hold;
			ProgressDialog pdia;
		    public ParseProjections(Activity activity, Storage holder) 
		    {
		    	pdia = new ProgressDialog(activity);
		    	pdia.setCancelable(false);
		        act = activity;
		        hold = holder;
		    }

			@Override
			protected void onPreExecute(){ 
				pdia.setMessage("Please wait, updating and saving the projections...");
		        pdia.show();
			   super.onPreExecute();   
			}

			@Override
			protected void onPostExecute(Void result){
				pdia.dismiss();
				act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			   super.onPostExecute(result);
			}

		    @Override
		    protected Void doInBackground(Object... data) 
		    {
		    	Storage holder = (Storage) data[0];
		    	Context cont = (Context) data[1];
		    	WriteToFile.writeTeamData(holder, cont);
		    	try {
					HighLevel.projPointsWrapper(holder, cont);
					HighLevel.parseECRWrapper(holder, cont);
					HighLevel.getPAA(holder, cont);
					SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
			    	//Rankings work
					Set<String> playerData = new HashSet<String>();
			    	for (PlayerObject player : holder.players)
			    	{
				    	StringBuilder players = new StringBuilder(10000);
			    		players.append(Double.toString(player.values.worth));
			    		players.append("&&");
			    		players.append(Double.toString(player.values.count));
			    		players.append("&&");
			    		players.append(player.info.name);
			    		players.append("&&");
			    		players.append(player.info.team);
			    		players.append("&&");
			    		players.append(player.info.position);
			    		players.append("&&");
			    		players.append(player.info.adp);
			    		players.append("&&");
			    		players.append(player.info.contractStatus);
			    		players.append("&&");
			    		players.append(player.info.age);
			    		players.append("&&");
			    		players.append(player.stats);
			    		players.append("&&");
			    		players.append(player.injuryStatus);
			    		players.append("&&");
			    		players.append(player.values.ecr);
			    		players.append("&&");
			    		players.append(player.risk);
			    		players.append("&&");
			    		players.append(player.values.points);
			    		players.append("&&");
			    		players.append(player.values.paa);
			    		players.append("&&");
			    		players.append(player.values.rosRank);
			    		playerData.add(players.toString());
			    	}
			    	editor.putStringSet("Parsed Player Names", new HashSet<String>(holder.parsedPlayers));
			    	editor.putStringSet("Player Values", playerData).commit();
				} catch (IOException e) {
					return null;
				}
				return null;
		    }

		  }




		/**
		 * Parses the posts from the forums
		 * @author Jeff
		 *
		 */
		public class FetchTrends extends AsyncTask<Object, Void, Void> 
		{
			Activity act;
			ProgressDialog pdia;
			Storage holder;
		    public FetchTrends(Activity activity, Storage hold) 
		    {
		        act = activity;
		        pdia = new ProgressDialog(activity);
		        pdia.setCancelable(false);
		        holder = hold;
		    }
			@Override
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia = new ProgressDialog(act);
			        pdia.setCancelable(false);
			        pdia.setMessage("Please wait, parsing the forums. This could take a few minutes...");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(Void result){
			   super.onPostExecute(result);
			   pdia.dismiss();
			   if(holder.posts.size() > 1)
			   {
				   WriteToFile.writePosts(holder, act);
			   }
			   Trending.setContent(act);
			}
	    	
		    @Override
		    protected Void doInBackground(Object... data) 
		    {
		    	Storage hold = (Storage) data[0];
		    	Context cont = (Context) data[1];
		    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
				boolean value =  prefs.getBoolean("Value Topic", true);
				boolean mustHave = prefs.getBoolean("Good Topic", true);
				boolean rookie = prefs.getBoolean("Rookie Topic", true);
				boolean dontWant = prefs.getBoolean("Bad Topic", false);

		    	holder = hold;
				try {
					if(mustHave)
					{
				    	//2013 'Must Haves'
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=338991&st=");
						//RB rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=344555&st=");
						//QB rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=329554&st=");
						//WR rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=339910&st=");
						//TE rankings
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=347782&st=");
						//D/K don't exist
					}
					if(value)
					{
						//Value picks
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=332995&st=");
						//2013 sleepers
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=327212&st=");
						//adp steals
						ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=354905&st=");
					}
					if(rookie)
			 		{
				 		//Rookie rankings
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=331665&st=");
			 			//Draft thread
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=345800&st=");
			 		}
			 		if(dontWant)
			 		{
			 			//Overvalued
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=334675&st=");
			 			//Don't draft
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=345722&st=");
			 			//Busts
			 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=347469&st=");
			 		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		    }
		  }

		/**
		 * This handles the running of the rankings in the background
		 * such that the user can't do anything until they're fetched
		 * @author Jeff
		 *
		 */
		public class ParseNames extends AsyncTask<Object, Void, Void> 
		{
			ProgressDialog pdia;
			Activity act;
			boolean isFirstFetch;
		    public ParseNames(Activity activity, boolean iff) 
		    {
		        pdia = new ProgressDialog(activity);
		        pdia.setCancelable(false);
		        act = activity;
		        isFirstFetch = iff;
		    }

			@Override
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia.setMessage("Please wait, fetching the player names list...");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(Void result){
			   super.onPostExecute(result);
			   act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			   pdia.dismiss();
			   if(isFirstFetch)
			   {
				   Intent intent = new Intent(act, Rankings.class);
			       act.startActivity(intent);	
			   }
			}

		    @Override
		    protected Void doInBackground(Object... data) 
		    {
		    	Context cont = (Context) data[0];
		    	try {
					ParsePlayerNames.fetchPlayerNames(cont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		    }
		  }

		

		/**
		 * Handles the back-end parsing of the news
		 * @author Jeff
		 *
		 */
		public class ParseRotoWorldNews extends AsyncTask<Object, Void, List<NewsObjects>> 
		{
			ProgressDialog pdia;
			Activity act;
		    public ParseRotoWorldNews(Context cont) 
		    {
		        pdia = new ProgressDialog(cont);
		        pdia.setCancelable(false);
		        act = (Activity)cont;
		    }

			@Override
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia.setMessage("Please wait, fetching the news...");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(List<NewsObjects> result){
			   super.onPostExecute(result);
			   pdia.dismiss();
			   ((News) act).handleNewsListView(result, act);
			}

		    @Override
		    protected List<NewsObjects> doInBackground(Object... data) 
		    {
		    	Context cont = (Context) data[0];
		    	boolean rh = (Boolean)data[1];
		    	boolean rp = (Boolean)data[2];
		    	boolean th = (Boolean)data[3];
		    	boolean cbs = (Boolean)data[4];
		    	boolean si = (Boolean)data[5];
		    	try {
		    		List<NewsObjects> news = new ArrayList<NewsObjects>(100);
		    		if(rh)
		    		{
		    			news = ParseNews.parseNewsRoto("http://www.rotoworld.com/headlines/nfl/0/football-headlines");
		    		}
		    		else if(rp)
		    		{
		    			news = ParseNews.parseNewsRoto("http://www.rotoworld.com/playernews/nfl/football-player-news");
		    		}
		    		else if(th)
		    		{
		    			news = ParseNews.parseNewsHuddle();
		    		}
		    		else if(cbs)
		    		{
		    			news = ParseNews.parseCBS();
		    		}
		    		else if(si)
		    		{
		    			news = ParseNews.parseSI();
		    		}
		    		WriteToFile.writeNewsRoto(cont, news, rh, rp, th, cbs, si);
					return news;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		    }
		  }

		/**
		 * Handles the back-end parsing of the twitter feeds
		 * @author Jeff
		 *
		 */
		public class ParseTwitterFeeds extends AsyncTask<Object, Void, List<NewsObjects>> 
		{
			ProgressDialog pdia;
			Activity act;
		    public ParseTwitterFeeds(Context cont) 
		    {
		        pdia = new ProgressDialog(cont);
		        pdia.setCancelable(false);
		        act = (Activity)cont;
		    }

			@Override
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia.setMessage("Please wait, fetching the feeds...");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(List<NewsObjects> result){
			   super.onPostExecute(result);
			   pdia.dismiss();
			   ((News)act).handleNewsListView(result, act);
			}

		    @Override
		    protected List<NewsObjects> doInBackground(Object... data) 
		    {
		    	Context cont = (Context) data[0];
		    	String selection = (String)data[1];
		    	TwitterWork obj = (TwitterWork)data[2];
		    	List<NewsObjects> news = new ArrayList<NewsObjects>(100);
		    	String url = "adamschefter";
		    	if(selection.contains("Mortenson"))
		    	{
		    		url = "mortreport";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("LaCanfora"))
		    	{
		    		url = "jasonlacanfora";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Brad Evans"))
		    	{
		    		url = "yahoonoise";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Glazer"))
		    	{
		    		url = "jayglazer";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Clay"))
		    	{
		    		url = "mikeclaynfl";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Douche"))
		    	{
		    		url = "fantasydouche";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Eric Mack"))
		    	{
		    		url = "ericmackfantasy";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Late Round"))
		    	{
		    		url = "lateroundqb";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Wesseling"))
		    	{
		    		url = "chriswesseling";
			    	news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Kay Adams"))
		    	{
		    		url = "heykayadams";
		    		news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Chet"))
		    	{
		    		url = "Chet_G";
		    		news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Sigmund"))
		    	{
		    		url = "SigmundBloom";
		    		news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	else if(selection.contains("Aggregate") && selection.contains("Fantasy"))
		    	{
		    		news = TwitterWork.parseTwitter4jList("chriswesseling", "Fantasy Football Writers", obj);
		    	}
		    	else if(selection.contains("Aggregate") && selection.contains("Beat"))
		    	{
		    		news = TwitterWork.parseTwitter4jList("Chet_G", "Beat Reporters", obj);
		    	}
		    	else if(selection.contains("Schefter"))
		    	{
		    		url = "adamschefter";
		    		news = TwitterWork.parseTwitter4j(url, obj);
		    	}
		    	WriteToFile.writeNewsTwitter(cont, news, selection);
				return news;
		    }
		  }

		/**
		 * Handles the back-end parsing of the twitter feeds
		 * @author Jeff
		 *
		 */
		public class ParseTwitterSearch extends AsyncTask<Object, Void, List<NewsObjects>> 
		{
			ProgressDialog pdia;
			Activity act;
			boolean flag;
			String query;
			TwitterWork tw;
		    public ParseTwitterSearch(Context cont, boolean news, String input, TwitterWork obj) 
		    {
		        pdia = new ProgressDialog(cont);
		        pdia.setCancelable(false);
		        act = (Activity)cont;
		        flag = news;
		        query = input;
		        tw = obj;
		    }

			@Override 
			protected void onPreExecute(){ 
			   super.onPreExecute();
			        pdia.setMessage("Please wait, searching the feeds...");
			        pdia.show();    
			}

			@Override
			protected void onPostExecute(List<NewsObjects> result){
				super.onPostExecute(result);
				pdia.dismiss();
				if(flag)
				{
				    ((News)act).handleNewsListView(result, act);
				}
				else
				{
					PlayerInfo.playerTweetSearch(result, act, query);
				}
			}

		    @Override
		    protected List<NewsObjects> doInBackground(Object... data) 
		    {
		    	Context cont = (Context) data[0];
		    	String selection = (String)data[1];
		    	String header = (String)data[2];
		    	TwitterWork obj = (TwitterWork)data[3];
		    	List<NewsObjects> news = new ArrayList<NewsObjects>(100);
		    	news = TwitterWork.searchTweets(selection, obj.userTwitter);
		    	if(flag)
		    	{
		    		WriteToFile.writeNewsTwitter(cont, news, header);
		    	}
				return news;
		    }
		  }
	}
