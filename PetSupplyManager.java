/*************************************************************************************
 * PetSupplyManager
 * -----------------------------------------------------------------------------------
 * This program will bring up two windows: a Register and a Supply Summary window. The
 * Register window is similar to a cash register where it can purchase a product given
 * the name and the amount to buy. It will access a list of products and check if the
 * given name is in the list. If it is found, it will update the list's info on the
 * amount of that product which is in stock, has been purchased, the amount of money
 * the product has made, and it will update the register's daily earned money. The
 * Supply Summary window allows the user to overview the list of products, along with
 * a shorter list for shelf restock purposes which only displays the name, amount on
 * shelf and total money made on each product. The Supply Summary window can increment
 * the day, which resets the daily variables and adds to the total money made. It also
 * has an update button which updates the table with the newest data stored in the
 * item list. 
 * This program can be used to manage supplies on shelfs, overview how effective
 * certain products are, and register items for a pet supply retailer, although with
 * some changes it can assist other retailers.
 * -----------------------------------------------------------------------------------
 * INPUT
 *  Register
 *   prodFld    : Text field to handle inputting a product name
 *   amtSldr    : Slider which is used to select an amount of the product to buy
 *   jbtnPch    : A button to purchase the product
 *   jbtnSupSum : A button to switch to the supply summary window
 *  Supply Summary
 *   lowAmtSldr : A slider to select the low amount to check for
 *   jbtnNxtDy  : A button to increment the day count and reset daily values
 *   jbtnUpd    : A button to update the JTables to the most recent values in the list
 *   jbtnReg    : A button to switch to the register window
 * OUTPUT
 *  Register
 *   purchLab : A label which displays if the purchase was successful/why it failed
 *   amtLab   : A label which displays the current amount selected on slider
 *   dlyMoneyLab : A label which displays the money earned on the current day
 *  Supply Summary
 *   refPriTbl   : A table which displays a list of products that have refill priority
 *   prodListTbl : A table which displays the list of products with all info.
 *   lowAmtLab   : A label which displays the current amount selected on slider
 *   dayLab      : A label which displays the current day
 *   ttlMoneyLab : A label which displays the total amount of money made
 ************************************************************************************/

package petsupplymanager;
import java.awt.*;          // For flow layout in swing
import java.awt.event.*;    // Necessary for ActionListener
import javax.swing.*;       // Swing GUI
import javax.swing.event.*; // Necessary for ChangeListener


/**
 *
 * @author Drake
 */

// Interface to hold search method prototypes
interface SearchInt {
    // This will always return a boolean and search for a string
    public int LinearSearch(String n); 
}

// A class which describes an object holding stats on an individual item
class ProdList {
    // Protected because they need to be dirrectly modified by subclasses,
    // static because they need to stay the same across separate objects
    // The name of the item, important for searching
    protected static String name[] = { "Blue Buffalo Wilderness Natural Adult "
                             + "High Protein Grain Free Chicken Dry Dog Food", 
                             "CANIDAE Beef & Oatmeal Dry Dog Food",
                             "Merrick Full Source Raw-Coated Kibble Real Salmon"
                             + " & Whitefish with Healthy Grains Dry Dog Food", 
                             "Instinct Raw Boost Whole Grain Real Chicken & "
                             + "Brown Rice Recipe Dry Dog Food with Freeze-"
                             + "Dried Raw Pieces", 
                             "Hill's Science Diet Adult Light Large Breed with "
                             + "Chicken Meal & Barley Dry Dog Food"};
    // The price of the item
    protected static float price[] = { 50.78f, 37.49f, 74.99f, 51.99f, 37.99f }; 
    // Amount on shelfs
    protected static int amtOnShelf[] = { 15, 9, 18, 5, 12 }; 
    // Number sold in the current day
    protected static int numSoldDay[] = { 0, 0, 0, 0, 0 }; 
    // Total sold over all the days
    protected static int numSoldTot[] = { 0, 0, 0, 0, 0 }; 
    // Method to return name value at a given index
    public static String getName(int i) { return name[i]; }
    // Method to return price value at a given index
    public static float getPrice(int i) { return price[i]; }
    // Method to return amount on shelf at a given index
    public static int getAmtOnShelf(int i) { return amtOnShelf[i]; }
    // Method to return number of product sold on current day at a given index
    public static int getNumSoldDay(int i) { return numSoldDay[i]; }
    // Method to return total number of product sold at a given index
    public static int getNumSoldTot(int i) { return numSoldTot[i]; }
    // Method to return list size
    public static int getListSize() { return name.length; }
}

