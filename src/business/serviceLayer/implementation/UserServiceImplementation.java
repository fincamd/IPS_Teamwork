package business.serviceLayer.implementation;

import business.loginLogic.FindUserBySurname;
import business.serviceLayer.UserService;
import dtos.UserDto;

public class UserServiceImplementation implements UserService {

	@Override
	public UserDto findUserBySurname(String username) {
		return new FindUserBySurname(username).execute();
	}

}
