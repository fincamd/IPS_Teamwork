package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.BudgetProductDto;
import dtos.ProductDto;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

public class BudgetedProductGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {

	}

	@Override
	public int delete(int id) throws SQLException {
		return 0;
	}

	@Override
	public int update(Object obj) throws SQLException {
		BudgetProductDto dto = (BudgetProductDto) obj;

		Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");
		String sqlQuery = sqlLoader.getProperty("SQL_UPDATE_BUDGETPRODUCT");
		Connection con = Jdbc.getCurrentConnection();
		PreparedStatement pst = con.prepareStatement(sqlQuery);
		pst.setInt(1, dto.amount);
		pst.setDouble(2, dto.price);
		pst.setInt(3, dto.budgetId);
		pst.setInt(4, dto.productId);

		int res = pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
				"producto " + dto.productId + " modificado para el presupuesto "+dto.budgetId+
				" con datos: [cantidad:" + dto.amount + "],[precio:" + dto.price + "]");

		Jdbc.close(pst);
		return res;
	}

	@Override
	public List<Object> findAll() throws SQLException {
		Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");
		String sqlQuery = sqlLoader.getProperty("SQL_FIND_ALL_BUDGETPRODUCTS");
		List<Object> res = new ArrayList<>();
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sqlQuery);

		while (rs.next()) {
			BudgetProductDto dto = new BudgetProductDto();
			dto.budgetId = rs.getInt("ID_PRESUPUESTO");
			dto.productId = rs.getInt("ID_PRODUCTO");
			dto.amount = rs.getInt("CANTIDAD");
			dto.price = rs.getDouble("CUANTIA");

			res.add(dto);
		}

		Jdbc.close(rs, st);
		return res;
	}

	@Override
	public Object findById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProductDtoWrapper> findProductsInBudget(int budgetId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ProductDtoWrapper> products = new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_PRODUCT_ID_IN_BUDGET_BY_ID"));
			preparedStatement.setInt(1, budgetId);
			resultSet = preparedStatement.executeQuery();

			ProductDto product;
			ProductDtoWrapper wrapper;
			while (resultSet.next()) {
				product = new ProductDto();
				product.id = resultSet.getInt("id_producto");
				wrapper = new ProductDtoWrapper(product);
				products.add(wrapper);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return products;
	}

	public List<BudgetProductDto> findProductsByBudgetId(int budgetId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<BudgetProductDto> products = new ArrayList<BudgetProductDto>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_PRODUCTS_IN_BUDGET_BY_ID"));
			preparedStatement.setInt(1, budgetId);
			resultSet = preparedStatement.executeQuery();

			BudgetProductDto product;
			while (resultSet.next()) {
				product = new BudgetProductDto();
				product.productId = resultSet.getInt("id_producto");
				product.amount = resultSet.getInt("cantidad");
				product.budgetId = resultSet.getInt("id_presupuesto");
				product.price = resultSet.getInt("cuantia");
				products.add(product);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return products;
	}

}
