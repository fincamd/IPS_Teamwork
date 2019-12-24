package ui.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.jtattoo.plaf.texture.TextureLookAndFeel;

import business.loginLogic.User;
import common.Conf;
import common.DBLogger;
import ui.balance.BalancePanel;
import ui.budget.CreateBudgetFirstPanel;
import ui.budget.CreateBudgetMenu;
import ui.budget.ShowPendingBudgetsPanel;
import ui.directSales.DirectSales;
import ui.login.Login;
import ui.priceChange.PublicPriceChange;
import ui.salesHistory.SalesHistoryPanel;
import ui.supplierOrdersPanel.SupplierOrdersPanel;
import ui.transportation.NotReceivedTransportationsFollow;

/**
 * Entry point of the UI, this is the first panel that will be showed to the
 * user
 * 
 * @author Angel
 *
 */
public class MainWindow extends JFrame {

	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 1065657624371078144L;

	// Main frame components
	// ------------------------------------------------------------------------

	private JPanel contentPane;
	private JPanel pnOptionsMenu;
	private JPanel pnCardLayout;
	private JButton btnPresupuestos;
	private JButton btnPendingBudgets;
	private JButton btnVentas;
	private JButton btnPedidosProveedor;
	private JButton btnTransportationFollowage;
	private JButton btnPublicPriceChange;
	private JButton btnBalance;
	private JButton btnDirectSales; //casaDeCampoVendedor1
	private JPanel pnLoggedAsInfo;
	private JLabel lblUserRole;
	private JLabel lblUsername;
	private JLabel lblUserRoleData;
	private JLabel lblUsernameData;

	// Fields
	// ------------------------------------------------------------------------

	private CardLayout cardLayoutMainWindow;
	private Conf conf = Conf.getInstance("configs/mainCardLayoutNames.properties");
	private String createBudgetFirstPanel = conf.getProperty("CREATEBUDGETFIRSTPANEL_NAME");
	private String showPendingBudgetsPanel = conf.getProperty("SHOWPENDINGBUDGETS_NAME");
	private String createSupplyOrderPanel = conf.getProperty("CREATESUPPLIERORDERPANEL_NAME");
	private String createSalesHistoryPanel = conf.getProperty("CREATESALESHISTORYPANEL_NAME");
	private String notRecievedTransportationsPanel = conf.getProperty("TRANSPORATIONTRACINGPANEL_NAME");
	private String createBalancePanel = conf.getProperty("BALANCEPANEL_NAME");
	private String publicPriceChangePanel = conf.getProperty("PUBLICPRICECHANGEPANEL_NAME");
	private String createDirectSalesPanel = conf.getProperty("DIRECT_SALES_NAME");

	private SalesHistoryPanel salesPanel;
	private SupplierOrdersPanel ordersPanel;
	private BalancePanel balancePanel;
	private CreateBudgetMenu createBudgetFirstPanelReference = new CreateBudgetMenu();
	private DirectSales directSales;
	private NotReceivedTransportationsFollow notReceivedTransportationsPanelReference = new NotReceivedTransportationsFollow();
	private PublicPriceChange publicPriceChangePanelReference = new PublicPriceChange();

	
	

	// Main method
	// ------------------------------------------------------------------------

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Properties props = new Properties();
					props.put("logoString", "");

					/* Kind of shiny theme */
					TextureLookAndFeel.setCurrentTheme(props);
					UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");

					/* Dark theme */
//					HiFiLookAndFeel.setCurrentTheme(props);
//					UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

					Login login = new Login();
					login.setVisible(true);

					// inicio del logger
					DBLogger.iniciarSesion();

					MainWindow frame = new MainWindow();
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor
	// ------------------------------------------------------------------------

	public MainWindow() {
		setTitle("Mueblerías Pepín");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/appicon.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPnOptionsMenu(), BorderLayout.WEST);
		contentPane.add(getPnCardLayout(), BorderLayout.CENTER);

		if (User.getInstance("", "").getType() != "Transportista") {
			getPnCardLayout().add(createBudgetFirstPanel, new CreateBudgetMenu());
			getPnCardLayout().add(showPendingBudgetsPanel, new ShowPendingBudgetsPanel());
			ordersPanel = new SupplierOrdersPanel();
			getPnCardLayout().add(createSupplyOrderPanel, ordersPanel);
			salesPanel = new SalesHistoryPanel();
			getPnCardLayout().add(createSalesHistoryPanel, salesPanel);
			getPnCardLayout().add(createBudgetFirstPanel, createBudgetFirstPanelReference);
			balancePanel = new BalancePanel();
			getPnCardLayout().add(createBalancePanel, balancePanel);
			getPnCardLayout().add(publicPriceChangePanel, publicPriceChangePanelReference);
			directSales = new DirectSales();
			getPnCardLayout().add(createDirectSalesPanel, directSales);
			
		}

		getPnCardLayout().add(notRecievedTransportationsPanel, notReceivedTransportationsPanelReference);

