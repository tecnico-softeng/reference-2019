package pt.ulisboa.tecnico.softeng.tax.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface;

@Controller
@RequestMapping(value = "/tax/balance")
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String presentGlobalBalance(Model model) {
        logger.info("presentGlobalBalance");
        model.addAttribute("balance", TaxInterface.getTaxBalanceData());
        return "balanceView";
    }
}
