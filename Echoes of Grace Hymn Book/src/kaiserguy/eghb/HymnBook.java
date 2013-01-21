package kaiserguy.eghb;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import kaiserguy.eghb.R;

import org.xmlpull.v1.XmlPullParserException;

public class HymnBook {
	
	public static class Hymn {
		public final int number;
		public final String title;
		public final String titleSort;
		public final String author;
		public final String meter;
		public final String ssmeter;
		public final String stanzaIndent;
		public final String chorusIndent;
		//public final int year;
		public final List<Stanza> stanzas;
		
		public Hymn(int number, String title, String titleSort, String meter, String ssmeter, String author, String stanzaIndent, String chorusIndent) {
			this.number = number;
			this.titleSort = titleSort;
			this.title = title;
			this.meter = meter;
			this.ssmeter = ssmeter;
			this.stanzas = new ArrayList<Stanza>();
			this.author = author;
			this.stanzaIndent = stanzaIndent;
			this.chorusIndent = chorusIndent;
			//this.year = 0;
		}

		public String getText() {
			String hymnText = "";
			for (int s = 0; s < stanzas.size() - 1; s++) {
				hymnText += stanzas.get(s).getText();
			}
			return hymnText;
		}

		public String getStanzaText(int stanza) {
			return stanzas.get(stanza).getText();
		}

		public String getHTML(boolean defined) {
			String hymnTableStart = "<table style='color:#DDDDDD; font-family:serif; vertical-align:top;'>";
			String hymnHTML = "";
			Stanza currentStanza;
			String currentIndent;
			for (int s = 0; s < stanzas.size(); s++) {
				hymnHTML += "<tr><td style='vertical-align:top;'>";
				currentStanza = stanzas.get(s);
				currentIndent = stanzaIndent;
				if(chorusIndent.length()!=0){ //check for the presence of a chorus
					if (s!=1){
						if (s>1){
							hymnHTML += Integer.toString(s);
						}else{
							hymnHTML += "1";
						}
					}else{
						currentIndent = chorusIndent;
						hymnHTML += "</td><td></td><tr><td id='refrain' colspan='2'><b>Refrain:</b><br />&nbsp;</td></tr><tr><td>&nbsp;";
					}
				}else{
					hymnHTML += Integer.toString(s+1);
				}
				hymnHTML += "</td><td>";
				if (defined) {
					String stanzaText = "";
					for (int l = 0; l < currentStanza.lines.length; l++) {
						for (int i=0;i < Integer.parseInt(String.valueOf(currentIndent.charAt(l)));i++){
							stanzaText += "&nbsp;";
						}
						stanzaText += currentStanza.lines[l] + "<br />";
					}
					hymnHTML += stanzaText
							.replaceAll(
									"\\b(\\w{3,40})\\b",
									"<a href='http://www.merriam-webster.com/dictionary/$1' style='color:White'>$1</a>");
				} else {
					String firstHalf = "";
					String stanzaText = "";
					
					for (int l = 0; l < currentStanza.lines.length; l++) {
						int intIndentIndex = Integer.parseInt(String.valueOf(currentIndent.charAt(l)));
						for (int i=0;i < intIndentIndex;i++){
							stanzaText += "&nbsp;";
						}
						
						stanzaText += currentStanza.lines[l] + "<br />";	
						
						if (l == (int) (currentStanza.lines.length * 0.5) - 1) {
							firstHalf = stanzaText;
							stanzaText = "";
						}
					}
					hymnHTML += "<span id='" + s + "' onclick=\"smoothScroll('"
							+ (s - 1) + "');\">" + firstHalf
							+ "</span><span onclick=\"smoothScroll('" + (s + 1)
							+ "');\">" + stanzaText + "</a>";
				}

				hymnHTML += "<br /></td></tr>";
			}

			hymnHTML = hymnTableStart + hymnHTML + "</table>";

			return hymnHTML;
		}
	}

	public static class Stanza {
		public final int number;
		public String[] lines = new String[1];
		public String[] references;

		public Stanza(int number) {
			this.number = number;
		}
		
		public String getText() {
			String stanzaText = "";

			stanzaText = stanzaText + Integer.toString(number) + " ";
			for (int l = 0; l < lines.length; l++) {
				stanzaText += lines[l];
			}
			stanzaText += "\n";
			stanzaText = stanzaText
			.replaceAll("\\|\\|/?i\\|\\|","")
			.replace("||sp||"," ");
			return stanzaText;
		}
	}

