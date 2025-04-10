package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.dto.UserDTO;
import es.codeurjc.practica1.dto.UserMapper;
import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
	private UserMapper mapper;

     private User toDomain(UserDTO userDTO) {
		return mapper.toDomain(userDTO);
	}

	private List<UserDTO> toDTOs(List<User> Users) {
		return mapper.toDTOs(Users);
	}

    private UserDTO toDTO(User User) {
		return mapper.toDTO(User);
	}

    public UserDTO getUser(String name) {
		return mapper.toDTO(userRepository.findByName(name).orElseThrow());
	}

	public User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByName(username).get();
    }

	public UserDTO getLoggedUserDTO() {
        return mapper.toDTO(getLoggedUser());
    }

    public List<UserDTO> findAll() {
        return toDTOs (userRepository.findAll());
    }

    public UserDTO findById(long id) {
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    public List<User> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public UserDTO findByEmail(String email) {
        return toDTO(userRepository.findByEmail(email).orElseThrow());
    }

    public List<UserDTO> findByDeleted(boolean deleted) {
        return toDTOs(userRepository.findByDeleted(deleted));
    }

    public UserDTO save(UserDTO userDTO) {
        User user = toDomain(userDTO);
        userRepository.save(user);
        return toDTO(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(UserDTO userDTO) {
        User user = toDomain(userDTO);
        user.setDeletedd(true);
        userRepository.save(user);
        userRepository.delete(user);
    }

    public void addUser(UserDTO userDTO) {
        User user = toDomain(userDTO);
        userRepository.save(user);
    }

    public List<UserDTO> findAllById(List<Long> ids) {
        return toDTOs(userRepository.findAllById(ids));
    }

    public UserDTO getLoggedUser(String name) {
        User user=userRepository.findByName(name).orElseThrow(()-> new UsernameNotFoundException("USER SERVICE LINE 44") );
        return toDTO(user);
    }

    public UserDTO findByName(String name) {
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toDTO(user);
    }

    public void addOrder(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            
            User user = userOptional.get();
            user.addAnOrder(order);
            userRepository.save(user);

        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void removeOrderFromUser(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.deleteOrder(order);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Order> getOrdersByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(User::getOrders).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public User findByUserName(String name) {
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public List<User> findAllByDeleted(boolean deleted) {
        return userRepository.findByDeleted(deleted);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

}
