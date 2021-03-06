package view;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import spark.data.SO;
import spark.data.SV;
import spark.data.io.SONReader;
import sparkClass.Root;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private JMenuItem openMenuItem;
	private ContentPanel contentPanel;
	
    public MainFrame() {
    	// setup
        super("Layout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
    	setSize(new Dimension(800, 600));

        // place the frame in the middle
    	GraphicsConfiguration gc = getGraphicsConfiguration();  
    	Rectangle bounds = gc.getBounds();  
    	Dimension size = getPreferredSize();
    	setLocation((int) ((bounds.width / 2) - (size.getWidth() / 2)), 
    			    (int) ((bounds.height / 2) - (size.getHeight() / 2)));  
    	// create
        createComponents();
        createMenuBar(); 

        pack();
    }
    
    private void createComponents() {        
        contentPanel = ContentPanel.getInstance();
        this.add(contentPanel);
    }
    
    private void createMenuBar(){
    	JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(actionListener);
        menu.add(openMenuItem);
    }
    
    private ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
	        if (e.getSource() == openMenuItem) {
	        	JFileChooser chooser = new JFileChooser();
	        	FileNameExtensionFilter drawfilter = new FileNameExtensionFilter("DRAW file (.draw)", "draw");
	            chooser.setFileFilter(drawfilter);
	        	int returnVal = chooser.showOpenDialog(MainFrame.this);
	        	File file = chooser.getSelectedFile();
                if (file != null && returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        contentPanel.clean();
                        String[] sparkClasses = new String[]{"sparkClass.widget","sparkClass.shape","sparkClass.layout","sparkClass"};
                        SONReader reader = new SONReader(sparkClasses, new FileInputStream(file));
                        SV sv = reader.read();
                        SO style = sv.getSO();
                        Root root = (Root) style;
                        root.setStyle(style);
                        contentPanel.setRoot(root);
                    } catch (FileNotFoundException e1) {
                        // never gonna happen
                    } catch (spark.data.io.json.JSONException e1){
                    	JOptionPane.showMessageDialog(null,
                    			e1,
                    		    "SON Parsing Error",
                    		    JOptionPane.ERROR_MESSAGE);
                    } catch (ClassCastException e1){
                    	JOptionPane.showMessageDialog(null,
                    		    "There should be a ROOT at the root level.",
                    		    "SON Parsing Error",
                    		    JOptionPane.ERROR_MESSAGE);
                    }
                }
	        }
	    }
    };
}
