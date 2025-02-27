package es.codeurjc.practica1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public String getUser(Model model, @PathVariable long id){
        Optional<User> user = userService.findById(id);
        if(user.isPresent()){
            model.addAttribute("user", user.get());
            return "Usuario";
        }else{
            return "Error";
        }
    }
    @GetMapping("/user/new")
    public String newUser(Model model){
        model.addAttribute("user", new User("Pepe","Pepe@gmail.com","123",1,1234));
        return "RegistrarUsuario";
    }
    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", userService.findAll());
        return "ListaUsuarios";
    }
}
