package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ProductSaleDto;
import dtos.SupplierOrderedProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.ProductGateway;

public class DirectSaleItemChecker {
	
	Gateway productGateway = PersistenceFactory.createProductGateway();
	List<ProductSaleDto> productDtos;
	Connection con = null;
	public DirectSaleItemChecker(List<ProductSaleDto> products) {
		productDtos= products;
	}
	
	public List<SupplierOrderedProductDto> execute() {
		List<SupplierOrderedProductDto> missingProducts = new ArrayList<SupplierOrderedProductDto>();
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			SupplierOrderedProductDto productWithMissingStock;
			for (ProductSaleDto orderedProduct: productDtos) {
				int stock = ((ProductGateway) productGateway).findProductStockById(orderedProduct.productId);
				if (stock < orderedProduct.quantity) {
					int missingStock = orderedProduct.quantity - stock;
					productWithMissingStock = new SupplierOrderedProductDto();
					productWithMissingStock.productId = orderedProduct.productId;
					productWithMissingStock.quantity = missingStock;
					productWithMissingStock.price = ((ProductGateway) productGateway).findProductSupplierPriceById(orderedProduct.productId);
					missingProducts.add(productWithMissingStock);
				}
			}			
			con.commit();
		} catch (SQLException e) {
			try {
				e.printStackTrace();
				System.out.println("\n");
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException();
			}
		} finally {
			Jdbc.close(con);
		}
		return missingProducts;
	}
}
