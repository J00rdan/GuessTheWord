package services.rest;

import Model.Conf;
import Model.GameDto;
import Persistence.ConfRepository;
import Persistence.GameDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/app/games")
public class GameDtoController {
    private static final String template = "Hello, %s!";

    @Autowired
    private GameDtoRepository gameDtoRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Iterable<GameDto> getById(@PathVariable String id){
        System.out.println("Get by id "+id);
        //System.out.println(arrivalRepository.findAllByCheckPoint(Integer.parseInt(id)));
        return gameDtoRepository.findByUsername(id);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userError(ServiceException e) {
        return e.getMessage();
    }
}