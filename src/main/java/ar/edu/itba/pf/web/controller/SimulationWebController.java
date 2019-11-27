package ar.edu.itba.pf.web.controller;

import ar.edu.itba.pf.web.domain.Simulation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/simulation")
public class SimulationWebController {

    @GetMapping("view")
    public ModelAndView view(@RequestParam(name = "simulationId") Simulation simulation){
        ModelAndView mav = new ModelAndView("simulation/view");
        mav.addObject("simulationId", simulation.getId());
        return mav;
    }
}