	private static final HymnBook sInstance = new HymnBook();

	public static HymnBook getInstance() {
		return sInstance;
	}

	public boolean mLoaded = false;
	public boolean refsLoaded = false;
	public int currentHymnNumber = 0;
	public int totalHymns = 0;

	/**
	 * Loads the hymns and their text if they haven't been loaded already.
	 * 
	 * @param resources
	 *            Used to load the file containing the hymns.
	 */
	public synchronized boolean ensureLoaded(final Resources resources) {
		if (mLoaded){
			return true;
		}
		new Thread(new Runnable() {
			public void run() {
				loadHymns(resources);
			}
		}).start();
		return true;
	}

	public String timeText = "";
	/**
	* Reallocates an array with a new size, and copies the contents
	* of the old array to the new array.
	* @param oldArray  the old array, to be reallocated.
	* @param newSize   the new array size.
	* @return          A new array with the same contents.
	*/
	private static Object resizeArray (Object oldArray, int newSize) {
	   int oldSize = java.lang.reflect.Array.getLength(oldArray);
	   Class elementType = oldArray.getClass().getComponentType();
	   Object newArray = java.lang.reflect.Array.newInstance(
	         elementType,newSize);
	   int preserveLength = Math.min(oldSize,newSize);
	   if (preserveLength > 0)
	      System.arraycopy (oldArray,0,newArray,0,preserveLength);
	   return newArray; }
	
