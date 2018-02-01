package uk.gov.digital.ho.egar.workflow.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
class ThymeleafTemplateController {

	@RequestMapping("/")
    String root(HttpServletRequest request, Model model) {
        return getIndex(request, model);
    }

    @RequestMapping("/index.html")
    String getIndex(HttpServletRequest request, Model model) {
        return getDefaultTemplatePath(model, "index");
    }

    @RequestMapping("/api.html")
    String getApi(HttpServletRequest request, Model model) {
        return getDefaultTemplatePath(model, "api");
    }

    @RequestMapping("/help.html")
    String getHelp(HttpServletRequest request, Model model) {
        return getDefaultTemplatePath(model, "help");
    }

    @RequestMapping("/licence.html")
    String getLicence(HttpServletRequest request, Model model) {
        return getDefaultTemplatePath(model, "licence");
    }

	private String getDefaultTemplatePath(Model model, String templateName) {
        return templateName;
	}

}