		lblUserRoleData.setText(User.getInstance("", "").getType());
		lblUsernameData.setText(User.getInstance("", "").getUsername());
	}

	// Getters for the components of the main frame
	// ------------------------------------------------------------------------

	private JPanel getPnOptionsMenu() {
		if (pnOptionsMenu == null) {
			pnOptionsMenu = new JPanel();
			pnOptionsMenu.setLayout(new GridLayout(0, 1, 0, 0));
			if (User.getInstance("", "").getType() != "Transportista") {
				pnOptionsMenu.add(getBtnPresupuestos());
				pnOptionsMenu.add(getBtnPendingBudgets());
				pnOptionsMenu.add(getBtnVentas());
				pnOptionsMenu.add(getBtnPedidosProveedor());
				pnOptionsMenu.add(getBtnPublicPriceChange());
				pnOptionsMenu.add(getBtnBalance());
				pnOptionsMenu.add(getBtnDirectSales());
			}
			pnOptionsMenu.add(getBtnTransportationFollowage());
			pnOptionsMenu.add(getPnLoggedAsInfo());
		}
		return pnOptionsMenu;
	}

	private JPanel getPnCardLayout() {
		if (pnCardLayout == null) {
			pnCardLayout = new JPanel();
			cardLayoutMainWindow = new CardLayout(0, 0);
			pnCardLayout.setLayout(cardLayoutMainWindow);
		}
		return pnCardLayout;
	}

	private JButton getBtnPublicPriceChange() {
		if (btnPublicPriceChange == null) {
			btnPublicPriceChange = new JButton("Cambiar Precio de Productos");
			btnPublicPriceChange.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					publicPriceChangePanelReference.reset();
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(), publicPriceChangePanel);
				}
			});
		}
		return btnPublicPriceChange;
	}

	private JButton getBtnPresupuestos() {
		if (btnPresupuestos == null) {
			btnPresupuestos = new JButton("Creación de Presupuestos");
			btnPresupuestos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createBudgetFirstPanelReference.reset();
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(), createBudgetFirstPanel);
				}
			});
		}
		return btnPresupuestos;
	}

	private JButton getBtnPendingBudgets() {
		if (btnPendingBudgets == null) {
			btnPendingBudgets = new JButton("Presupuestos pendientes");
			btnPendingBudgets.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					((CardLayout) pnCardLayout.getLayout()).show(getPnCardLayout(), showPendingBudgetsPanel);
				}
			});
		}
		return btnPendingBudgets;
	}

	private JButton getBtnVentas() {
		if (btnVentas == null) {
			btnVentas = new JButton("Ventas");
			btnVentas.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(),
							Conf.getInstance("configs/mainCardLayoutNames.properties")
									.getProperty("CREATESALESHISTORYPANEL_NAME"));
					salesPanel.reset();
				}
			});
		}
		return btnVentas;
	}

	private JButton getBtnPedidosProveedor() {
		if (btnPedidosProveedor == null) {
			btnPedidosProveedor = new JButton("Pedidos proveedor");
			btnPedidosProveedor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(), createSupplyOrderPanel);
					ordersPanel.reset();
				}
			});
		}
		return btnPedidosProveedor;
	}

	private JButton getBtnBalance() {
		if (btnBalance == null) {
			btnBalance = new JButton("Balance");
			btnBalance.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(), createBalancePanel);
					balancePanel.paint();
				}
			});
		}
		return btnBalance;
	}

	private JButton getBtnTransportationFollowage() {
		if (btnTransportationFollowage == null) {
			btnTransportationFollowage = new JButton("Seguimiento de Transportes");
			btnTransportationFollowage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					notReceivedTransportationsPanelReference.reset();
					((CardLayout) getPnCardLayout().getLayout()).show(getPnCardLayout(),
							notRecievedTransportationsPanel);
				}
			});
		}
		return btnTransportationFollowage;
	}
	
	private JButton getBtnDirectSales() {
		if (btnDirectSales == null) {
			btnDirectSales = new JButton("Venta Directa");
			btnDirectSales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					((CardLayout) pnCardLayout.getLayout()).show(getPnCardLayout(), createDirectSalesPanel);
				}
			});
		}
		return btnDirectSales;
	}

	private JPanel getPnLoggedAsInfo() {
		if (pnLoggedAsInfo == null) {
			pnLoggedAsInfo = new JPanel();
			pnLoggedAsInfo.setLayout(new GridLayout(0, 1, 0, 0));
			pnLoggedAsInfo.add(getLblUserRole());
			pnLoggedAsInfo.add(getLblUserRoleData());
			pnLoggedAsInfo.add(getLblUsername());
			pnLoggedAsInfo.add(getLblUsernameData());
		}
		return pnLoggedAsInfo;
	}

	private JLabel getLblUserRole() {
		if (lblUserRole == null) {
			lblUserRole = new JLabel("Has iniciado sesión como:");
			lblUserRole.setFont(new Font("Tahoma", Font.BOLD, 12));
			lblUserRole.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblUserRole;
	}

	private JLabel getLblUserRoleData() {
		if (lblUserRoleData == null) {
			lblUserRoleData = new JLabel("");
			lblUserRoleData.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblUserRoleData;
	}

	private JLabel getLblUsernameData() {
		if (lblUsernameData == null) {
			lblUsernameData = new JLabel("");
			lblUsernameData.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblUsernameData;
	}

	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Nombre de usuario:");
			lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
			lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblUsername;
	}
}
