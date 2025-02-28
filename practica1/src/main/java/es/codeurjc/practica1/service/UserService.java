package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }
	public List<User> findByIds(List<Long> ids){
		return userRepository.findAllById(ids);
	}

	public User save(User shop) {
		return userRepository.save(shop);
	}

    public List<User> findAllById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

}
