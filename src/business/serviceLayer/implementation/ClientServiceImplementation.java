package business.serviceLayer.implementation;

import java.util.List;

import business.clientLogic.ClientAdder;
import business.clientLogic.ClientFinder;
import business.serviceLayer.ClientService;
import dtos.ClientDto;

/**
 * This class represents an specific implementation for the ClientService
 * (Façade). This class will delegate all Budget related operations to classes
 * on the business layer, it acts as an intermediary between the UI layer and
 * the business layer
 * 
 * @author Angel
 *
 */
public class ClientServiceImplementation implements ClientService {

	/**
	 * Delegates to the business layer class ClientFinder the listing of all the
	 * clients on the underlying database
	 */
	@Override
	public List<ClientDto> findAllClients() {
		return new ClientFinder().findAllClients();
	}

	/**
	 * Delegates to the business layer class ClientFinder the creation of a new
	 * client id
	 */
	@Override
	public int computeClientId() {
		return new ClientFinder().computeBiggestId();
	}

	/**
	 * Delegates to the business layer class ClientAdder the insertion of a client
	 * into the underlying database
	 */
	@Override
	public boolean createClient(ClientDto dtoClient) {
		return new ClientAdder(dtoClient).addClient();
	}

	@Override
	public ClientDto findById(int clientId) {
		return new ClientFinder().findById(clientId);
	}

}
