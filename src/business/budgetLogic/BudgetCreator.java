package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.BudgetDto;
import dtos.BudgetProductDto;
import dtos.ClientDto;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

/**
 * This class encapsulates the whole budget creation process. This process has
 * some steps that must be done on a transactional way, thats why operations
 * with different gateways are done here with the same connection
 * 
 * @author Angel
 *
 */
public class BudgetCreator {

	/**
	 * Create a budget where the client is not specified or the client already
	 * exists, this method makes the whole process transactional
	 * 
	 * @param dtoBudget   - Dto representing the new Budget
	 * @param dtosProduct - List of ProductDto that represents all the products of
	 *                    the budget
	 * @return true if everything went fine, false if some problem happened
	 */
	public boolean createBudgetWithoutClientOrExistingClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct) {
		Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
		Gateway budgetProductGateway = PersistenceFactory.createBudgetProductGateway();

		Connection con = null;

		boolean succesfulCreation = false;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			int newId = findNewId(budgetGateway);
			dtoBudget.id = newId;
			
			if(dtoBudget.clientId == null) {
				dtoBudget.status = "MODELO";
			} else {
				dtoBudget.status = "NO ACEPTADO";
			}

			budgetGateway.add(dtoBudget);

			for (ProductDtoWrapper dtoProductWrapper : dtosProduct) {
				ProductDto dtoProduct = dtoProductWrapper.getDto();

				BudgetProductDto budgetProductDto = new BudgetProductDto();
				budgetProductDto.budgetId = dtoBudget.id;
				budgetProductDto.productId = dtoProduct.id;
				budgetProductDto.amount = dtoProductWrapper.quantityOrdered;
				budgetProductDto.price = dtoProduct.publicPrice;
				budgetProductGateway.add(budgetProductDto);
			}

			con.commit();
			succesfulCreation = true;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException();
			}
		} finally {
			Jdbc.close(con);
		}

		return succesfulCreation;
	}

	/**
	 * Create a budget where the client is a new one this method makes the whole
	 * process transactional
	 * 
	 * @param dtoBudget   - Dto representing the new Budget
	 * @param dtosProduct - List of ProductDto that represents all the products of
	 *                    the budget
	 * @param dtoClient   - Dto representing the new Client
	 * @return true if everything went fine, false if some problem happened
	 */
	public boolean createBudgetWithNewClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct,
			ClientDto dtoClient) {
		Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
		Gateway budgetProductGateway = PersistenceFactory.createBudgetProductGateway();
		Gateway clientGateway = PersistenceFactory.createClientGateway();
		Connection con = null;

		boolean succesfulCreation = false;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			int newBudgetId = findNewId(budgetGateway);
			int newClientId = findNewClientId(clientGateway);

			dtoClient.id = newClientId;
			clientGateway.add(dtoClient);

			dtoBudget.clientId = newClientId;
			dtoBudget.id = newBudgetId;

			dtoBudget.status = "NO ACEPTADO";
			budgetGateway.add(dtoBudget);

			for (ProductDtoWrapper dtoProductWrapper : dtosProduct) {
				ProductDto dtoProduct = dtoProductWrapper.getDto();

				BudgetProductDto budgetProductDto = new BudgetProductDto();
				budgetProductDto.budgetId = dtoBudget.id;
				budgetProductDto.productId = dtoProduct.id;
				budgetProductDto.amount = dtoProductWrapper.quantityOrdered;
				budgetProductDto.price = dtoProduct.publicPrice;
				budgetProductGateway.add(budgetProductDto);
			}

			con.commit();
			succesfulCreation = true;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException();
			}
		} finally {
			Jdbc.close(con);
		}

		return succesfulCreation;
	}

	private int findNewClientId(Gateway clientGateway) throws SQLException {
		List<Object> allClients = clientGateway.findAll();
		List<ClientDto> castedDtos = new ArrayList<>();
		for (Object obj : allClients)
			castedDtos.add((ClientDto) obj);

		return newClientiD(castedDtos);
	}

	private int newClientiD(List<ClientDto> dtos) {
		int max = 0;

		for (ClientDto dto : dtos) {
			int possible = dto.id;
			if (possible > max)
				max = possible;
		}

		return max + 1;
	}

	private int findNewId(Gateway budgetGateway) throws SQLException {
		List<Object> allBudgets = budgetGateway.findAll();
		List<BudgetDto> castedDtos = new ArrayList<>();
		for (Object obj : allBudgets) {
			castedDtos.add((BudgetDto) obj);
		}

		return newId(castedDtos);
	}

	private int newId(List<BudgetDto> dtos) {
		int max = 0;

		for (BudgetDto dto : dtos) {
			int possible = dto.id;
			if (possible > max)
				max = possible;
		}

		return max + 1;
	}

}
