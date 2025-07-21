package com.javamastery.inventory.ui;

import com.javamastery.inventory.service.CategoryService;
import com.javamastery.inventory.service.InventoryService;
import com.javamastery.inventory.service.ProductService;
import com.javamastery.inventory.ui.panels.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window with tabbed interface for inventory management
 */
public class MainWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
    
    private final CategoryService categoryService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private ProductManagementPanel productPanel;
    private InventoryPanel inventoryPanel;
    private CategoryPanel categoryPanel;
    
    public MainWindow() {
        this.categoryService = new CategoryService();
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        logger.info("Main window initialized");
    }
    
    private void initializeComponents() {
        setTitle("Inventory Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set application icon (if available)
        try {
            // You can add an icon here if you have one
            // setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not available, continue without it
        }
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        dashboardPanel = new DashboardPanel(inventoryService, productService);
        productPanel = new ProductManagementPanel(productService, categoryService);
        inventoryPanel = new InventoryPanel(inventoryService, productService);
        categoryPanel = new CategoryPanel(categoryService);
        
        // Add tabs
        tabbedPane.addTab("Dashboard", createTabIcon("📊"), dashboardPanel, "Overview of inventory status");
        tabbedPane.addTab("Products", createTabIcon("📦"), productPanel, "Manage products and catalog");
        tabbedPane.addTab("Inventory", createTabIcon("📋"), inventoryPanel, "Manage stock levels");
        tabbedPane.addTab("Categories", createTabIcon("🏷️"), categoryPanel, "Manage product categories");
        
        // Set initial tab
        tabbedPane.setSelectedIndex(0);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Create status bar
        JPanel statusBar = createStatusBar();
        
        // Add components
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Tab change event to refresh data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            logger.debug("Tab changed to index: {}", selectedIndex);
            
            // Refresh the currently selected panel
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof RefreshablePanel) {
                ((RefreshablePanel) selectedComponent).refreshData();
            }
        });
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.setMnemonic('R');
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> refreshAllPanels());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        dashboardItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem productsItem = new JMenuItem("Products");
        productsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        productsItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JMenuItem inventoryItem = new JMenuItem("Inventory");
        inventoryItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        inventoryItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JMenuItem categoriesItem = new JMenuItem("Categories");
        categoriesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 4"));
        categoriesItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        viewMenu.add(dashboardItem);
        viewMenu.add(productsItem);
        viewMenu.add(inventoryItem);
        viewMenu.add(categoriesItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(Box.createHorizontalGlue()); // Push help menu to the right
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(0, 25));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        // Update time every second
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
        });
        timer.start();
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private Icon createTabIcon(String emoji) {
        // Simple text-based icon using emoji
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g.drawString(emoji, x, y + 12);
            }
            
            @Override
            public int getIconWidth() {
                return 20;
            }
            
            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }
    
    private void refreshAllPanels() {
        logger.info("Refreshing all panels");
        
        if (dashboardPanel instanceof RefreshablePanel) {
            ((RefreshablePanel) dashboardPanel).refreshData();
        }
        if (productPanel instanceof RefreshablePanel) {
            ((RefreshablePanel) productPanel).refreshData();
        }
        if (inventoryPanel instanceof RefreshablePanel) {
            ((RefreshablePanel) inventoryPanel).refreshData();
        }
        if (categoryPanel instanceof RefreshablePanel) {
            ((RefreshablePanel) categoryPanel).refreshData();
        }
    }
    
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the Inventory Management System?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            logger.info("Application exit requested by user");
            System.exit(0);
        }
    }
    
    private void showAboutDialog() {
        String aboutText = """
            <html>
            <h2>Inventory Management System</h2>
            <p><strong>Version:</strong> 1.0</p>
            <p><strong>Description:</strong> A comprehensive inventory management system<br>
            demonstrating advanced database integration using JDBC,<br>
            proper layered architecture, and enterprise-level features.</p>
            <br>
            <p><strong>Features:</strong></p>
            <ul>
            <li>Product catalog management</li>
            <li>Real-time inventory tracking</li>
            <li>Stock movement audit trail</li>
            <li>Category management</li>
            <li>Business rule validation</li>
            <li>Advanced reporting</li>
            </ul>
            <br>
            <p><strong>Technology Stack:</strong></p>
            <ul>
            <li>Java 17</li>
            <li>JDBC with HikariCP</li>
            <li>H2/MySQL Database</li>
            <li>Swing GUI</li>
            <li>Maven Build System</li>
            </ul>
            </html>
            """;
        
        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About Inventory Management System",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Interface for panels that can be refreshed
     */
    public interface RefreshablePanel {
        void refreshData();
    }
}