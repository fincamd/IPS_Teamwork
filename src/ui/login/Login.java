package ui.login;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import business.loginLogic.LoginManager;
import dtos.UserDto;
import factories.ServiceFactory;
import ui.main.MainWindow;

public class Login extends JDialog {

	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 3377984554683337119L;

	// Extra fields
	// ------------------------------------------------------------------------

	private TxtFieldContentSelectorListener txtFieldContentSelectorListener =
			new TxtFieldContentSelectorListener();

	// Fields for the components of the window
	// ------------------------------------------------------------------------

	private JPanel pnLoginFields;
	private JPanel pnUsername;
	private JPanel pnPassword;
	private JLabel lblWelcomeMessage;
	private JLabel lblAppLoginTitle;
	private JLabel lblUsername;
	private JTextField txtUsername;
	private JLabel lblPassword;
	private JButton btnLogin;
	private JButton btnExitLogin;
	private JPasswordField txtPassword;

	// Constructor
	// ------------------------------------------------------------------------

	public Login() {
		setResizable(false);
		setTitle("Mueblerías pepín: Inicio de sesión");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainWindow.class.getResource("/resources/appicon.png")));
		setModal(true);
		setBounds(100, 100, 560, 390);
		getContentPane().setLayout(null);
		getContentPane().add(getLblAppLoginTitle());
		getContentPane().add(getLblWelcomeMessage());
		getContentPane().add(getPnLoginFields());
		getContentPane().add(getBtnLogin());
		getContentPane().add(getBtnExitLogin());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				askUserIfExitIsNecessary();
			}
		});

		getRootPane().setDefaultButton(btnLogin);
	}

	// Basic methods
	// ------------------------------------------------------------------------

	private JPanel getPnLoginFields() {
		if (pnLoginFields == null) {
			pnLoginFields = new JPanel();
			pnLoginFields.setBounds(70, 160, 404, 87);
			pnLoginFields.setLayout(new GridLayout(0, 1, 0, 0));
			pnLoginFields.add(getPnUsername());
			pnLoginFields.add(getPnPassword());
		}
		return pnLoginFields;
	}

	private JPanel getPnUsername() {
		if (pnUsername == null) {
			pnUsername = new JPanel();
			pnUsername.setLayout(null);
			pnUsername.add(getLblUsername());
			pnUsername.add(getTxtUsername());
		}
		return pnUsername;
	}

	private JPanel getPnPassword() {
		if (pnPassword == null) {
			pnPassword = new JPanel();
			pnPassword.setLayout(null);
			pnPassword.add(getLblPassword());
			pnPassword.add(getTxtPassword());
		}
		return pnPassword;
	}

	private JLabel getLblWelcomeMessage() {
		if (lblWelcomeMessage == null) {
			lblWelcomeMessage = new JLabel(
					"Bienvenido a la aplicación de gestión de nuestra empresa");
			lblWelcomeMessage.setBounds(97, 94, 344, 17);
			lblWelcomeMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblWelcomeMessage;
	}

	private JLabel getLblAppLoginTitle() {
		if (lblAppLoginTitle == null) {
			lblAppLoginTitle = new JLabel("MUEBLERÍAS PEPÍN");
			lblAppLoginTitle.setBounds(178, 61, 181, 22);
			lblAppLoginTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		}
		return lblAppLoginTitle;
	}

	private JLabel getLblUsername() {
		if (lblUsername == null) {
			lblUsername = new JLabel("Nombre de usuario:");
			lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
			lblUsername.setBounds(0, 14, 118, 14);
		}
		return lblUsername;
	}

	private JTextField getTxtUsername() {
		if (txtUsername == null) {
			txtUsername = new JTextField();
			txtUsername.addFocusListener(txtFieldContentSelectorListener);
			txtUsername.setHorizontalAlignment(SwingConstants.CENTER);
			txtUsername.setBounds(128, 11, 266, 20);
			txtUsername.setColumns(10);
		}
		return txtUsername;
	}

	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Contraseña: ");
			lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPassword.setBounds(0, 14, 118, 14);
		}
		return lblPassword;
	}

	private JButton getBtnLogin() {
		if (btnLogin == null) {
			btnLogin = new JButton("Iniciar sesión");
			btnLogin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					processLogin();
				}
			});
			btnLogin.setBounds(432, 327, 112, 23);
		}
		return btnLogin;
	}

	private JButton getBtnExitLogin() {
		if (btnExitLogin == null) {
			btnExitLogin = new JButton("Salir");
			btnExitLogin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					askUserIfExitIsNecessary();
				}
			});
			btnExitLogin.setBounds(333, 327, 89, 23);
		}
		return btnExitLogin;
	}

	private JPasswordField getTxtPassword() {
		if (txtPassword == null) {
			txtPassword = new JPasswordField();
			txtPassword.addFocusListener(txtFieldContentSelectorListener);
			txtPassword.setHorizontalAlignment(SwingConstants.CENTER);
			txtPassword.setBounds(128, 11, 266, 20);
		}
		return txtPassword;
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private void processLogin() {
		boolean areFieldsValid = checkIdFieldsAreValid();
		if (areFieldsValid) {
			String username = txtUsername.getText();
			String password = String.valueOf(txtPassword.getPassword());
			LoginManager loginManager = new LoginManager(username, password);
			loginManager.process();
			if (loginManager.wasLoginSuccessful()) {
				JOptionPane.showMessageDialog(this,
						"Se ha iniciado sesion satisfactoriamente"
								+ "\nBienvenido: " + username,
						"Exito", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} else {
				JOptionPane.showMessageDialog(this,
						"La combinación: nombre de usuario y contraseña no se corresponde con ninguno de nuestros usuarios",
						"Error", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private boolean checkIdFieldsAreValid() {
		if (txtUsername.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"El nombre de usuario no puede estar vacio",
					"Error en el inicio de sesion",
					JOptionPane.WARNING_MESSAGE);
			return false;
		} else {
			UserDto user = getUserFromDatabase(txtUsername.getText());
			if (user == null) {
				JOptionPane.showMessageDialog(this,
						"Ese nombre de usuario no figura en nuestros registros",
						"Error en el inicio de sesion",
						JOptionPane.WARNING_MESSAGE);
				txtUsername.requestFocus();
				txtUsername.selectAll();
				return false;
			} else {
				if (String.valueOf(txtPassword.getPassword()).isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"La contraseña no puede estar vacia",
							"Error en el inicio de sesión",
							JOptionPane.WARNING_MESSAGE);
					txtPassword.requestFocus();
					return false;
				}
			}
		}
		return true;
	}

	private UserDto getUserFromDatabase(String username) {
		UserDto user = null;
		user = ServiceFactory.createUserService().findUserBySurname(username);
		return user;
	}

	private void askUserIfExitIsNecessary() {
		int option = JOptionPane.showConfirmDialog(this,
				"La aplicación se cerrará\n¿De verdad quieres salir?",
				"Saliendo de la aplicación", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	// My listeners
	// ------------------------------------------------------------------------

	private class TxtFieldContentSelectorListener extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {
			JTextComponent txtComponent = (JTextComponent) e.getSource();
			txtComponent.selectAll();
		}

	}

}
