import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.table.DefaultTableModel;

public class RestaurantPOS implements ActionListener, Printable {
    private JFrame frame;
    private JPanel panel;
    private JButton[] buttons;
    private JTextArea textArea;
    private JButton printButton;
    private JTable menuTable;
    private JLabel totalPriceLabel;
    private double totalPrice;

    private Object[][] menuData = {
        {"1. Cheeseburger", "$8.99"},
        {"2. Chicken Caesar Salad", "$10.99"},
        {"3. Margherita Pizza", "$12.99"},
        {"4. Grilled Salmon", "$16.99"},
        {"5. Ribeye Steak", "$19.99"},
        {"6. Spaghetti Bolognese", "$13.99"},
        {"7. Fish and Chips", "$11.99"},
        {"8. Chicken Alfredo", "$14.99"},
        {"9. Classic Hamburger", "$7.99"},
        {"10. Caesar Salad", "$8.99"},
        // Add more menu items here...
    };

    public RestaurantPOS() {
        frame = new JFrame("Restaurant POS");
        panel = new JPanel(new BorderLayout());
        buttons = new JButton[16];
        textArea = new JTextArea();
        printButton = new JButton("Print");
        totalPriceLabel = new JLabel("Total: $" + totalPrice);

        // Create the buttons and add them to the panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("Table " + (i+1));
            buttons[i].addActionListener(this);
            buttons[i].setBackground(Color.RED); // Set the initial color to red
            buttonPanel.add(buttons[i]);
        }
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Create the menu table
        String[] menuColumns = {"Item", "Price"};
        menuTable = new JTable(menuData, menuColumns);
        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        menuScrollPane.setPreferredSize(new Dimension(250, 0));
        panel.add(menuScrollPane, BorderLayout.WEST);

        // Add the text area and print button to the panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(textArea, BorderLayout.CENTER);
        rightPanel.add(printButton, BorderLayout.SOUTH);
        rightPanel.add(totalPriceLabel, BorderLayout.NORTH);
        panel.add(rightPanel, BorderLayout.EAST);

        // Set the frame properties and add the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

        // Add the print button listener
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(RestaurantPOS.this);
                if (job.printDialog()) {
                    try {
                        job.print();
                    } catch (PrinterException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

public void actionPerformed(ActionEvent e) {
    // Determine which button was clicked
    JButton button = (JButton)e.getSource();
    String tableNumber = button.getText();

    // Get the number of guests for the table
    int numGuests = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of guests for Table " + tableNumber));

    // Create a new table model to store the items ordered for this table
    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.addColumn("Item");
    tableModel.addColumn("Price");
    JTable orderTable = new JTable(tableModel);
    
    // Create a panel to display the order table
    JPanel orderPanel = new JPanel(new BorderLayout());
    orderPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

    // Create a panel to display the total amount
    JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
   JLabel totalLabel = new JLabel(" ");
   totalPanel.add(totalLabel);

    // Create a split pane to display the order panel and total panel side by side
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderPanel, totalPanel);
    splitPane.setResizeWeight(0.8);

    // Show a dialog with the split pane to get the items ordered for this table
    String itemName;
    double totalAmount = 0.0;
    do {
        itemName = JOptionPane.showInputDialog(frame, splitPane, "Enter an item number ordered for Table " + tableNumber + "\n(Enter 'End' to finish)");
        if (!itemName.equalsIgnoreCase("End")) {
            // Search the menu data
            int itemIndex = Integer.parseInt(itemName) - 1;
            if (itemIndex >= 0 && itemIndex < menuData.length) {
                String item = (String)menuData[itemIndex][0];
                double price = Double.parseDouble(menuData[itemIndex][1].toString().substring(1));
                tableModel.addRow(new Object[]{item, String.format("$%.2f", price)});
                totalAmount += price;
                totalLabel.setText("Total: " + String.format("$%.2f", totalAmount));
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid item number. Please try again.");
            }
        }
    } while (!itemName.equalsIgnoreCase("End"));

    // Add the order information to the text area
    String orderInfo = "Table " + tableNumber + " - Guests: " + numGuests + ", Items Ordered:\n";
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        orderInfo += tableModel.getValueAt(i, 0) + " " + tableModel.getValueAt(i, 1) + "\n";
    }
    orderInfo += "Total: " + String.format("$%.2f", totalAmount) + "\n\n";
    textArea.append(orderInfo);

    // Change the color of the button to green
    button.setBackground(Color.GREEN);
}
    
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        textArea.printAll(g);
        return PAGE_EXISTS;
    }
    
    public static void main(String[] args) {
        new RestaurantPOS();
    }
}

