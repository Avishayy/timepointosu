// Shitty author is named Avishay
package osutimingpoints;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class MainWindow extends Frame implements ActionListener, WindowListener {

	final Integer width = 500;
	final Integer height = 550;
	private Button diffPickBtn = new Button("Pick a new difficulty file");
	private Button executeBtn = new Button("Insert timing points into difficulty!");
	private Label addBeat = new Label("Add a timing point every");
	private Label beat = new Label("beat.");
	private Label amount = new Label("Amount of timing points:              ");
	private Label insertOffset = new Label("Insert starting offset:             ");
	private Label vlmStrtLabel = new Label("Insert volume start value:");
	private Label incrLabel = new Label("Insert volume increasing value (0 if none, negative works too):");
	private TextField offsetStart = new TextField("000000");
	private TextField lineAmount = new TextField("1");
	private TextField mapName = new TextField("- Map Name -", 50);
	private TextField diffName = new TextField("-- Difficulty name --", 25);
	private TextField volStart = new TextField("69"); // so funny
	private TextField volChange = new TextField("0");
	private Choice typ = new Choice();
	private Choice timings = new Choice();
	private Choice snappings = new Choice();
	private OsuFile file = null;
	private Checkbox excludeFirst = new Checkbox("Exclude first point");
	
	final JFileChooser fc = new JFileChooser() {
		@Override
		public void approveSelection() {
            if (getSelectedFile().exists())
            {
            	if(getSelectedFile().getName().endsWith(".osu"))
            		super.approveSelection();
            	else
            		JOptionPane.showMessageDialog(this, getSelectedFile().getName() + " is not a difficulty file!",
                            "Wrong file selected", JOptionPane.ERROR_MESSAGE);
            }
            else
                JOptionPane.showMessageDialog(this, getSelectedFile().getName() + " does not exist!",
                        "File is not found", JOptionPane.ERROR_MESSAGE);
		}
	};
	
	public MainWindow() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 45, 30)); //layout that arranges stuff from left to right
		setTitle("Timing points creator"); // frame title
		setSize(width, height);
                
		typ.addItem("Red Offset");
		typ.addItem("Green Offset");
                
                snappings.add("1"); //adding timings to menu
                Integer[] array = {2, 3, 4, 6, 8, 12, 16};
		Arrays.stream(array).forEach(i -> snappings.add("1/" + i));
		snappings.select(1); //default is 1/2
		
		mapName.setEditable(false);
		add(mapName); // adds textfield
		
		diffName.setEditable(false);
		add(diffName); // ye 

		add(diffPickBtn); // adds button

		diffPickBtn.addActionListener(l ->
                { 	int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = new OsuFile(fc.getSelectedFile()
						.getAbsolutePath());
				System.out.println("File named " + file.getName() + " was loaded.");
				mapName.setText(file.getMapName());
				diffName.setText(file.getDifficulty());
				setVisible(true);
				
				fileLoaded();

			} else
				System.out.println("File wasn't loaded.");
                });

		addWindowListener(this);

		setVisible(true); // show the whole 'Frame'
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == diffPickBtn) { // loading button

		}
		
		if (e.getSource() == executeBtn) { // executing button
			Integer ptsAmount = Integer.parseInt(lineAmount.getText());
			Integer startOffset = Integer.parseInt(offsetStart.getText());
			Double bpm = Translator.translateBpm(file.getTimingsNBpm().get(typ.getSelectedIndex()).get(1));
			Double snap = 1 / (double) (Integer.parseInt(snappings.getSelectedItem().substring(2)));
			Integer volStartNum = Integer.parseInt(volStart.getText());
			Integer volChangeNum = Integer.parseInt(volChange.getText());
			
			if (excludeFirst.getState() && typ.getSelectedIndex() == 0)
				file.addTimingPoints(OsuFile.createRedTimingPoints(ptsAmount, (int) (startOffset + 60000/bpm * snap), bpm, snap, volStartNum, volChangeNum));
			else if (!excludeFirst.getState() && typ.getSelectedIndex() == 0)
				file.addTimingPoints(OsuFile.createRedTimingPoints(ptsAmount, startOffset, bpm, snap, volStartNum, volChangeNum));
			else if (excludeFirst.getState() && typ.getSelectedIndex() == 1)
				file.addTimingPoints(OsuFile.createGreenTimingPoints(ptsAmount, (int) (startOffset + 60000/bpm * snap), bpm, snap, volStartNum, volChangeNum));
			else
				file.addTimingPoints(OsuFile.createGreenTimingPoints(ptsAmount, startOffset, bpm, snap, volStartNum, volChangeNum));
			
			JOptionPane.showMessageDialog(this, "Done!", "Succesful insertion!", JOptionPane.OK_OPTION);
		}

	}

	public void fileLoaded() {
		add(typ);
		
		timings.removeAll();
		for(List<Double> lst : file.getTimingsNBpm()) {
			Double bpm = Translator.translateBpm(lst.get(1));
			String time = Translator.offsetToString(lst.get(0).intValue());
			timings.add(bpm + " BPM at " + time);
		}
		add(timings);
		
		add(addBeat);
		add(snappings);
		add(beat);
		
		add(amount);
		add(lineAmount);
		
		KeyListener kl = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					Integer.parseInt(lineAmount.getText());
				} catch (NumberFormatException e) {
					lineAmount.setText("");
				}	
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		};
		lineAmount.addKeyListener(kl);
		
		MouseListener ml = new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				offsetStart.setText(file.getTimingsNBpm().get(timings.getSelectedIndex()).get(0).intValue() + "");					
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
			
		};
		
		add(insertOffset);
		add(offsetStart);
		offsetStart.setText(file.getTimingsNBpm().get(0).get(0).intValue() + "");
		offsetStart.addKeyListener(kl);
		offsetStart.addMouseListener(ml);
		
		add(vlmStrtLabel);
		add(volStart);
		
		add(incrLabel);
		add(volChange);
		
		add(executeBtn);
		
		executeBtn.addActionListener(this);
		
		excludeFirst.setState(true);
		add(excludeFirst);
		
		setVisible(true);
		
		System.out.println();
		
		
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}