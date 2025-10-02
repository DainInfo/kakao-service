package dain.web.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class TestController {	
	@RequestMapping("/api/ping")
	public ResponseEntity getUsers() {
		return new ResponseEntity<String>("pong", HttpStatus.OK);
	}
}
