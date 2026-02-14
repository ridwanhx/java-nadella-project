package id.go.beacukai.nadella.controller;

import id.go.beacukai.nadella.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    @GetMapping
    public String index() {
        return "chatbot/index";
    }
    
    @PostMapping("/message")
    @ResponseBody
    public Map<String, String> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response = chatbotService.getResponse(userMessage);
        
        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return result;
    }
}
