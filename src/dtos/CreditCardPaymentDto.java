package dtos;

import java.util.Date;

public class CreditCardPaymentDto extends PaymentMethodDto {
	public String ownerName;
	public int cvv;
	public Date dueDate;
	public String cardNumber;
	
}
