package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.domain.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "hola amigo";
	}
}
