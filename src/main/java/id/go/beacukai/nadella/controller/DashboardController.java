package id.go.beacukai.nadella.controller;

import id.go.beacukai.nadella.entity.User;
import id.go.beacukai.nadella.service.LogistikService;
import id.go.beacukai.nadella.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final LogistikService logistikService;
    private final UserService userService;
    
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication) {
        Map<String, Long> statistics = logistikService.getStatistics();
        
        // Get full name from username
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        String fullName = (user != null) ? user.getFullName() : username;
        
        model.addAttribute("totalImpor", statistics.get("totalImpor"));
        model.addAttribute("totalEkspor", statistics.get("totalEkspor"));
        model.addAttribute("jalurHijau", statistics.get("jalurHijau"));
        model.addAttribute("jalurKuning", statistics.get("jalurKuning"));
        model.addAttribute("jalurMerah", statistics.get("jalurMerah"));
        model.addAttribute("totalData", statistics.get("totalData"));
        model.addAttribute("username", username);
        model.addAttribute("fullName", fullName);
        
        return "dashboard";
    }
}
