package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import common.BusinessException;
import common.Dates;
import common.Jdbc;
import dtos.BudgetProductDto;
import dtos.ProductDto;
import dtos.ProductSaleDto;
import dtos.SaleDto;
import dtos.TransportDto;
import dtos.BudgetDto;
import factories.PersistenceFactory;
import persistence.implementation.BudgetGateway;
import persistence.implementation.BudgetedProductGateway;
import persistence.implementation.ProductGateway;
import persistence.implementation.ProductSaleGateway;
import persistence.implementation.SaleGateway;
import persistence.implementation.TransportationGateway;
import wrappers.ProductDtoWrapper;

public class SalesAdder {
	SaleDto sdto;
	TransportDto tdto;
	int saleId;

	public SalesAdder(SaleDto saledto, TransportDto transportdto) {
		this.sdto = saledto;
		this.tdto = transportdto;
	}

	public int execute() throws BusinessException {
		Connection connection = null;
		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			sdto.date = Dates.toString(new Date());
			// se añade la venta a la tabla y se guarda la id que ha generado la base de
			// datos
			sdto.clientId = ((BudgetDto)((BudgetGateway) PersistenceFactory.createBudgetGateway()).findById(sdto.budgetId)).clientId;
			saleId = ((SaleGateway) PersistenceFactory.createSaleGateway()).addSale(sdto);

			tdto.saleId = sdto.id=saleId;

			// si hay fecha de entrega y por tanto transporte, se añade a la base de datos
			if (tdto.deliveryDate != null) {
				PersistenceFactory.createTransportationGateway().add(tdto);
				TransportationGateway tg = (TransportationGateway) PersistenceFactory.createTransportationGateway();
				// si hay montaje se cambia a si
				if (tdto.requiresAssembly == "SI") {
					tg.updateMontaje(tdto.saleId, true);
				}
			}
			
			BudgetedProductGateway bpg =((BudgetedProductGateway) PersistenceFactory.createBudgetedProductGateway());
			
			//la lista de productos que se deben añadir a venta_productos
			ArrayList<BudgetProductDto> budgetedProducts = (ArrayList<BudgetProductDto>) bpg.findProductsByBudgetId(sdto.budgetId);
			
			ProductSaleGateway psg = (ProductSaleGateway) PersistenceFactory.createProductSaleGateway();
			ProductGateway p = (ProductGateway) PersistenceFactory.createProductGateway();

			// add productos into producto-venta && update almacen
			for (int i = 0; i < budgetedProducts.size(); i++) {
				// se obtiene el dto y se añade a la tabla tventa_productos
				// TODO el precio que se añade tiene que ser el de tpresupuestos
				ProductSaleDto psdto = new ProductSaleDto();
				psdto.productId = budgetedProducts.get(i).productId;
				psdto.saleId = sdto.id;
				psdto.quantity = budgetedProducts.get(i).amount;
				psdto.price = budgetedProducts.get(i).price;
				psg.add(psdto);

				// se cambia el stock en tproductos, si es negativo se pone a cero
				int stock = ((ProductDto) p.findById(budgetedProducts.get(i).productId)).stock;
				int newQuantity = budgetedProducts.get(i).amount;
				int diference = stock - newQuantity;
				if (diference > 0) {
					p.updateStock(budgetedProducts.get(i).productId, diference);
				} else {
					p.updateStock(budgetedProducts.get(i).productId, 0);
				}

			}

			// update budget
			BudgetGateway bg = (BudgetGateway) PersistenceFactory.createBudgetGateway();
			bg.changeToAcepted(sdto.budgetId);

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
}
