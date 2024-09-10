package web.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {	
	@RequestMapping("/api/ping")
	public ResponseEntity<String> getUsers() {
		return new ResponseEntity<String>("pong", HttpStatus.OK);
	}
}