class SupplySummary extends ProdList {
    private int lowAmt; // The low amount to check for with amount on shelf
    // The total money earned since the list began recording each day
    private float ttlMoney;
    private int day;    // The current day
    // Constructor
    SupplySummary(int lAmt, float m, int d) {
        lowAmt = lAmt;
        ttlMoney = m;
        day = d;
    }
    // Switch to next day
    public void SwitchDay() {
        day++; // Increment day
        ttlMoney += Register.getDlyMoney(); // Add daily money to total
        Register.resetDlyMoney(); // Reset the daily money amount
        // Set all daily values stored in ProdList to 0
        for (int i = 0; i < ProdList.getListSize(); i++) {
            numSoldDay[i] = 0;
        }
    }
    // Retrieve day
    public int getDay() { return day; }
    // Retrieve total money
    public float getTtlMoney() { return ttlMoney; }
}

class Register extends ProdList implements SearchInt{
    private static float dlyMoney; // Money earned on current day
    // Constructor for register, will initialize daily money
    Register(float m) { dlyMoney = m; }
    // This method will return daily money earned
    public static float getDlyMoney() { return dlyMoney; }
    // This method will reset daily money earned
    public static void resetDlyMoney() { dlyMoney = 0; }
    // This method will search through the array of names and return the index
    // if the name is found.
    @Override
    public int LinearSearch(String n) { 
        int s = name.length;       // Size of list
        for(int i = 0; i < s; i++) // Check through entire list for the name
        { 
            if(name[i].equals(n))  // Compare stored item names with given item name
                return i; 
        } 
        return -1; 
    } 
    // This method will recieve an item name and amount, then attempt to add the
    // purchase to the item list. It will return if it was successful.
    public String Purchase(String n, int c) {
        int indx = LinearSearch(n);
        if (indx < 0) // Purchase will fail if name was not found
            return "Purchase Failed: Invalid Name";
        // If amount attempting to purchase is above the available amount
        else if (c > amtOnShelf[indx])
            return "Purchase Failed: Amount Too High";
        else { // If nothing fails
            amtOnShelf[indx] -= c; // Subtract amount purchased from shelf count
            numSoldDay[indx] += c; // Add amount purchased to total sold per day
            numSoldTot[indx] += c; // Add amount purchased to total sold
            dlyMoney += c * price[indx];  // Add amount earned from purchase
            return "Purchase Successful"; // return successful purchase message
        }
    }
}

// The main class
public class PetSupplyManager implements ActionListener, ChangeListener {
    Register registerObj = new Register(0f);            // Object for Register
    // Object for Supply Summary
    SupplySummary supplySumObj = new SupplySummary(10, 0f, 1); 
    
    JFrame sumFrm;     // Declare summary window frame
    JFrame regFrm;     // Declare register window frame
    
    // Table to display the refill priority list
    JTable refPriTbl; 
    // Table to display all the products stored, including how much each made
    JTable prodListTbl; 
    
    JPanel regPnlName; // Declare a panel to group together product name
    JPanel regPnlAmt;  // Declare a panel to group together amount purchased
    JPanel sumPnl;     // Declare a panel to group together summary components
    
    JTextField prodFld;   // holds the product to be purchased
    JSlider amtSldr;      // determines the amount to purchase
    JSlider lowAmtSldr;      // determines the amount to purchase
    // display prompts/info
    JLabel purchLab, dlyMoneyLab, dayLab, prodLab, amtLab, lowAmtLab, ttlMoneyLab; 

