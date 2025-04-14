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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByDeleted(boolean deleted) {
        return userRepository.findByDeleted(deleted);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> findAllById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User getLoggedUser(String name) {
        User user=userRepository.findByName(name).orElseThrow(()-> new UsernameNotFoundException("USER SERVICE LINE 44") );
        return user;
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
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

    //-------------------------------------------------------------------------------------------
    //Funciones API

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

	public User getLoggedUser2() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByName(username).get();
    }

    public UserDTO getLoggedUserDTO() {
        return toDTO(getLoggedUser2());
    }

    public List<UserDTO> getAll() {
        return toDTOs (userRepository.findAll());
    }

    public UserDTO findUserById(long id) {
        return mapper.toDTO(userRepository.findById(id).orElseThrow());
    }

    public List<UserDTO> findUsersByIds(List<Long> ids) {
        return mapper.toDTOs(userRepository.findAllById(ids));
    }

    public UserDTO findUserByName(String name) {
        return mapper.toDTO(userRepository.findByName(name).orElseThrow());
    }

    public UserDTO findUserByEmail(String email) {
        return mapper.toDTO(userRepository.findByEmail(email).orElseThrow());
    }

    public List<UserDTO> findDeletedUser(boolean deleted) {
        return mapper.toDTOs(userRepository.findByDeleted(deleted));
    }

    public UserDTO saveUser(UserDTO userDTO) {
        User user = toDomain(userDTO);
        userRepository.save(user);
        return mapper.toDTO(user);
    }

    public UserDTO deleteUser(UserDTO userDTO) {
        User user = toDomain(userDTO);
        user.setDeletedd(true);
        userRepository.save(user);
        userRepository.delete(user);
        return mapper.toDTO(user);
    }

    public UserDTO addOrderToUser(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            
            User user = userOptional.get();
            user.addAnOrder(order);
            userRepository.save(user);
            return mapper.toDTO(user);

        } else {
            throw new RuntimeException("User not found");
        }
    }

    public UserDTO removeAnOrderFromUser(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.deleteOrder(order);
            userRepository.save(user);
            return mapper.toDTO(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

}
