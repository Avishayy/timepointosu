package osutimingpoints.timepointosu;

import osutimingpoints.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class OsuFile extends File {
	
	public OsuFile(String path) {
			super(path);
	}

	public List<String> scanAndCut(List<String> schFor) {
		List<String> found = new ArrayList<>(schFor);
		
		for (Integer i = 0; i < found.size(); i++)
			found.set(i, "");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(this));
		    String line;
		    while ((line = br.readLine()) != null && found.contains("")) {
		    	for (Integer i = 0; i < schFor.size(); i++) {
		    		if (line.startsWith(schFor.get(i))) // if the line starts with what we were searching for
		    			found.set(i, line.substring(schFor.get(i).length())); // then in the same index in 'found' it will set the value of the line, except for the start
		    	}
		    }
		    br.close();
		} catch (Exception e) {
			e.getMessage();
		}
		return found;
	}
	public String getMapName() {
		List<String> needed = new ArrayList<>();
		needed.add("Title:");
		needed.add("Artist:");
		List<String> found = scanAndCut(needed);
		return found.get(1) + " - " + found.get(0);
	}
	
	public String getDifficulty() {
		List<String> needed = new ArrayList<>();
		needed.add("Version:");
		List<String> found = scanAndCut(needed);
		return found.get(0);
	}
	
	public List<List<Double>> getTimingsNBpm() {
		List<List<Double>> timings = new ArrayList<>();
		
		List<String> points = new ArrayList<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(this));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.equals("[TimingPoints]")) {
					while (!(line = br.readLine()).isEmpty()) {
						points.add(line);
					}
					break;
				}
			}
			br.close();
		} catch (Exception e) {
			e.getMessage();
		}
		
		for (String str : points) {
			String newStr = str.substring(str.indexOf(',')+1);
			if(!newStr.startsWith("-")) {
				List<Double> timing = new ArrayList<>();
				timing.add(Double.parseDouble(str.substring(0, str.indexOf(','))));
				timing.add(Double.parseDouble(newStr.substring(0, newStr.indexOf(','))));
				timings.add(timing);
			}
		}
		return timings;
	}
	
	public static List<String> createRedTimingPoints(Integer amount, Integer startOffset, Double bpm, Double snap, Integer volStart, Integer volChange) {
		List<String> pointsAsText = new ArrayList<>();
		
		for(Integer i = 0; i < amount; i++) {
			String point = "";
			point += ((int) (startOffset + (i * 60000/bpm * snap))) + ","; // offset
			point += 100 * (600 / bpm) + ","; // bpm in peppy format
			point += "4,"; // no idea what's the purpose of it
			point += "1,"; // hitsound set type, 1 = normal, 2 = soft, 3 = drum
			point += "0,"; // if it is a custom one, then the number of it, 0 = not a custom hitsound set
			point += (volStart + volChange*i > 100 ? 100 : volStart + volChange*i) + ","; //volume //TODO vol smaller than 0
			point += "1,"; // not an inherited timing point
			point += "0"; // kiai time
			pointsAsText.add(point);
		}
		
		return pointsAsText;
	}
	
	public static List<String> createGreenTimingPoints(Integer amount, Integer startOffset, Double bpm, Double snap, Integer volStart, Integer volChange) { //TODO sv change
		List<String> pointsAsText = new ArrayList<>();
		for(Integer i = 0; i < amount; i++) {
			String point = "";
			point += ((int) (startOffset + (i * 60000/bpm * snap))) + ","; // offset
			point += "-100,"; //-100 is standard 1.0x
			point += "4,"; // no idea what's the purpose of it
			point += "1,"; // hitsound set type, 1 = normal, 2 = soft, 3 = drum
			point += "0,"; // if it is a custom one, then the number of it, 0 = not a custom hitsound set
			point += (volStart + volChange*i > 100 ? 100 : volStart + volChange*i) + ","; //volume //TODO vol smaller than 0
			point += "0,"; // not an inherited timing point
			point += "0"; // kiai time
			pointsAsText.add(point);
		}
		return pointsAsText;
	}
	public void addTimingPoints(List<String> points) {
		List<String> fileContent = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(this));
			String line = "";
			while ((line = br.readLine()) != null) {
				fileContent.add(line);
				if (line.equals("[TimingPoints]")) {
                                    points.stream().forEach((ab) ->  fileContent.add(ab));
				}
			}
			br.close();
			
	        BufferedWriter erasor = new BufferedWriter(new FileWriter(this));
	        for (String str : fileContent) {
	        	erasor.write(str);
	        	erasor.newLine();
	        }
	        erasor.close();
		} catch (Exception e) {
			e.getMessage();
		}
		
	}
}