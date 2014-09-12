import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private JMenuItem openMenuItem;
	private ContentPanel contentPanel;
	
    public MainFrame() {
    	// setup
        super("Draw Tree");
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
        contentPanel = new ContentPanel();
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
	        	FileNameExtensionFilter drawfilter = new FileNameExtensionFilter("files in SON format", "draw");
	            chooser.setFileFilter(drawfilter);
	        	chooser.showOpenDialog(MainFrame.this);
	        	// TODO only shows .draw files. 
	        	// A .draw file is in SON format and contains only objects that implement Drawable (see below). 
	        	// You can read such files using the spark.io.SONReader class.
	        }
	    }
    };
}
