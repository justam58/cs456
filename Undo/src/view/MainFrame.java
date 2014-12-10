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
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import able.Drawable;
import able.Interactable;
import spark.data.SO;
import spark.data.SV;
import spark.data.io.SONReader;
import sparkClass.Group;
import sparkClass.Root;
import sparkClass.Select;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private JMenuItem openMenuItem;
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private ContentPanel contentPanel;
	private SO style;
	
    public MainFrame() {
    	// setup
        super("Undo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 1000));
    	setSize(new Dimension(1000, 1000));

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
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(actionListener);
        fileMenu.add(openMenuItem);
        
        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK));
        undoMenuItem.addActionListener(actionListener);
        editMenu.add(undoMenuItem);
        
        redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK));
        redoMenuItem.addActionListener(actionListener);
        editMenu.add(redoMenuItem);
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
                        style = sv.getSO();
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
                    	if(!(style instanceof Group) || !(style instanceof Select)){
                        	JOptionPane.showMessageDialog(null,
                        		    "There should be a Root or Group or Select at the root level.",
                        		    "SON Parsing Error",
                        		    JOptionPane.ERROR_MESSAGE);
                        	return;
                    	}
                    	Drawable drawable = (Drawable) style;
                    	drawable.setStyle(style);
                    	Interactable root = (Interactable) style;
                    	contentPanel.setRoot(root);
                    }
                }
	        }
	        if (e.getSource() == undoMenuItem) {
	        	contentPanel.undo();
	        }
	        if (e.getSource() == redoMenuItem) {
	        	contentPanel.redo();
	        }
	    }
    };
}
