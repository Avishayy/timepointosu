package osutimingpoints;

public class Translator {
	
	public static String offsetToString(Integer offset) { //offset to normal
		Integer milliseconds = offset % 1000; 
		Integer minutes = offset / 60000;
		Integer seconds = (offset - minutes*60000) / 1000;
		
		String sMin = minutes < 10 ? "0" + minutes : minutes + "";
		String sSec = seconds < 10 ? "0" + seconds : seconds + "";
		String sMsc = milliseconds < 100 ? (milliseconds < 10 ? "00" + milliseconds : "0" + milliseconds) : milliseconds + "";
		
		return sMin + ":" + sSec + ":" + sMsc;
	}
	
	public static double translateBpm(Double bpm) { //translate peppy's weird bpm format to regular number
		bpm = 100 * (600 / bpm); //normalBpm = 100 * 600 / peppyBpm ---> peppyBpm = 100 * 600 / normalBpm
		if (bpm - (bpm).intValue() > 0.99999) //WHY DID PEPPY ROUND THE LAST 2 NUMBERS IN HIS BPM FORMAT, IT RUINS EVERYTHING
			return bpm.intValue() + 1;
		else if (bpm - bpm.intValue() < 0.00001)
			return bpm.intValue();
		else
			return Math.round(bpm*100.0)/100.0; //Won't be displayed correctly if the bpm has more than 3 numbers after decimal point.
	}
	
}