    PetSupplyManager() {
        // Create a new JFrame container for the supply summary window
        sumFrm = new JFrame("Supply Summary Window");
        // Create a new JFrame container for the register window
        regFrm = new JFrame("Register Window");
        // Specify FlowLayout for the layout manager.
        sumFrm.setLayout(new FlowLayout());
        regFrm.setLayout(new FlowLayout());
        // Give the frames an initial size.
        sumFrm.setSize(1100, 600);
        regFrm.setSize(600, 250);
        // Terminate the program when the user closes the application.
        sumFrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regFrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        regPnlName = new JPanel(); // Create the register panel for product name
        regPnlAmt = new JPanel(); // Create the register panel for amount
        sumPnl = new JPanel(); // Create the summary panel
        // Create the text field for the product name to purchase
        prodFld = new JTextField(40);
        // Create a slider for the amount of a product to purchase
        amtSldr = new JSlider(1, 50);
        // Create a slider for the low amount to check for.
        lowAmtSldr = new JSlider(0, 20);
        // Add tick marks to the amount slider 
        amtSldr.setPaintTrack(true); 
        amtSldr.setPaintTicks(true); 
        amtSldr.setPaintLabels(true); 
        // Set tick spacing for amount slider
        amtSldr.setMajorTickSpacing(10); 
        amtSldr.setMinorTickSpacing(1); 
        // Add tick marks to the low= amount slider 
        lowAmtSldr.setPaintTrack(true); 
        lowAmtSldr.setPaintTicks(true); 
        lowAmtSldr.setPaintLabels(true); 
        // Set tick spacing for low amount slider
        lowAmtSldr.setMajorTickSpacing(5); 
        lowAmtSldr.setMinorTickSpacing(1); 
        // setChangeListener 
        amtSldr.addChangeListener(this); 
        lowAmtSldr.addChangeListener(this);
        
        // Set up tables
        // Declare data arrays for refill priority JTable
        String refPriData[][] = new String[ProdList.getListSize()][3];
        String refPriCNames[] = { "Name", "Amount on Shelf" , "Total Earned"}; 
        String prodListData[][] = new String[ProdList.getListSize()][6];
        String prodListCNames[] = { "Name", "Price" , "Amount", "# Sold Today", 
                                    "# Sold Total", "Total Earned"}; 
        // Initialize data for product list
        for(int i = 0; i < ProdList.getListSize(); i++) {
            // Store product name
            prodListData[i][0] = ProdList.getName(i);
            // Store product price
            prodListData[i][1] = String.format("%.2f", ProdList.getPrice(i));
            // Store product amount on shelf
            prodListData[i][2] = Integer.toString(ProdList.getAmtOnShelf(i));
            // Store number sold today
            prodListData[i][3] = Integer.toString(ProdList.getNumSoldDay(i));
            // Store number sold in total
            prodListData[i][4] = Integer.toString(ProdList.getNumSoldTot(i));
            // Store the total money the product has made, convert to string
            prodListData[i][5] = ("$" + String.format("%.2f", 
                         ((ProdList.getPrice(i) * ProdList.getNumSoldTot(i))))); 
        }
        // Initialize data for refill priority data
        int r = 0; // To increment rows
        for(int i = 0; i < ProdList.getListSize(); i++) {
            if (lowAmtSldr.getValue() >= ProdList.getAmtOnShelf(i)) {
                // Store product name
                refPriData[r][0] = ProdList.getName(i);
                // Store product amount on shelf
                refPriData[r][1] = Integer.toString(ProdList.getAmtOnShelf(i));
                // Store the total money the product has made, convert to string
                refPriData[r][2] = ("$" + String.format("%.2f", 
                         ((ProdList.getPrice(i) * ProdList.getNumSoldTot(i))))); 
                r++; // Increment row
            }
        }
        
        // Initialize the JTables
        prodListTbl = new JTable(prodListData, prodListCNames); 
        refPriTbl = new JTable(refPriData, refPriCNames); 
        // Set table bounds
        prodListTbl.setBounds(60, 80, 200, 300); 
        refPriTbl.setBounds(60, 80, 200, 300); 
        // Allow the user to sort the rows by clicking on them
        prodListTbl.setAutoCreateRowSorter(true);
        refPriTbl.setAutoCreateRowSorter(true);
        // Add Tables to scroll pane
        JScrollPane sp1 = new JScrollPane(refPriTbl); 
        JScrollPane sp2 = new JScrollPane(prodListTbl); 

        // Create the button to switch to Register view
        JButton jbtnReg = new JButton("Register");
        // Create the button to switch to Supply Summary view
		JButton jbtnSupSum = new JButton("Supply Summary");
        // Create the button to purchase a product
        JButton jbtnPch = new JButton("Purchase");
        // Create the button to update list
        JButton jbtnUpd = new JButton("Update");
        // Create the button to switch to next day
        JButton jbtnNxtDy = new JButton("Next Day");

        // Add action listener for the buttons
        jbtnReg.addActionListener(this);
        jbtnSupSum.addActionListener(this);
        jbtnPch.addActionListener(this);
        jbtnUpd.addActionListener(this);
        jbtnNxtDy.addActionListener(this);

        // Create the labels.
        purchLab = new JLabel("This will state purchase success/failure");
        dlyMoneyLab = new JLabel("     Amount earned today: $" + 
                              String.format("%.2f", registerObj.getDlyMoney()));
        dayLab = new JLabel("Day : " + Integer.toString(supplySumObj.getDay()));
        prodLab = new JLabel("Product Name: ");
        amtLab = new JLabel("Amount to buy: " + amtSldr.getValue());
        lowAmtLab = new JLabel("Alert for refill at: " + lowAmtSldr.getValue());
        ttlMoneyLab = new JLabel("Total $ Earned: " + 
                             String.format("%.2f", supplySumObj.getTtlMoney()));
        // Register components
        // Add components to JPanel
        regPnlName.add(prodLab);
        regPnlName.add(prodFld);
        regPnlAmt.add(amtLab);
        regPnlAmt.add(amtSldr);

        // Add the components to the content pane.
        regFrm.add(purchLab);
        regFrm.add(dlyMoneyLab);
        regFrm.add(regPnlName);
        regFrm.add(regPnlAmt);
        regFrm.add(jbtnPch);
        regFrm.add(jbtnSupSum);
        // Summary components
        // Add components to JPanel
        sumPnl.add(lowAmtLab);
        sumPnl.add(lowAmtSldr);
        sumPnl.add(dayLab);
        sumPnl.add(jbtnNxtDy);
        sumPnl.add(jbtnUpd);
        sumPnl.add(jbtnReg);
        // Add the components to the content pane.
        sumFrm.add(sp1);
        sumFrm.add(sp2);
        sumFrm.add(sumPnl);
        sumFrm.add(ttlMoneyLab);

        // Display the initial frames.
        regFrm.setVisible(true);
        sumFrm.setVisible(false);
    }
    // if JSlider value is changed 
    @Override
    public void stateChanged(ChangeEvent e) 
    { 
        amtLab.setText("Amount to buy: " + amtSldr.getValue()); 
        lowAmtLab.setText("Alert for refill at: " + lowAmtSldr.getValue());
    } 
    // Manage button interactions
    @Override
    public void actionPerformed(ActionEvent ae) {
        int r = 0; // Create variable to increment rows
        switch(ae.getActionCommand()) {
            case "Register" :
                // Switch to the Register view
                sumFrm.setVisible(false);
                regFrm.setVisible(true);
                break; 
            case "Supply Summary" :
                // Switch to the Supply Summary view
                sumFrm.setVisible(true);
                regFrm.setVisible(false);           
                break;
            case "Purchase" :
                /* If "Purchase" is clicked, call the Purchase method using the
                   product name inputted and the value on the slider, and store
                   what it returns in the purchase status label */
                purchLab.setText(registerObj.Purchase(prodFld.getText(), 
                                                      amtSldr.getValue()));
                dlyMoneyLab.setText("     Amount earned today: $" + 
                              String.format("%.2f", registerObj.getDlyMoney()));
                break;
            case "Update" : 
                // If "Update" is clicked, first reset all values to 0 in
                // refill priority table
                for(r = 0; r < ProdList.getListSize(); r++) {
                    for(int c = 0; c < 3; c++) {
                        refPriTbl.setValueAt("", r, c); // Set blank value
                    }
                }
                // Fill in the table with products which are understocked
                r = 0; // Reset row count
                for(int i = 0; i < ProdList.getListSize(); i++) {
                    if (lowAmtSldr.getValue() >= ProdList.getAmtOnShelf(i)) {
                        // Store product name
                        refPriTbl.setValueAt(ProdList.getName(i), r, 0);
                        // Store product amount on shelf
                        refPriTbl.setValueAt(Integer.toString(ProdList.getAmtOnShelf(i)), r, 1);
                        // Store the total money the product has made, convert to string
                        refPriTbl.setValueAt("$" + String.format("%.2f", 
                         ((ProdList.getPrice(i) * ProdList.getNumSoldTot(i)))), r, 2);
                        r++; // Increment row
                    }
                }
                // Also update the second table by checking if the total sold changed
                for(int i = 0; i < ProdList.getListSize(); i++) {
                    if(prodListTbl.getValueAt(i, 4) != Integer.toString(ProdList.getNumSoldTot(i))) {
                        prodListTbl.setValueAt(Integer.toString(ProdList.getAmtOnShelf(i)), i, 2);
                        prodListTbl.setValueAt(Integer.toString(ProdList.getNumSoldDay(i)), i, 3);
                        prodListTbl.setValueAt(Integer.toString(ProdList.getNumSoldTot(i)), i, 4);
                        prodListTbl.setValueAt("$" + String.format("%.2f", 
                         ((ProdList.getPrice(i) * ProdList.getNumSoldTot(i)))), i, 5);                        
                    }
                }
                break;
            case "Next Day" :
                // Switch daily items to 0, and update them on the list
                supplySumObj.SwitchDay();
                dayLab.setText("Day : " + Integer.toString(supplySumObj.getDay()));
                for (int i = 0; i < ProdList.getListSize(); i++) {
                    prodListTbl.setValueAt("0", i, 3);
                }
                // Update labels
                dlyMoneyLab.setText("     Amount earned today: $" + 
                            String.format("%.2f", registerObj.getDlyMoney()));
                ttlMoneyLab.setText("Total $ Earned: " + 
                             String.format("%.2f", supplySumObj.getTtlMoney()));
                break;
        }
    }
    public static void main(String args[]) {
        // Create the frame on the event dispatching thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PetSupplyManager();
            }
        });
    }
}
