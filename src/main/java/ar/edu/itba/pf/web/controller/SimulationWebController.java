package ar.edu.itba.pf.web.controller;

import ar.edu.itba.pf.web.domain.Simulation;
import ar.edu.itba.pf.web.repository.SimulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/simulation")
public class SimulationWebController {

    @Autowired
    private SimulationRepository simulationRepository;

    @GetMapping("view")
    public ModelAndView view(@RequestParam(name = "simulationId", required = false) Simulation simulation){
        ModelAndView mav = new ModelAndView("simulation/view");
        if (simulation == null){
            simulation = simulationRepository.findAll().stream().reduce((first, second) -> second).get();
        }
        mav.addObject("simulationId", simulation.getId());
        return mav;
    }
}
