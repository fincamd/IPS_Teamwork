package business.serviceLayer;

import dtos.UserDto;

public interface UserService {

	UserDto findUserBySurname(String username);

}