	private synchronized void loadHymns(Resources resources) {
		if (mLoaded)
			return;
		String strMeter = "";
		String strSSMeter = "";
		String strTitle = "";
		String strTitleSort = "";
		int intHymnNumber = 1;
		String strStanzaIndent = "";
		String strChorusIndent = "";
		String strAuthor = "";
		//int intYear = 0;

		XmlResourceParser xrpTitles = resources.getXml(R.xml.hymntitles);
		try {
			while (xrpTitles.getName() == null) {
				xrpTitles.next();
			}
			while (xrpTitles.getName().equals("hymn") == false) {
				xrpTitles.next();
			}
			while (xrpTitles.getName().equals("hymn")) {
				intHymnNumber = xrpTitles.getAttributeIntValue(null, "id", 1);
				strTitle = xrpTitles.getAttributeValue(null, "Title");
				strTitleSort = xrpTitles.getAttributeValue(null, "TitleSort");
				strAuthor = xrpTitles.getAttributeValue(null, "Author");
				strMeter = xrpTitles.getAttributeValue(null, "Meter");
				//strSSMeter = xrpTitles.getAttributeValue(null, "ssmeter");
				strStanzaIndent = xrpTitles.getAttributeValue(null, "StanzaIndent");
				strChorusIndent = xrpTitles.getAttributeValue(null, "ChorusIndent");
				//intYear = xrp.getAttributeIntValue(null, "year", 0);
				
				addHymn(intHymnNumber, strTitle, strTitleSort, strMeter, strSSMeter, strAuthor, strStanzaIndent, strChorusIndent);
				currentHymnNumber += 1;
				totalHymns += 1;
				xrpTitles.nextTag(); //read end tag
				xrpTitles.nextTag(); //read start tag
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xrpTitles.close();

		int intStanzaNumber = 0;
		int intLineNumber = 0;
		int intPreviousHymnStanzaNumber = 0;
		int intPreviousHymnNumber = 0;
		Hymn currentHymn;
		Stanza currentStanza;
		XmlResourceParser xrpLines;
		for (int i=0; i<4; i++){
			if (i==0){
				xrpLines = resources.getXml(R.xml.hymnlines_1);
			}else if (i==1){
				xrpLines = resources.getXml(R.xml.hymnlines_2);
			}else if (i==2){
				xrpLines = resources.getXml(R.xml.hymnlines_3);
			}else{
				xrpLines = resources.getXml(R.xml.hymnlines_4);
			}
		try {
		
		while (xrpLines.getName() == null) {
			xrpLines.next();
		}
		while (xrpLines.getName().equals("line") == false) {
			xrpLines.next();
		}
		while (xrpLines.getName().equals("line")) {
			intHymnNumber = xrpLines.getAttributeIntValue(null, "HymnID", 1);
			intStanzaNumber = xrpLines.getAttributeIntValue(null, "StanzaID", 1);
			intLineNumber = xrpLines.getAttributeIntValue(null, "LineNum", 1);
			currentHymn = mHymns.get(intHymnNumber-1);
			if (intHymnNumber - intPreviousHymnNumber > 1){
				intPreviousHymnNumber = intHymnNumber - 1;
				intPreviousHymnStanzaNumber = intStanzaNumber - 1;
			}
			intStanzaNumber = intStanzaNumber - intPreviousHymnStanzaNumber;
			
			switch (intLineNumber){
			case 1:
				currentStanza = new Stanza(intStanzaNumber);
				String temp = xrpLines.getAttributeValue(null, "Text");	
				currentStanza.lines[0] = temp;	
				currentHymn.stanzas.add(currentStanza);
				currentHymnNumber += 1;
				break;
			default:
				currentHymn.stanzas.get(intStanzaNumber-1).lines = (String[])resizeArray(currentHymn.stanzas.get(intStanzaNumber-1).lines,intLineNumber);
				currentHymn.stanzas.get(intStanzaNumber-1).lines[intLineNumber - 1] = xrpLines.getAttributeValue(null, "Text");
			}
			xrpLines.nextTag();
			xrpLines.nextTag();
		}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xrpLines.close();
		}
		mLoaded = true;
	}

	public String[] getHymnNumbersByAuthor(String author) {
		List<String> list = new ArrayList<String>();
		int mHymnsSize = mHymns.size();
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);

			if (thisHymn.author.equals(author)) {
				list.add(thisHymn.number + " " + thisHymn.getStanzaText(0).substring(2, thisHymn.getStanzaText(0).indexOf("\n")));
				continue;
			}
		}
		
		String[] aryList = new String[list.size()];
		int mFirstLinesSize = list.size();
		
		for (int i = 0; i < mFirstLinesSize; i++) {
			aryList[i] = list.get(i);
		}
		//Arrays.sort(aryList);
		return aryList;
	}

	public String strip(String value) {
		return value.replaceAll("[\\W\\d]", "").trim().toLowerCase();
	}

	public String stripPunctuation(String value) {
		return value.replaceAll("\\W", "").trim().toLowerCase();
	}

	public List<Hymn> getMatches(String query, boolean boolSuggest, Resources resources) {
		List<Hymn> list = new ArrayList<Hymn>();
		if (query.length() == 0){
			return list;
		}
		String punctStripped = stripPunctuation(query);
		String stripped = strip(query);
		
		Boolean isHymnOrMeter = false;
		if (isInteger(query)) {
			isHymnOrMeter = true;
		}
		
		if (isHymnOrMeter) {
			/* Search hymn numbers and meters only */
			int mHymnsSize = mHymns.size();
			Hymn thisHymn;
			for (int i = 0; i < mHymnsSize; i++) {
				thisHymn = mHymns.get(i);
				if (Integer.toString(thisHymn.number).startsWith(query)) {
					list.add(thisHymn);
					continue;
				} else if (stripPunctuation(thisHymn.meter).startsWith(
						punctStripped)) {
					list.add(thisHymn);
					continue;
				}
				if (boolSuggest){
					if (list.size() > 5){
						return list;
					}
				}
			}
		}
		else {
			if (punctStripped.length() < 2 && stripped.length() < 2){
				return list;
			}
			/* Search hymn text, author, and meter only */
			int mHymnsSize = mHymns.size();
			Hymn thisHymn;
			for (int i = 0; i < mHymnsSize; i++) {
				thisHymn = mHymns.get(i);
				if (thisHymn.meter.equals(query) || stripPunctuation(thisHymn.meter).equals(punctStripped)) {
					list.add(thisHymn);
					continue;
				} else if (punctStripped.length() > 1 && stripPunctuation(thisHymn.author).contains(
						punctStripped)) {
					list.add(thisHymn);
					continue;
				} else if (stripped.length() > 1) {
					{
						int mStanzasSize = thisHymn.stanzas.size();
						for (int s = 0; s < mStanzasSize; s++) {
							String stanzaText = "";
							for (int l = 0; l < thisHymn.stanzas.get(s).lines.length; l++) {
								stanzaText += thisHymn.stanzas.get(s).lines[l];
							}
							if (strip(stanzaText).contains(stripped)) {
								list.add(thisHymn);
								break;
							}
						}
					}
				}

				if (boolSuggest){
					if (list.size() > 5){
						return list;
					}
				}
			}
		}

		return list;
	}
	public List<Hymn> getHymnsByMeter(String strMeter) {
		List<Hymn> list = new ArrayList<Hymn>();
		/* Search meters only */
		int mHymnsSize = mHymns.size();
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);

			if (thisHymn.meter.equals(strMeter)) {
				list.add(thisHymn);
				continue;
			}
		}		
		return list;
	}
	
	public String[] getFirstLinesByNumber() {
		int mHymnsSize = mHymns.size();
		String[] aryList = new String[mHymnsSize];
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);
			aryList[i] = thisHymn.number + " - " + thisHymn.getStanzaText(0).substring(2,thisHymn.getStanzaText(0).indexOf("\n"));
		}
		return aryList;
	}
	
	public static String trimChars(String str,char ch) {
		return str.replaceAll(ch + "$|^" + ch, "");
	}

	public String[] getFirstLinesByLetter(char chrLetter) {
		List<String> lstFirstLines = new ArrayList<String>();
		int mHymnsSize = mHymns.size();
		
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);
			int intStanzaSize = thisHymn.stanzas.size();
			for (int s=0; s < intStanzaSize; s++){
				Boolean boolFoundLetter = false;
				String stanzaText = thisHymn.getStanzaText(s);
				stanzaText = thisHymn.getStanzaText(s).substring(2,stanzaText.indexOf("\n"));
				stanzaText = trimChars(trimChars(trimChars(stanzaText,'“'),'’'),'”');
				char chrFirstChar = stanzaText.charAt(0);
				if (chrFirstChar == chrLetter){
					lstFirstLines.add(stanzaText + " #" + thisHymn.number);
					boolFoundLetter=true;
				}else if (boolFoundLetter){
						break;
				}
			}
		}
		
		String[] aryList = new String[lstFirstLines.size()];
		int mFirstLinesSize = lstFirstLines.size();
		
		if (mFirstLinesSize<1){
			lstFirstLines.add("No Stanzas #0");
		}
		for (int i = 0; i < mFirstLinesSize; i++) {
			aryList[i] = lstFirstLines.get(i);
		}
		Arrays.sort(aryList);
		return aryList;
	}

	public String[] getMeters() {
		List<String> list = new ArrayList<String>();
		/* get all meters */
		int mHymnsSize = mHymns.size();
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);

			if (thisHymn.meter.length() > 0) {
				if (list.contains(thisHymn.meter)){
					// do nothing
				} else {
					list.add(thisHymn.meter);
				}
			}
		}

		int mMetersSize = list.size();
		String[] aryList = new String[mMetersSize];
		for (int i = 0; i < mMetersSize; i++) {
			aryList[i] = list.get(i);
		}
		Arrays.sort(aryList);
		return aryList;
	}

	public String[] getAuthors() {
		List<String> list = new ArrayList<String>();
		/* get all meters */
		int mHymnsSize = mHymns.size();
		for (int i = 0; i < mHymnsSize; i++) {
			Hymn thisHymn = mHymns.get(i);

			if (thisHymn.author.length() > 0) {
				if (list.contains(thisHymn.author)){
					// do nothing
				} else {
					list.add(thisHymn.author);
				}
			}
		}

		int mAuthorsSize = list.size();
		String[] aryList = new String[mAuthorsSize];
		for (int i = 0; i < mAuthorsSize; i++) {
			aryList[i] = list.get(i);
		}
		Arrays.sort(aryList);
		return aryList;
	}

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private List<Hymn> mHymns = new ArrayList<Hymn>();

	public Hymn getHymn(Integer hymnNumber) {
		if (hymnNumber < 1){
			hymnNumber = 1;
		}else if (hymnNumber > 379){
			hymnNumber = 379;
		}
		return mHymns.get(hymnNumber - 1);
	}

	private void addHymn(int intHymnNumber,String strTitle,String strTitleSort, String strMeter, String strSSMeter, String strAuthor, String strStanzaIndent, String strChorusIndent) {
		mHymns.add(new Hymn(intHymnNumber, strTitle, strTitleSort, strMeter, strSSMeter, strAuthor, strStanzaIndent, strChorusIndent));
	}
}