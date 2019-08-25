package boost.brain.youtube.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class HttpSessions {
    public static void main(String [] args){
        SpringApplication.run(HttpSessions.class, args);
    }
}
