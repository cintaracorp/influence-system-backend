/**
 * 
 */
package com.thecorporateer.influence.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thecorporateer.influence.exceptions.PasswordComplexityException;
import com.thecorporateer.influence.exceptions.RepositoryNotFoundException;
import com.thecorporateer.influence.exceptions.UserAlreadyExistsException;
import com.thecorporateer.influence.objects.Division;
import com.thecorporateer.influence.objects.User;
import com.thecorporateer.influence.objects.UserRole;
import com.thecorporateer.influence.repositories.UserRepository;
import com.thecorporateer.influence.repositories.UserRoleRepository;

/**
 * @author Zollak
 * 
 *         Service handling actions concerning the User entity
 *
 */
@Service
public class UserHandlingService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private ObjectService objectService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CorporateerHandlingService corporateerHandlingService;

	public User getUserByName(String name) {

		User user = userRepository.findByUsername(name);

		if (user == null) {
			throw new RepositoryNotFoundException("User not found");
		}

		return user;
	}

	public void updateUser(User user) {

		userRepository.save(user);
	}

	public List<User> getAllUsers() {

		List<User> users = userRepository.findAll();

		if (users == null) {
			throw new RepositoryNotFoundException("User not found.");
		}

		return users;
	}

	/**
	 * 
	 * Checks whether a supplied password matches a user's current password
	 * 
	 * @param user
	 *            The user to check the password for
	 * @param password
	 *            The password from input
	 * @return <code>true</code> if the password is correct; <code>false</code>
	 *         otherwise
	 */
	private void checkCurrentPassword(User user, String password) {

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("Wrong password in change request.");
		}
	}

	/**
	 * 
	 * Set new password for a user
	 * 
	 * @param user
	 *            The user who will get the password changed
	 * @param newPassword
	 *            The new password to be set
	 * @return <code>true</code> if the password is changed; <code>false</code>
	 *         otherwise
	 */
	public void changePassword(Authentication authentication, String currentPassword, String newPassword) {

		User user = getUserByName(authentication.getName());

		checkCurrentPassword(user, currentPassword);

		RuleResult result = validator.validate(new PasswordData(user.getUsername(), newPassword));

		if (!result.isValid()) {
			throw new PasswordComplexityException(String.join(" \n", validator.getMessages(result)));
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		updateUser(user);
	}

	// TODO: set role when creating user
	// TODO: use more than username to create user
	public void createUserWithInfo(String username, String discord_id, String corporateerName, String password, Division mainDivision,
			List<String> divisionNames) {

		User user = new User();
		user.setUsername(username);
		user.setEmail(username);
		user.setEnabled(true);
		user.setDiscord_id(discord_id);
		user.setPassword(passwordEncoder.encode(password));

		corporateerHandlingService.createCorporateerWithDivisions(corporateerName, mainDivision, divisionNames);

		user.setCorporateer(corporateerHandlingService.getCorporateerByName(corporateerName));

		List<UserRole> roles = new ArrayList<UserRole>();
		roles.add(userRoleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);
		updateUser(user);
	}

	public String createUser(String name, Division mainDivision, List<String> divisionNames) {

		if(null != userRepository.findByUsername(name)) {
			throw new UserAlreadyExistsException("This username is already taken");
		}
		String password = generatePassword();

		createUserWithInfo(name, "", name, password, mainDivision, divisionNames);
		return password;
	}
	
	public String createUser(String name, Division mainDivision, String discord_id, List<String> divisionNames) {

		if(null != userRepository.findByUsername(name)) {
			throw new UserAlreadyExistsException("This username is already taken");
		}
		String password = generatePassword();

		createUserWithInfo(name, name, discord_id, password, mainDivision, divisionNames);
		return password;
	}

	public void createTestuser(String username, String corporateerName, String password, boolean admin) {

		createUserWithInfo(username, corporateerName, null, password, objectService.getDefaultDivision(),
				new ArrayList<String>());

		if (admin) {
			List<UserRole> roles = new ArrayList<UserRole>();
			roles.add(userRoleRepository.findByName("ROLE_ADMIN"));

			User user = getUserByName(username);
			user.setRoles(roles);

			updateUser(user);
		}
	}
	
	public String resetPassword(String username) {
		
		User user = getUserByName(username);
		
		String password = generatePassword();
		
		user.setPassword(passwordEncoder.encode(password));
		
		updateUser(user);
		
		return password;
	}
	
	private String generatePassword() {
		List<CharacterRule> rules = new ArrayList<CharacterRule>(
				Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
						new CharacterRule(EnglishCharacterData.LowerCase, 1),
						new CharacterRule(EnglishCharacterData.Digit, 1)));
		PasswordGenerator generator = new PasswordGenerator();
		
		return generator.generatePassword(8, rules);
	}

	/**
	 * Settings for password validation
	 */
	PasswordValidator validator = new PasswordValidator(

			// length between 8 and 20 characters
			new LengthRule(8, 20),

			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),

			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),

			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1),

			// at least one symbol (special character)
			new CharacterRule(EnglishCharacterData.Special, 1),

			// username is not allowed as part of the password (not even backwards)
			new UsernameRule(true, true),

			// no whitespace
			new WhitespaceRule());
}
