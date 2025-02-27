package es.codeurjc.practica1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @GetMapping("/product/{id}")
    public String getProduct(Model model, @PathVariable long id){
        Optional<Product> product = productService.findById(id);
        if(product.isPresent()){
            model.addAttribute("product", product.get());
            return "Producto";
        }else{
            return "Error";
        }
    }
    @GetMapping("/product/new")
    public String newProduct(Model model){
        model.addAttribute("product", new Product());
        return "RegistrarProducto";
    }

}
