package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;

@Controller
@RequestMapping(value = "/rentacars/rentacar/{code}/processor")
public class ProcessorController {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String showPendingRentings(Model model, @PathVariable String code) {
        logger.info("showPendingRentings code: {}", code);

        RentACarInterface rentACarInterface = new RentACarInterface();

        model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
        model.addAttribute("processor", rentACarInterface.getProcessorData(code));
        return "processorView";
    }

//    @RequestMapping(value = "/vehicle", method = RequestMethod.POST)
//    public String vehicleSubmit(Model model, @PathVariable String code, @ModelAttribute VehicleData vehicleData) {
//        logger.info("vehicleSubmit plate:{}, km:{}, price:{}, type:{}", vehicleData.getPlate(),
//                vehicleData.getKilometers(), vehicleData.getPrice(), vehicleData.getType());
//
//        RentACarInterface rentACarInterface = new RentACarInterface();
//
//        try {
//            rentACarInterface.createVehicle(code, vehicleData);
//        } catch (CarException be) {
//            model.addAttribute("error", "Error: it was not possible to create the Rent-A-Car");
//            model.addAttribute("rentacar", rentACarInterface.getRentACarData(code));
//            model.addAttribute("vehicle", vehicleData);
//            model.addAttribute("vehicles", rentACarInterface.getVehicles(code));
//            return "vehiclesView";
//        }
//
//        return "redirect:/rentacars/rentacar/" + code + "/vehicles";
//    }
}
