package ar.edu.itba.pf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/simulation")
public class SimulationWebController {

    @GetMapping("view")
    public String view(){
        return "simulation/view";
    }
}
