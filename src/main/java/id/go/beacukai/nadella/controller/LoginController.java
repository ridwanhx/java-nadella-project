package id.go.beacukai.nadella.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Username atau password salah!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Anda telah berhasil logout.");
        }
        
        return "login";
    }
}
