package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import common.Dates;
import common.Jdbc;
import dtos.ProductDto;
import dtos.ProductSaleDto;
import dtos.SaleDto;
import dtos.TransportDto;
import factories.PersistenceFactory;
import persistence.implementation.ProductGateway;
import persistence.implementation.ProductSaleGateway;
import persistence.implementation.SaleGateway;
import persistence.implementation.TransportationGateway;

public class DirectSalesAdder {
	
	SaleDto sdto;
	TransportDto tdto;
	List<ProductSaleDto> fullOrder;
	Connection connection = null;
	int saleId;
	public DirectSalesAdder(SaleDto sdto, TransportDto tdto, List<ProductSaleDto> fullOrder) {
		this.sdto= sdto;
		this.tdto= tdto;
		this.fullOrder=fullOrder;

	}

	public int execute() {
		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			sdto.date = Dates.toString(new Date());
			saleId = ((SaleGateway) PersistenceFactory.createSaleGateway()).addDirectSale(sdto);

			tdto.saleId = sdto.id=saleId;
			
			//si hay fecha de entrega  y por tanto transporte, se añade a la base de datos
			if (tdto.deliveryDate != null) {
				PersistenceFactory.createTransportationGateway().add(tdto);
				TransportationGateway tg = (TransportationGateway) PersistenceFactory.createTransportationGateway();
				//si hay montaje se cambia a si
				if (tdto.requiresAssembly == "SI") {
					tg.updateMontaje(tdto.saleId, true);
				}
			}
			
			ProductSaleGateway psg = (ProductSaleGateway) PersistenceFactory.createProductSaleGateway();
			
			for(ProductSaleDto product: fullOrder) {
				product.saleId= saleId;
				psg.add(product);
				
				updateStock(product);
			}
			
			connection.commit();
			
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		} finally {
			Jdbc.close(connection);
		}
		return saleId;
	}

	/**
	 * @param product
	 * @throws SQLException
	 */
	private void updateStock(ProductSaleDto product)
		throws SQLException {
		ProductGateway p = (ProductGateway) PersistenceFactory.createProductGateway();
		int stock = ((ProductDto) p.findById(product.productId)).stock;
		int remainingStock= stock - product.quantity;
		if(remainingStock>0) {
			p.updateStock(product.productId, remainingStock);
		}
		else {
			p.updateStock(product.productId, 0);
		}

	}

}
