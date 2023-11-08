package comp.finalproject.user.controller.web;

import comp.finalproject.user.dto.UserDto;
import comp.finalproject.user.entity.Item;
import comp.finalproject.user.entity.User;
import comp.finalproject.user.repository.ItemRepository;
import comp.finalproject.user.repository.SalesRepository;
import comp.finalproject.user.repository.UserRepository;
import comp.finalproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SalesRepository salesRepository;

    public AuthController(UserService userService, UserRepository userRepository, ItemRepository itemRepository, SalesRepository salesRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.salesRepository = salesRepository;
    }

    // handler method to handle home page request
    @GetMapping("/")
    public String home(Model model) {
        List<Item> listItem = itemRepository.findTop4ByTotalSoldGreaterThanOrderByTotalSoldDesc(0);
        model.addAttribute("listItem", listItem);
        return "index";
    }

    // handler method to handle login request
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "auth/register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/auth/register";
        }

        userService.saveUser(userDto);
        return "redirect:/login?success";
    }

    // handler method to handle list of users
    /*@GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }*/
}
