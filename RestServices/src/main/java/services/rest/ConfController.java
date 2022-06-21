package services.rest;

import Model.Conf;
import Persistence.ConfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/app/conf")
public class ConfController {
    private static final String template = "Hello, %s!";

    @Autowired
    private ConfRepository confRepository;

    @RequestMapping( method= RequestMethod.GET)
    public Iterable<Conf> getAll(){
        System.out.println("Get all confs ...");
        return confRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Conf create(@RequestBody Conf conf){
        return confRepository.saveConf(conf);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userError(ServiceException e) {
        return e.getMessage();
    }
}
