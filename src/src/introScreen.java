package src;

import Networking.NetworkBase;
import Networking.PeerConnectionListener;
import Networking.ReceiveObjectListener;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.LayerUI;
import javax.swing.plaf.TextUI;

import static src.constants.*;

// some of source taken from docs.oracle.com/javase/
// gradient idea copied
public class introScreen {
    static JTextField ipTextField1,ipTextField2,ipTextField3;
    static JLabel ipLabel1,ipLabel2,ipLabel3,statusLabel;
    static MouseAdapter onConnectListener;

    static ReceiveObjectListener receiveObjectListener = new ReceiveObjectListener() {
        @Override
        public void onReceive(Object obj) {
            System.out.println("Received object " + obj);
            //TODO
        }
    };
    static NetworkBase network = new NetworkBase(8080,receiveObjectListener);

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }

    public static void createUI() {
        JFrame f = new JFrame ("Ping Pong");

        LayerUI<JPanel> layerUI = new SpotlightLayerUI();
        JPanel panel = createPanel();
        JLayer<JPanel> jlayer = new JLayer<JPanel>(panel, layerUI);

        f.add (jlayer);

//        f.setSize(WINDOW_XSIZE,WINDOW_YSIZE);
        f.pack();
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo (null);
        f.setVisible (true);
    }

    private static JPanel createPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        southPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        ButtonGroup entreeGroup = new ButtonGroup();
        JRadioButton radioButton;
        northPanel.add(radioButton = new JRadioButton("Easy"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Hard"));
        entreeGroup.add(radioButton);
        northPanel.add(radioButton = new JRadioButton("Insane",true));
        entreeGroup.add(radioButton);

//        JPanel centerPanel = new AnimatedJPanel("fireball");



        GridLayout connectButtonLayout = new GridLayout(2,0);
        connectButtonLayout.setVgap(10);
        JButton connectButton = new JButton("Connect");
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        //orderButton.setLocation(WINDOW_XSIZE/2,WINDOW_YSIZE/2);
        //orderButton.setBounds(WINDOW_XSIZE/2,WINDOW_YSIZE/2,50,50);


        JLabel myIPAddressText = new JLabel("Your IP Address : ");
        myIPAddressText.setHorizontalAlignment(SwingConstants.CENTER);
        myIPAddressText.setText(myIPAddressText.getText() + NetworkBase.getIPAddress());
        onConnectListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                String[] args = {getSelectedButtonText(entreeGroup)};
//                //centerPanel.pausePanel();
//                PingPong.main(args);

                //super.mouseClicked(e);

                statusLabel.setText("Connecting...");


                Map<Integer,String> filledIps = getListedIpAddresses();
                System.out.println(filledIps.toString());



//                if(ipTextField1.isVisible()) {
//                    hideTextField(0);
//                    statusLabel.setText("Connecting...");
//                }else {
//                    showTextField(0);
//                    statusLabel.setText("");
//                }
                disableButton(connectButton);
                connectToPeerList(filledIps,statusLabel,connectButton);
            }
        };
        connectButton.addMouseListener(onConnectListener);

        JPanel textFieldPanel = new JPanel();
        GridLayout textFieldLayout = new GridLayout(3,1);
        GridLayout centerLayout = new GridLayout(2,1);
        ipTextField1 = new JTextField(10);
        ipTextField2 = new JTextField(10);
        ipTextField3 = new JTextField(10);
        ipLabel1 = new JLabel();
        ipLabel2 = new JLabel();
        ipLabel3 = new JLabel();

        textFieldPanel.add(getTextFieldLayout("Player 2 IP Address :",ipTextField1,ipLabel1),BorderLayout.NORTH);
        textFieldPanel.add(getTextFieldLayout("Player 3 IP Address :",ipTextField2,ipLabel2),BorderLayout.CENTER);
        textFieldPanel.add(getTextFieldLayout("Player 4 IP Address :",ipTextField3,ipLabel3),BorderLayout.SOUTH);

        centerPanel.add(myIPAddressText,BorderLayout.NORTH);
        centerPanel.add(textFieldPanel,BorderLayout.CENTER);
        centerPanel.setSize(10,10);
        textFieldPanel.setLayout(textFieldLayout);
        centerPanel.setLayout(centerLayout);


        southPanel.add(statusLabel,BorderLayout.NORTH);
        southPanel.add(connectButton,BorderLayout.SOUTH);
        southPanel.setLayout(connectButtonLayout);
        mainPanel.add(northPanel,BorderLayout.NORTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(southPanel,BorderLayout.SOUTH);
        //System.out.println(String.format("ht %d, wd%d",mainPanel.getHeight(),mainPanel.getWidth()));
        return mainPanel;
    }


    private static class PeerConnectionStore {
        interface AllConnectionsResListener {
            void onAllConnectionsRes(List<Integer> failedList);
        }

        AllConnectionsResListener listener;
        int numPeers;
        int limit;
        List<Integer> failedToConnectPeers;
        void incrementNumPeers() {
            numPeers++;
            if (numPeers == limit) {
                listener.onAllConnectionsRes(failedToConnectPeers);
            }
        }

        public void setConnected (Integer i) {
            incrementNumPeers();
        }
        public void setNotConnected (Integer i) {
            failedToConnectPeers.add(i);
            incrementNumPeers();
        }
        public PeerConnectionStore(int limit, AllConnectionsResListener listener){
            this.limit = limit;
            this.listener = listener;
            this.numPeers = 0;
            this.failedToConnectPeers = new ArrayList<>();
        }
    }

    //0, 1, 2
    private static void connectToPeerList (Map<Integer,String> peerList, JLabel indicatorLabel,JButton connectButton) {

        PeerConnectionStore connectionStore = new PeerConnectionStore(peerList.size(), new PeerConnectionStore.AllConnectionsResListener() {
            @Override
            public void onAllConnectionsRes(List<Integer> failedList) {
                if (failedList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to connect to ");
                    for (Integer i : failedList) {
                        sb.append(i + ",");
                    }
                    sb.append(".Please remove.");
                    indicatorLabel.setText(sb.toString());
                    reEnableButton(connectButton);
                } else {
                    System.out.println("Move ahead");
                }
            }
        });

        for (Integer i : peerList.keySet()) {
            if (!peerList.get(i).isEmpty()) {
                network.addPeer("Peer#" + i, peerList.get(i), 8080, 1, new PeerConnectionListener() {
                    @Override
                    public void onConnectionSuccess() {
                        hideTextField(i, "Connected to : " + peerList.get(i));
//                        connectedPeers.add(i);
                        connectionStore.setConnected(i);
                    }

                    @Override
                    public void onConnectionFailure() {
//                        failedToConnectPeers.add(i);
                        connectionStore.setNotConnected(i);
                    }
                });
            }
        }
    }


    private static void hideTextField(int textFieldId,String statusText){
        switch (textFieldId){
            case 0:
//                ipLabel1.setText("Connected to "+ ipTextField1.getText());
                ipLabel1.setText(statusText);
                ipTextField1.setVisible(false);
                break;
            case 1:
//                ipLabel2.setText("Connected to "+ ipTextField2.getText());
                ipLabel2.setText(statusText);
                ipTextField2.setVisible(false);
                break;
            case 2:
//                ipLabel3.setText("Connected to "+ ipTextField3.getText());
                ipLabel3.setText(statusText);
                ipTextField3.setVisible(false);
                break;

        }
    }
    private static void showTextField(int textFieldId){
        switch (textFieldId){
            case 0:
                ipLabel1.setText("Player 2 IP Address :");
                ipTextField1.setVisible(true);
                break;
            case 1:
                ipLabel2.setText("Player 3 IP Address :");
                ipTextField2.setVisible(true);
                break;
            case 2:
                ipLabel3.setText("Player 4 IP Address :");
                ipTextField3.setVisible(true);
                break;

        }
    }

    private static void disableButton(JButton button){
        button.setEnabled(false);
        button.removeMouseListener(onConnectListener);
    }

    private static void reEnableButton(JButton button){
        button.setEnabled(true);
        button.addMouseListener(onConnectListener);
    }

    private static JPanel getTextFieldLayout(String hintText, JTextField textField,JLabel label){
        JPanel textFieldPanel = new JPanel();
        label.setText(hintText);
        textFieldPanel.add(label,BorderLayout.WEST);
        textFieldPanel.add(textField,BorderLayout.EAST);
        return textFieldPanel;
    }

    public static Map<Integer, String> getListedIpAddresses(){
        Map<Integer,String> ipList = new HashMap<>();
        if (!ipTextField1.getText().isEmpty())
            ipList.put(0,ipTextField1.getText());
        if (!ipTextField2.getText().isEmpty())
            ipList.put(1,ipTextField2.getText());
        if (!ipTextField3.getText().isEmpty())
            ipList.put(2,ipTextField3.getText());
        return ipList;
    }

    public static String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
}
class AnimatedJPanel extends JPanel implements ActionListener{
    private Timer clock;
    private ImageIcon imageArray[];
    private int delay=PHOTO_DELAY, totalFrames=N_PHOTOS, currentFrame=0;

