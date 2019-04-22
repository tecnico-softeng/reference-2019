package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData.CopyDepth;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BulkData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/bulks")
public class BulkController {
    private static final Logger logger = LoggerFactory.getLogger(AdventureController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String showBulks(Model model, @PathVariable String brokerCode) {
        logger.info("showBulks code:{}", brokerCode);

        BrokerData brokerData = BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS);

        if (brokerData == null) {
            model.addAttribute("error", "Error: it does not exist a broker with the code " + brokerCode);
            model.addAttribute("broker", new BrokerData());
            model.addAttribute("brokers", BrokerInterface.getBrokers());
            return "brokers";
        } else {
            model.addAttribute("bulk", new BulkData());
            model.addAttribute("broker", brokerData);
            return "bulks";
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitBulk(Model model, @PathVariable String brokerCode, @ModelAttribute BulkData bulkData) {
        logger.info("submitBulk brokerCode:{}, number:{}, arrival:{}, departure:{}, nif:{}, iban:{}", brokerCode,
                bulkData.getNumber(), bulkData.getArrival(), bulkData.getDeparture());

        try {
            BrokerInterface.createBulkRoomBooking(brokerCode, bulkData);
        } catch (BrokerException be) {
            model.addAttribute("error", "Error: it was not possible to create the bulk room booking");
            model.addAttribute("bulk", bulkData);
            model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
            return "bulks";
        }

        return "redirect:/brokers/" + brokerCode + "/bulks";
    }

    @RequestMapping(value = "/{bulkId}/process", method = RequestMethod.POST)
    public String processBulk(Model model, @PathVariable String brokerCode, @PathVariable String bulkId) {
        logger.info("processBulk brokerCode:{}, bulkId:{}, ", brokerCode, bulkId);

        try {
            BrokerInterface.processBulk(brokerCode, bulkId);
        } catch (BrokerException | HotelException | RemoteAccessException e) {
            model.addAttribute("error", "Error: it was not possible process the bulk booking " + bulkId);
            model.addAttribute("bulk", new BulkData());
            model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
            return "bulks";
        }

        return "redirect:/brokers/" + brokerCode + "/bulks";
    }

    @RequestMapping(value = "/{bulkId}/references/{reference}", method = RequestMethod.GET)
    public String viewReference(Model model, @PathVariable String brokerCode, @PathVariable String bulkId, @PathVariable String reference) {
        logger.info("viewReference brokerCode: {}, bulkId: {}, reference: {}", brokerCode, bulkId, reference);

        RoomBookingData roomBookingData;
        try {
            roomBookingData = BrokerInterface.getBulkBookedRoomBookingDataByReference(brokerCode, reference);
        } catch (BrokerException | HotelException | RemoteAccessException e) {
            model.addAttribute("error", "Error: it was not possible to view the reference " + reference);
            model.addAttribute("bulk", new BulkData());
            model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
            return "bulks";
        }

        model.addAttribute("brokerCode", brokerCode);
        model.addAttribute("bulkId", bulkId);
        model.addAttribute("roomBookingData", roomBookingData);
        return "roomBooking";
    }

    @RequestMapping(value = "/{bulkId}/references/{reference}/cancel", method = RequestMethod.POST)
    public String cancelBooking(Model model, @PathVariable String brokerCode, @PathVariable String bulkId,
                                @PathVariable String reference) {
        logger.info("cancelBooking brokerCode: {}, bulkId: {}, reference: {}", brokerCode, bulkId, reference);

        HotelInterface hotelInterface = new HotelInterface();

        try {
            hotelInterface.cancelBooking(reference);
        } catch (HotelException | RemoteAccessException e) {
            model.addAttribute("error", "Error: it was not possible to cancel the reference " + reference);
            model.addAttribute("bulk", new BulkData());
            model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS));
            return "bulks";
        }

        return "redirect:/brokers/" + brokerCode + "/bulks";
    }

}
