package ui.budget;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CreateBudgetMenu extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblBudgetCreation;
	private JTabbedPane tbpBudgetCreation;
	private CreateBudgetFirstPanel newBudgetPanelReference;
	private CreateBudgetFromModel budgetFromModelPanelReference;
	
	/**
	 * Create the panel.
	 */
	public CreateBudgetMenu() {
		setLayout(new BorderLayout(0, 0));
		add(getLblBudgetCreation(), BorderLayout.NORTH);
		add(getTbpBudgetCreation(), BorderLayout.CENTER);

	}

	private JLabel getLblBudgetCreation() {
		if (lblBudgetCreation == null) {
			lblBudgetCreation = new JLabel("Creación de Presupuestos");
			lblBudgetCreation.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblBudgetCreation.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblBudgetCreation;
	}
	private JTabbedPane getTbpBudgetCreation() {
		if (tbpBudgetCreation == null) {
			tbpBudgetCreation = new JTabbedPane(JTabbedPane.TOP);
			tbpBudgetCreation.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					budgetFromModelPanelReference.reset();
					newBudgetPanelReference.reset();
				}
			});
			
			newBudgetPanelReference = new CreateBudgetFirstPanel();
			budgetFromModelPanelReference = new CreateBudgetFromModel();
			tbpBudgetCreation.addTab("Crear Presupuesto A Partir De Modelo", budgetFromModelPanelReference);
			tbpBudgetCreation.addTab("Crear Presupuesto Nuevo", newBudgetPanelReference);
		}
		return tbpBudgetCreation;
	}
	
	public void reset() {
		resetWindow();
	}

	private void resetWindow() {
		newBudgetPanelReference.reset();
		budgetFromModelPanelReference.reset();
	}
}
