package dtos;

/**
 * Represents a client on the underlying database. It´s used to transfer data
 * between layers
 * 
 * @author Angel
 *
 */
public class ClientDto {
	public int id, phoneNumber;
	public String dni, name, street, postCode;
}