    public AnimatedJPanel(String photo){
        imageArray = new ImageIcon[totalFrames];
        for(int i=1;i<=imageArray.length; ++i){
            imageArray[i-1] = new ImageIcon(IMAGES_PATH+photo+i+".png");
        }
        clock = new Timer(delay,this);
        clock.start();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(currentFrame==imageArray.length) currentFrame = 0;
        imageArray[currentFrame++].paintIcon(this,g,this.getWidth()/2-25,this.getHeight()/2-25);
    }
    @Override
    public void actionPerformed(ActionEvent e){
       // System.out.println("Here");
        repaint();
    }
    public static void pausePanel(){
        //clock.stop();
    }
    public void resumePanel(){
        //clock.start();
    }
}

class SpotlightLayerUI extends LayerUI<JPanel> {
    private boolean mActive;
    private int mX, mY;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer)c;
        jlayer.setLayerEventMask(
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer)c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint (Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g.create();

        // Paint the view.
        super.paint (g2, c);

        if (mActive) {
            // Create a radial gradient, transparent in the middle.
//            java.awt.geom.Point2D center = new java.awt.geom.Point2D.Float(mX, mY);
//            float radius = 150;
//            float[] dist = {0.3f, 1.0f};
//            Color[] colors = {new Color(0.0f, 0.0f, 0.0f, 0.0f), Color.BLACK};
//            RadialGradientPaint p =
//                    new RadialGradientPaint(center, radius, dist, colors);
//            g2.setPaint(p);
//            g2.setComposite(AlphaComposite.getInstance(
//                    AlphaComposite.SRC_OVER, 1.0f));
//            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        g2.dispose();
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        if (e.getID() == MouseEvent.MOUSE_ENTERED) mActive = true;
        if (e.getID() == MouseEvent.MOUSE_EXITED) mActive = false;
        l.repaint();
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        mX = p.x;
        mY = p.y;
        l.repaint();
    }

}