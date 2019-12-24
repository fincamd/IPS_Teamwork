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
import dtos.ProductDto;
import dtos.ReturnDto;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

/**
 * Represents a Gateway between the business layer class using this and the
 * specific table on the database. This gateway is a Table Data Gateway, so it
 * <<<<<<< HEAD represents one and only one table on the database. It represents
 * the ======= represents one and only one table on the database. It represents
 * the >>>>>>> refs/remotes/origin/develop_7260 TProductos table on the database
 * 
 * @author Angel
 *
 */
public class ProductGateway implements Gateway {

	private Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	/**
	 * Adds a given entity to the specific table <<<<<<< HEAD
	 * 
	 * ======= >>>>>>> refs/remotes/origin/develop_7260
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		// TODO Auto-generated method stub

	}

	/**
	 * Deletes an entity from the specific table on the database <<<<<<< HEAD
	 * 
	 * ======= >>>>>>> refs/remotes/origin/develop_7260
	 * 
	 * @param id - the identifier of the entity
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Updates the information of a given entity on the specific database <<<<<<<
	 * HEAD
	 * 
	 * @param obj - the entity that encapsulates the new values that will be
	 *            represented on the database =======
	 * @param obj - the entity that encapsulates the new values that will be
	 *            represented on the database >>>>>>>
	 *            refs/remotes/origin/develop_7260
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int update(Object obj) throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		PreparedStatement pst = con.prepareStatement(sqlLoader.getProperty("SQL_UPDATE_PRODUCT"));
		ProductDto dto = (ProductDto) obj;

		pst.setString(1, dto.name);
		pst.setDouble(2, dto.publicPrice);
		pst.setDouble(3, dto.supplierPrice);
		pst.setDouble(4, dto.previousPrice);
		pst.setString(5, dto.category);
		pst.setInt(6, dto.stock);
		pst.setInt(7, dto.id);

		int updated = pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"el producto  " + dto.id + " ha sido modificado con los datos: [nombre:" + dto.name
			+ "],[precio publico:" + dto.publicPrice + "],[precio proveedor:" + dto.supplierPrice + 
			"],[precio anterior:" + dto.previousPrice + "],[categoria:" + dto.category +
			"],[stock:" + dto.stock + "]");
		
		Jdbc.close(pst);
		return updated;
	}

	/**
	 * Finds all the entities on the represented table on the database <<<<<<< HEAD
	 * 
	 * ======= >>>>>>> refs/remotes/origin/develop_7260
	 * 
	 * @return a collection of the found entities
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public List<Object> findAll() throws SQLException {
		String sqlSentence = sqlLoader.getProperty("SQL_FIND_ALL_PRODUCTS");
		List<Object> res = new ArrayList<>();

		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sqlSentence);

		Conf columnNames = Conf.getInstance("configs/databasecolumnnames.properties");

		while (rs.next()) {
			ProductDto dto = new ProductDto();

			dto.id = rs.getInt(columnNames.getProperty("PRODUCT_ID"));
			dto.category = rs.getString(columnNames.getProperty("PRODUCT_CATEGORY"));
			dto.name = rs.getString(columnNames.getProperty("PRODUCT_NAME"));
			dto.publicPrice = rs.getDouble(columnNames.getProperty("PRODUCT_PRICE"));
			dto.stock = rs.getInt(columnNames.getProperty("PRODUCT_STOCK"));
			dto.supplierPrice = rs.getDouble(columnNames.getProperty("PRODUCT_PRICESUPPLIER"));
			dto.previousPrice = rs.getDouble("PRECIO_ORIGINAL");

			res.add(dto);
		}
		Jdbc.close(rs, st);
		return res;
	}

	/**
	 * Finds the object represented by a given id <<<<<<< HEAD
	 * 
	 * ======= >>>>>>> refs/remotes/origin/develop_7260
	 * 
	 * @param id - the identifier of the object on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public Object findById(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ProductDto product = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_PRODUCT_BY_ID"));
			preparedStatement.setLong(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				product = new ProductDto();
				product.id = resultSet.getInt("id");
				product.name = resultSet.getString("nombre");
				product.previousPrice = resultSet.getDouble("precio_original");
				product.publicPrice = resultSet.getDouble("precio_publico");
				product.supplierPrice = resultSet.getDouble("precio_proveedor");
				product.category = resultSet.getString("categoria");
				product.stock = resultSet.getInt("cantidad_almacen");
			}

		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return product;
	}

	public String[] getColumnNames() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String[] columnNames = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_GET_COLUMN_NAMES_FOR_BREAKDOWN"));
			resultSet = preparedStatement.executeQuery();
			int columnCount = resultSet.getMetaData().getColumnCount();
			columnNames = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				columnNames[i - 1] = resultSet.getMetaData().getColumnName(i);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return columnNames;
	}

	public void returnProductsToStoreHouse(ReturnDto dto) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_RETURN_PRODUCTS_TO_STOREHOUSE"));
			preparedStatement.setInt(1, dto.quantity);
			preparedStatement.setInt(2, dto.productId);
			preparedStatement.executeUpdate();
			
			// log
			DBLogger.getLogger().log(Level.INFO,
				"para el producto  " + dto.productId + " se le ha aumentado el stock en "+dto.quantity+" unidades");
			
		} finally {
			Jdbc.close(preparedStatement);
		}
	}

	public int findProductStockById(int productId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int stock;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_GET_STOCK_BY_ID"));

			preparedStatement.setLong(1, productId);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			stock = resultSet.getInt("cantidad_almacen");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
		return stock;

	}

	public void addUnitsToStorage(ProductDtoWrapper each) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_ADD_PRODUCTS_TO_STOREHOUSE"));
			preparedStatement.setInt(2, each.getDto().id);
			preparedStatement.setInt(1, each.quantityOrdered);
			preparedStatement.executeUpdate();
			
			// log
			DBLogger.getLogger().log(Level.INFO,
				"para el producto  " + each.getDto().id + " se le ha aumentado el stock en "+each.quantityOrdered+" unidades");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public void updateStock(int id, int quantity) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_REMOVE_FROM_STORAGE"));
			preparedStatement.setInt(1, quantity);
			preparedStatement.setInt(2, id);
			preparedStatement.executeUpdate();
			// log
			DBLogger.getLogger().log(Level.INFO,
				"para el producto  " + id + " se le ha modificado el stock a "+quantity+" unidades");
			
		} finally {
			Jdbc.close(preparedStatement);
		}
	}

	public double findProductSupplierPriceById(int productId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int supplierPrice;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_GET_SUPPLIER_PRICE_BY_ID"));

			preparedStatement.setLong(1, productId);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			supplierPrice = resultSet.getInt("precio_proveedor");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
		return supplierPrice;

	}
}
