package business.serviceLayer;

import java.util.List;

import dtos.ClientDto;

/**
 * Interface representing the contract that every ClientService (Façade) must
 * follow. This interface will be used by the UI layer only and acts as an
 * intermediary between this layer and the business layer, isolating all the
 * functionality of the business layer so the UI layer works as an independent
 * module. In order to create new functionalities that the UI needs to execute,
 * a new implementation for the interface must be given or extending an existing
 * one
 * 
 * @author Angel
 *
 */
public interface ClientService {

	/**
	 * Finds all clients in the underlying database
	 * 
	 * @return returns all the clients on the database represented by the ClientDto
	 */
	public List<ClientDto> findAllClients();

	/**
	 * Computes a client id to be used
	 * 
	 * @return the valid client id so that it is not equal to any id on the
	 *         underlying database
	 */
	public int computeClientId();

	/**
	 * Adds a new client to the underlying database
	 * 
	 * @param dtoClient - The dto representing a Client entity
	 */
	public boolean createClient(ClientDto dtoClient);

	/**
	 * Returns the client dto representing the client whose id is passed as a
	 * parameter.
	 * 
	 * @param clientId The id of the client to look for.
	 * @return The ClientDto representing the client whose id is the parameter of
	 *         this method.
	 */
	public ClientDto findById(int clientId);

}
